package questbot.commands

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.moshi.responseObject
import com.github.kittinunf.result.Result
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import jakarta.inject.Inject
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.*
import questbot.TombstoneAnalyzer
import questbot.api.CommandHandler

class AnalyzeCommand @Inject
constructor(private val moshi: Moshi, private val tombstoneAnalyzer: TombstoneAnalyzer) : CommandHandler {

    override val name: String = "analyze"

    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(
            name, "Analyze tombstones",
            arrayListOf(
                SlashCommandOption.create(
                    SlashCommandOptionType.SUB_COMMAND,
                    "versions",
                    "List supported versions in BS",
                    false
                ),
                SlashCommandOption.createWithOptions(
                    SlashCommandOptionType.SUB_COMMAND, "upload", "Upload a tombstone to analyze",
                    arrayListOf(
                        SlashCommandOption.createAttachmentOption("tombstone", "Analyze uploaded tombstone", true)
                    )
                )
            )
        )
    }

    override fun onCommandInvoke(command: ApplicationCommand, event: SlashCommandCreateEvent) {
        val interaction = event.slashCommandInteraction
        val versions = interaction.getOptionByName("versions")
        return when {
            versions.isPresent -> respondVersions(event)
            else -> analyzeTombstone(event)
        }
    }



    private fun analyzeTombstone(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val upload = slashCommandInteraction.getOptionByName("upload")
        val file = upload.flatMap { it.getArgumentAttachmentValueByName("tombstone") } // .getOptionByName()

        if (file.isEmpty) {
            slashCommandInteraction.createImmediateResponder().setContent("No file attached")
                .setFlags(MessageFlag.EPHEMERAL).respond()
            return
        }

        slashCommandInteraction.respondLater()

        val url = file.get().url

        val messageBuilder = MessageBuilder()

        tombstoneAnalyzer.analyze(file.get().fileName, url, messageBuilder).thenAccept {
            val interactionMessageBuilder = slashCommandInteraction.createFollowupMessageBuilder()

            interactionMessageBuilder.stringBuilder.append(it.messageBuilder.stringBuilder.toString())
            interactionMessageBuilder.addAttachment(it.fileData.encodeToByteArray(), it.fileName)

            interactionMessageBuilder.send()

        }
    }

    private fun respondVersions(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        slashCommandInteraction.respondLater()
        "https://analyzer.questmodding.com/api/versions"
            .httpGet()
            .responseObject<VersionResult> { _, _, result ->
                val createFollowupMessageBuilder = slashCommandInteraction.createFollowupMessageBuilder()
                when (result) {
                    is Result.Failure -> {
                        createFollowupMessageBuilder.setContent("Failure in retrieving version list")
                    }
                    is Result.Success -> {
                        val embed = EmbedBuilder()
                        embed.setTitle("Found versions!")
                        embed.setFooter("Version pulled from https://analyzer.questmodding.com/api/versions")

                        val data = result.value

                        embed.addField("Saber Beat", data.versions.reversed().joinToString(",    "))

                        createFollowupMessageBuilder.addEmbed(embed).send()

                    }
                }
            }
    }
}


@JsonClass(generateAdapter = true)
internal data class VersionResult(
    val versions: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VersionResult

        if (!versions.contentEquals(other.versions)) return false

        return true
    }

    override fun hashCode(): Int {
        return versions.contentHashCode()
    }
}

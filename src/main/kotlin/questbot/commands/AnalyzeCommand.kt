package questbot.commands

import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType
import questbot.api.CommandHandler
import javax.inject.Inject

class AnalyzeCommand @Inject
constructor(private val gson: Gson) : CommandHandler {
    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(
            "analyze", "Analyze tombstones",
            arrayListOf(
                SlashCommandOption.create(SlashCommandOptionType.SUB_COMMAND, "versions", "List supported versions in BS", false)
            )
        )
    }

    override fun onCommandInvoke(command: SlashCommand, event: SlashCommandCreateEvent) {
        val interaction = event.slashCommandInteraction
        val versions = interaction.getOptionByName("versions")
        if (versions.isPresent) return respondVersions(event)
    }

    private fun respondVersions(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        slashCommandInteraction.respondLater().thenAccept {

            "https://il2cpp-analyzer.herokuapp.com/api/versions"
                .httpGet()
                .responseObject<VersionResult>(gson) { request, response, result ->
                    val createFollowupMessageBuilder = slashCommandInteraction.createFollowupMessageBuilder()
                    when (result) {
                        is Result.Failure -> {
                            createFollowupMessageBuilder.setContent("Failure in retrieving version list")
                        }
                        is Result.Success -> {
                            val embed = EmbedBuilder()
                            embed.setTitle("Found versions!")
                            embed.setFooter("Version pulled from https://il2cpp-analyzer.herokuapp.com/api/versions")

                            val data = result.get()

                            embed.addField("Saber Beat", data.versions.reversed().joinToString(",    "))

                            createFollowupMessageBuilder.addEmbed(embed).send()

                        }
                        else -> {}
                    }
                }
        }
    }
}

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
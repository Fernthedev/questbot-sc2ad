package questbot.commands

import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType
import org.slf4j.Logger
import questbot.api.CommandHandler
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class AnalyzeCommand @Inject
constructor(private val gson: Gson, private val logger: Logger) : CommandHandler {
    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(
            "analyze", "Analyze tombstones",
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

    override fun onCommandInvoke(command: SlashCommand, event: SlashCommandCreateEvent) {
        val interaction = event.slashCommandInteraction
        val versions = interaction.getOptionByName("versions")
        return when {
            versions.isPresent -> respondVersions(event)
            else -> analyzeTombstone(event)
        }
    }

    private fun getVersion(stacktrace: String): String {
        return "1.23.0"
    }

    private fun analyzeTombstone(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val upload = slashCommandInteraction.getOptionByName("upload")
        val file = upload.flatMap { it.getOptionAttachmentValueByName("tombstone") } // .getOptionByName()

        if (file.isEmpty) {
            slashCommandInteraction.createImmediateResponder().setContent("No file attached").setFlags(MessageFlag.EPHEMERAL).respond()
            return
        }

        slashCommandInteraction.respondLater()

        val fileDataAsync = file.get().downloadAsByteArray().handle { t, u ->
            if (u != null) throw u

            return@handle t.decodeToString()
        }


        fileDataAsync.thenAccept { fileData ->
            "https://il2cpp-analyzer.herokuapp.com/api/analyze"
                .httpPost()
                .jsonBody(AnalyzeRequest(
                    version = getVersion(fileData),
                    stacktrace = fileData
                ), gson)
                .header(
                    "Accept" to "application/json")
                .header(
                    "Content-Type" to "application/json"
                )
                .allowRedirects(true)
                .responseObject<AnalyzeResult>(gson) { request, response, result ->
                    val createFollowupMessageBuilder = slashCommandInteraction.createFollowupMessageBuilder()
                    when (result) {
                        is Result.Failure -> {
                            logger.warn("Received error ${response.statusCode} ${response.responseMessage}")
                            logger.warn(result.error.toString())
                            logger.warn("${request.url} : ${request.body.asString("application/json")}")
                            createFollowupMessageBuilder.setContent("Failure in analyzing ${response.statusCode}").send()
                        }
                        is Result.Success -> {
                            val data = result.value

                            if (!data.success || data.stacktrace == null) {
                                logger.warn("Error analyzing ${data.error}")
                                createFollowupMessageBuilder.setContent("Failure in analyzing: ${data.error}").send()
                                return@responseObject
                            }

                            createFollowupMessageBuilder.addAttachment(
                                data.stacktrace.encodeToByteArray(),
                                "${file.get().fileName}_analyzed.cpp"
                            )

                            val backtraceStart = data.stacktrace.indexOf("backtrace:")
                            if (backtraceStart > -1) {
                                var backtraceEnd = max(data.stacktrace.indexOf("\nstack:"), backtraceStart + 700)
                                if (backtraceEnd < 0) backtraceEnd = backtraceStart + 700

                                backtraceEnd = min(backtraceEnd, data.stacktrace.length - 1)
                                val backtrace = data.stacktrace.substring(backtraceStart, backtraceEnd).trim().trimIndent().trimMargin()


                                val backtraceTrimmed = if (backtrace.length > 1800)  {
                                    backtrace.substring(0, 1800)
                                } else {
                                    backtrace
                                }

                                createFollowupMessageBuilder.appendCode("cpp", backtraceTrimmed)
                            }

                            createFollowupMessageBuilder.send()

                        }
                        else -> {}
                    }
                }
        }
    }

    private fun respondVersions(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        slashCommandInteraction.respondLater()
        "https://il2cpp-analyzer.herokuapp.com/api/versions"
            .httpGet()
            .responseObject<VersionResult>(gson) { _, _, result ->
                val createFollowupMessageBuilder = slashCommandInteraction.createFollowupMessageBuilder()
                when (result) {
                    is Result.Failure -> {
                        createFollowupMessageBuilder.setContent("Failure in retrieving version list")
                    }
                    is Result.Success -> {
                        val embed = EmbedBuilder()
                        embed.setTitle("Found versions!")
                        embed.setFooter("Version pulled from https://il2cpp-analyzer.herokuapp.com/api/versions")

                        val data = result.value

                        embed.addField("Saber Beat", data.versions.reversed().joinToString(",    "))

                        createFollowupMessageBuilder.addEmbed(embed).send()

                    }
                    else -> {}
                }
            }
    }
}

internal data class AnalyzeRequest(
    val version: String,
    val stacktrace: String
)
internal data class AnalyzeResult(
    val success: Boolean,
    val version: String?,
    val stacktrace: String?,
    val error: String?
) {}

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
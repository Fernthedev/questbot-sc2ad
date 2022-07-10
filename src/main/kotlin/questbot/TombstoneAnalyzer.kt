package questbot

import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import org.javacord.api.entity.message.MessageBuilder
import org.slf4j.Logger
import java.net.URL
import java.util.concurrent.CompletableFuture
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class TombstoneAnalyzer @Inject constructor(
    private val logger: Logger,
    private val gson: Gson
) {

    private val backtraceRegexWithBuildId = Regex("#\\d+ pc 0[\\da-fA-F]+ [a-zA-Z-/\\-.\\d=]+( \\(BuildId: ([a-zA-Z\\d]+)\\))?")
    private val buildId = Regex("( \\(BuildId: ([a-zA-Z\\d]+)\\))")
    private val directoryPrefix = Regex("/.+/")
    private val backtraceRegexWithoutBuildId = Regex("#\\d+ pc 0[\\da-fA-F]+[a-zA-Z-/\\-.\\d=]+(\\(BuildId: ([a-zA-Z\\d]+)\\))?")

    // automatically resolves the correct version from the BuildID
    private fun getVersion(): String {
        return "BuildID"
    }

    fun analyze(fileName: String, url: URL, messageBuilder: MessageBuilder): CompletableFuture<AnalyzeTombstoneResult> {
        val future = CompletableFuture<AnalyzeTombstoneResult>()
        "https://il2cpp-analyzer.herokuapp.com/api/analyze"
            .httpPost()
            .jsonBody(
                AnalyzeRequest(
                    version = getVersion(),
                    url = url.toExternalForm()
                ), gson
            )
            .header(
                "Accept" to "application/json"
            )
            .header(
                "Content-Type" to "application/json"
            )
            .allowRedirects(true)
            .responseObject<AnalyzeResult>(gson) { request, response, result ->
                run {
                    when (result) {
                        is Result.Failure -> {
                            logger.warn("Received error ${response.statusCode} ${response.responseMessage}")
                            logger.warn(result.error.toString())
                            logger.warn("${request.url} : ${request.body.asString("application/json")}")
                            messageBuilder.setContent("Failure in analyzing ${response.statusCode}")
                        }
                        is Result.Success -> {
                            val data = result.value

                            if (!data.success || data.stacktrace == null) {
                                logger.warn("Error analyzing ${data.error}")
                                messageBuilder.setContent("Failure in analyzing: ${data.error}")
                                return@run
                            }

                            val backtraceStart = data.stacktrace.indexOf("backtrace:")
                            if (backtraceStart > -1) {
                                var backtraceEnd = max(data.stacktrace.indexOf("\nstack:"), backtraceStart + 700)
                                if (backtraceEnd < 0) backtraceEnd = backtraceStart + 700

                                backtraceEnd = min(backtraceEnd, data.stacktrace.length - 1)

                                val backtrace =
                                    data.stacktrace.substring(backtraceStart, backtraceEnd).trim().trimIndent()
                                        .trimMargin()

                                val cleanBacktrace = backtrace.lines()
                                    .joinToString(separator = "\n") {
                                        it.replace(buildId, "")
                                        .replace(directoryPrefix, "")
                                    }


                                val backtraceTrimmed = if (cleanBacktrace.length > 1800) {
                                    cleanBacktrace.substring(0, 1800)
                                } else {
                                    cleanBacktrace
                                }

                                messageBuilder.appendCode("cpp", backtraceTrimmed)
                            }
                            future.complete(
                                AnalyzeTombstoneResult(
                                    messageBuilder = messageBuilder,
                                    fileData = data.stacktrace,
                                    fileName = "${fileName}_analyzed.cpp"
                                )
                            )

                            return@run
                        }
                    }


                    future.complete(
                        AnalyzeTombstoneResult(
                            messageBuilder = messageBuilder,
                            fileData = "",
                            fileName = ""
                        )
                    )
                }
            }

        return future
    }

}
internal data class AnalyzeRequest(
    val version: String,
    val url: String
)

internal data class AnalyzeResult(
    val success: Boolean,
    val version: String?,
    val stacktrace: String?,
    val error: String?
)

class AnalyzeTombstoneResult(
    val messageBuilder: MessageBuilder,
    val fileName: String,
    val fileData: String
)
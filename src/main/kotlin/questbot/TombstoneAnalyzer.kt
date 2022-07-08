package questbot

import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import org.javacord.api.entity.message.MessageBuilder
import org.slf4j.Logger
import questbot.commands.AnalyzeRequest
import questbot.commands.AnalyzeResult
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

    private fun getVersion(stacktrace: String): String {
        // TODO: Implement
        return "1.23.0"
    }

    fun analyze(fileName: String, fileData: String, messageBuilder: MessageBuilder): CompletableFuture<AnalyzeTombstoneResult> {
        val future = CompletableFuture<AnalyzeTombstoneResult>()
        "https://il2cpp-analyzer.herokuapp.com/api/analyze"
            .httpPost()
            .jsonBody(
                AnalyzeRequest(
                    version = getVersion(fileData),
                    stacktrace = fileData
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


                                val backtraceTrimmed = if (backtrace.length > 1800) {
                                    backtrace.substring(0, 1800)
                                } else {
                                    backtrace
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

class AnalyzeTombstoneResult(
    val messageBuilder: MessageBuilder,
    val fileName: String,
    val fileData: String
)
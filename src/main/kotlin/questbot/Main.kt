package questbot

import com.google.inject.Guice
import org.javacord.api.DiscordApiBuilder
import questbot.api.IBootstrap
import questbot.modules.MainModule
import java.io.File
import kotlin.system.measureTimeMillis


fun main() {
    val token = File("./token.txt").readText()

    val api = DiscordApiBuilder().setToken(token)
        .login().join()
    println(api.createBotInvite())

    val time = measureTimeMillis {
        val injector = Guice.createInjector(MainModule(api))

        injector.getInstance(IBootstrap::class.java).startup()
    }

    println("Took ${time}ms to finish startup")


}
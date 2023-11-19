package questbot

import com.google.inject.Guice
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.activity.ActivityType
import org.javacord.api.entity.intent.Intent
import questbot.api.IBootstrap
import questbot.modules.MainModule
import java.io.File
import kotlin.system.measureTimeMillis


fun main() {
    val token = File("./token.txt").readText()

    val api = DiscordApiBuilder().setToken(token)
        .addIntents(Intent.MESSAGE_CONTENT, Intent.GUILD_MEMBERS)
        .login().join()

    api.updateActivity(ActivityType.CUSTOM, "Finishing Flamingo🦩")
    println(api.createBotInvite())

    val time = measureTimeMillis {
        val injector = Guice.createInjector(MainModule(api))

        injector.getInstance(IBootstrap::class.java).startup()
    }

    println("Took ${time}ms to finish startup")
}
package questbot.api

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.ApplicationCommand
import org.javacord.api.interaction.SlashCommandBuilder

interface CommandHandler {

    val name: String

    fun buildCommand(): SlashCommandBuilder

    fun onCommandInvoke(command: ApplicationCommand, event: SlashCommandCreateEvent)

    val global: Boolean
        get() = true
}
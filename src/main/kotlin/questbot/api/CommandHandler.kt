package questbot.api

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder

interface CommandHandler {

    fun buildCommand(): SlashCommandBuilder

    fun onCommandInvoke(command: SlashCommand, event: SlashCommandCreateEvent)

    val global: Boolean
        get() = true
}
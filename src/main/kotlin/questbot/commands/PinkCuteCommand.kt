package questbot.commands

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import questbot.api.CommandHandler

class PinkCuteCommand : CommandHandler{
    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with("pinkcute", "reminds pink of her cuteness")
    }

    override fun onCommandInvoke(command: SlashCommand, event: SlashCommandCreateEvent) {
        event.slashCommandInteraction.createImmediateResponder().setContent("Pink cute").respond()
    }
}
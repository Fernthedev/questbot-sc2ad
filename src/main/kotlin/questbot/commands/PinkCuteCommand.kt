package questbot.commands

import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.ApplicationCommand
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import questbot.api.CommandHandler

class PinkCuteCommand : CommandHandler{
    override val name: String = "pinkcute"


    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(name, "reminds pink of her cuteness")
    }

    override fun onCommandInvoke(command: ApplicationCommand, event: SlashCommandCreateEvent) {
        event.slashCommandInteraction.createImmediateResponder().setContent("Pink cute").respond()
    }
}
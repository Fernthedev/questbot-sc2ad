package questbot.internal

import jakarta.inject.Inject
import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommandBuilder
import org.slf4j.Logger
import questbot.api.CommandHandler
import questbot.api.IBootstrap
import questbot.api.MessageListener
import questbot.reminders.ReminderManager
import java.util.concurrent.CompletableFuture

class Bootstrap
@Inject constructor(
    private val api: DiscordApi,
    private val messageListeners: Set<MessageListener>,
    private val commandHandlers: Set<CommandHandler>,
    private val logger: Logger,
    private val reminderManager: ReminderManager
) : IBootstrap {
    override fun startup() {
        println("meow")
        logger.info("vrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr")
        logger.info("starting up")
        addMessageListeners()
        addCommandHandlers()
        reminderManager.init()

        logger.info("Finished startup")
    }

    override fun shutdown(): CompletableFuture<Void> {
        return api.disconnect()
    }

    private fun addCommandHandlers() {
        // TODO: Use hashmap
        for (commandHandler in commandHandlers) {
            logger.info("Registering command class ${commandHandler.javaClass.simpleName}")


            val commandBuilder: SlashCommandBuilder = commandHandler.buildCommand()
            commandBuilder.createGlobal(api).whenComplete { command, _ ->
                api.addSlashCommandCreateListener { event ->
                    if (command == null) {
                        logger.info("Command is null")
                        return@addSlashCommandCreateListener
                    }

                    if (event.slashCommandInteraction.commandId == command.id) {
                        commandHandler.onCommandInvoke(command, event);
                    }
                }
            }

        }
    }

    private fun addMessageListeners() {
        for (messageListener in messageListeners) {
            logger.info("Registering listener class ${messageListener.javaClass.simpleName}")
            api.addMessageCreateListener(messageListener::handleCreatedMessage)
            api.addMessageDeleteListener(messageListener::handleDeletedMessage)
            api.addMessageEditListener(messageListener::handleEditedMessage)
        }
    }


}
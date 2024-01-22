package questbot.internal

import jakarta.inject.Inject
import org.javacord.api.DiscordApi
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

        val builders = commandHandlers.associateWith { commandHandler ->
            logger.info("Registering command class ${commandHandler.javaClass.simpleName}")


            commandHandler.buildCommand()
        }

        api.bulkOverwriteGlobalApplicationCommands(builders.values.toSet()).thenApply { commands ->
            api.addSlashCommandCreateListener { event ->
                val invokedCommandName = event.slashCommandInteraction.commandName

                val commandHandler = commandHandlers.find { it.name == invokedCommandName }
                val command = commands.find { it.name == invokedCommandName }
                if (command == null) {
                    logger.info("Command is null")
                    return@addSlashCommandCreateListener
                }
                if (commandHandler == null) {
                    logger.info("Command is null")
                    return@addSlashCommandCreateListener
                }



                commandHandler.onCommandInvoke(command, event);

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
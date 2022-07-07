package questbot.internal

import org.javacord.api.DiscordApi
import org.javacord.api.interaction.SlashCommandBuilder
import org.slf4j.Logger
import questbot.api.CommandHandler
import questbot.api.IBootstrap
import questbot.api.MessageListener
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class Bootstrap
@Inject constructor(
    private val api: DiscordApi,
    private val messageListeners: Set<MessageListener>,
    private val commandHandlers: Set<CommandHandler>,
    private val logger: Logger
) : IBootstrap {
    override fun startup() {
        println("meow")
        logger.info("vrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr")
        logger.info("starting up")
        addMessageListeners()
        addCommandHandlers()
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
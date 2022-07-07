package questbot.api

import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.event.message.MessageDeleteEvent
import org.javacord.api.event.message.MessageEditEvent

interface MessageListener {

    fun handleCreatedMessage(e: MessageCreateEvent) {}
    fun handleDeletedMessage(e: MessageDeleteEvent) {}
    fun handleEditedMessage(e: MessageEditEvent) {}

}
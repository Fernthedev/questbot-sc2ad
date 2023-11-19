package questbot.listeners

import jakarta.inject.Inject
import org.javacord.api.entity.message.Message
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.event.message.MessageEditEvent
import questbot.Emojis
import questbot.api.MessageListener

class FlamingoMessage
@Inject constructor(
    private val emojis: Emojis
): MessageListener {
    override fun handleCreatedMessage(e: MessageCreateEvent) {
        handleFlamingo(e.message)
    }

    override fun handleEditedMessage(e: MessageEditEvent) {
        handleFlamingo(e.message)
    }

    private fun handleFlamingo(message: Message) {
        if (message.content.contains("flamingo", ignoreCase = true)) {
            message.addReaction(emojis.flamingoEmoji)
        } else {
            message.removeOwnReactionByEmoji(emojis.flamingoEmoji)
        }
    }
}
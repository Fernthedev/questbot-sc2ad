package questbot.listeners

import org.javacord.api.event.message.MessageCreateEvent
import questbot.Emojis
import questbot.api.MessageListener
import javax.inject.Inject

class FlamingoMessage
@Inject constructor(
    private val emojis: Emojis
): MessageListener {
    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.contains("flamingo", ignoreCase = true)) return

        e.message.addReaction(emojis.flamingoEmoji)
    }
}
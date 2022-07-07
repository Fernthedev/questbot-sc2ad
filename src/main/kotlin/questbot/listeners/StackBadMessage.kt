package questbot.listeners

import org.javacord.api.event.message.MessageCreateEvent
import questbot.api.MessageListener

class StackBadMessage : MessageListener {

    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.equals("stack bad", ignoreCase = true)) return

        e.channel.sendMessage("agreed")
    }
}
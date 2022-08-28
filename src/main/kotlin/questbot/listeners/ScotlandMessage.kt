package questbot.listeners

import org.javacord.api.event.message.MessageCreateEvent
import questbot.api.MessageListener

class ScotlandMessage : MessageListener {
    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.equals("scotland", ignoreCase = true)) return
        if (e.message.author.isYourself) return;

        e.channel.sendMessage("SCOTLAND FOREVER")
    }
}
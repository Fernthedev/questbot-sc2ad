package questbot.listeners

import org.javacord.api.event.message.MessageCreateEvent
import questbot.api.MessageListener

class PinkCuteMessage : MessageListener {
    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.equals("pink cute", ignoreCase = true)) return
        if (e.message.author.isYourself) return;

        e.channel.sendMessage("Pink cute")
    }
}
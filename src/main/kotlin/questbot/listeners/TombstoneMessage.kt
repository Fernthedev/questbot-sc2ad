package questbot.listeners

import jakarta.inject.Inject
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.event.message.MessageCreateEvent
import questbot.TombstoneAnalyzer
import questbot.api.MessageListener

class TombstoneMessage
@Inject constructor(
    private val tombstoneAnalyzer: TombstoneAnalyzer
) : MessageListener {

    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (e.messageAuthor.isYourself) return

        val tombstones = e.messageAttachments.filter { it.fileName.startsWith("tombstone_") }
        if (tombstones.isEmpty()) return

        e.channel.sendMessage("Analyzing ${tombstones.joinToString(";") { it.fileName }}")

        for (tombstone in tombstones) {
            val url = tombstone.url
            val message = MessageBuilder()
            tombstoneAnalyzer.analyze(tombstone.fileName, url, message).thenAccept {
                message.addAttachment(it.fileData.encodeToByteArray(), it.fileName)
                message.send(e.channel)
            }
        }
    }
}
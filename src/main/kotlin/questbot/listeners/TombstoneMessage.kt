package questbot.listeners

import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.event.message.MessageCreateEvent
import questbot.TombstoneAnalyzer
import questbot.api.MessageListener
import javax.inject.Inject

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
            tombstone.downloadAsByteArray().thenAccept { fileData ->
                val message = MessageBuilder()
                tombstoneAnalyzer.analyze(tombstone.fileName, fileData.decodeToString(), message).thenAccept {
                    message.addAttachment(it.fileData.encodeToByteArray(), it.fileName)
                    message.send(e.channel)
                }
            }
        }
    }
}
package questbot.reminders

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import jakarta.inject.Inject
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.nio.file.Path
import java.util.*

class FileReminderStorage @Inject
constructor(
    private val moshi: Moshi
) : IReminderStorage {

    private val baseDirectory = Path.of("", "reminders").toAbsolutePath()

    private fun makeFolderIfNeeded() {
        val folder = baseDirectory.toFile()

        if (folder.exists() && folder.isDirectory) {
            return
        }
        folder.mkdirs()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun loadReminders(): Map<UUID, Reminder> {
        // Dir doesn't exist
        val folder = baseDirectory.toFile()

        if (!folder.exists()) {
            return mapOf()
        }

        return folder
            .listFiles()!!
            .mapNotNull { file ->
                moshi.adapter<Reminder>().fromJson(file.source().buffer())
            }
            .associateBy { it.uuid }.toMap()
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun addReminder(reminder: Reminder) {
        makeFolderIfNeeded()

        val file = File(baseDirectory.toFile(), "${reminder.uuid}.json")
        file.createNewFile()

        // write json
        moshi.adapter<Reminder>().toJson(file.sink(false).buffer(), reminder)
    }

    override fun removeReminder(uuid: UUID) {
        val file = File(baseDirectory.toFile(), "${uuid}.json")
        if (!file.exists()) return

        require (file.delete()) {"Unable to delete file $file"}
    }
}
package questbot.reminders

import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ScheduledFuture

data class Reminder(
    val userId: Long,
    val message: String,
    val time: LocalDateTime,
    val uuid: UUID = UUID.randomUUID(),
    @Transient
    var scheduledFuture: ScheduledFuture<*>? = null
)
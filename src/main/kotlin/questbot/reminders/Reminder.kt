package questbot.reminders

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ScheduledFuture

@JsonClass(generateAdapter = true)
data class Reminder(
    val userId: Long,
    val message: String,
    val time: LocalDateTime,
    val uuid: UUID = UUID.randomUUID(),
    @Transient
    var scheduledFuture: ScheduledFuture<*>? = null
)
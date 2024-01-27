package questbot.reminders

import jakarta.inject.Inject
import org.javacord.api.DiscordApi
import org.javacord.api.entity.user.User
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ReminderManager
@Inject constructor(private val storage: IReminderStorage, private val api: DiscordApi) {

    private var reminders: MutableMap<UUID, Reminder> = storage.loadReminders().toMutableMap()

    private val scheduler = Executors.newScheduledThreadPool(1)


    fun init() {
        val now = LocalDateTime.now()

        // prune expired reminders
        reminders.values.filter {
            it.time.isBefore(now)
        }.forEach { reminder ->
            removeReminder(null, reminder.uuid)
        }

        // schedule existing reminders
        reminders.values.forEach { reminder ->
            val delay = Duration.between(reminder.time, now)
            val task = scheduler.schedule({
                // get user, then notify
                api.getUserById(reminder.userId).thenApply { user ->
                    notifyUser(user, reminder)
                }
            }, delay.seconds, TimeUnit.SECONDS)

            reminder.scheduledFuture = task
        }
    }

    fun listReminders(user: User): Map<UUID, Reminder> {
        return reminders.filter { it.value.userId == user.id }
    }

    fun addReminder(user: User, message: String, delay: Duration): Reminder {
        // TODO: Reminder permission check?

        require(!delay.isNegative) { "Delay $delay is negative!" }


        val time = LocalDateTime.now().plus(delay)

        val reminder = Reminder(user.id, message, time)
        storage.addReminder(reminder)
        reminders[reminder.uuid] = reminder


        reminder.scheduledFuture = scheduler.schedule({
            notifyUser(user, reminder)
        }, delay.seconds, TimeUnit.SECONDS)

        return reminder
    }

    fun removeReminder(user: User?, uuid: UUID) {
        val reminder = reminders[uuid]

        requireNotNull(reminder) { "Reminder $uuid not found" }
        if (user != null) {
            require(reminder.userId == user.id) { "You cannot remove reminders you do not own" }
        }

        pruneReminder(reminder)
    }

    private fun pruneReminder(reminder: Reminder) {
        reminders.remove(reminder.uuid)
        storage.removeReminder(reminder)

        reminder.scheduledFuture?.cancel(false)
        reminder.scheduledFuture = null
    }

    private fun notifyUser(user: User, reminder: Reminder) {
        if (!reminders.containsKey(reminder.uuid)) return;

        user.sendMessage("Reminder ${reminder.uuid} expired today: ${reminder.time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
        user.sendMessage(reminder.message)

        reminder.scheduledFuture = null
        pruneReminder(reminder)
    }
}
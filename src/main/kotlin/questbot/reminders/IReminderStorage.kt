package questbot.reminders

import java.util.*

interface IReminderStorage {

   fun loadReminders(): Map<UUID, Reminder>

   fun addReminder(reminder: Reminder)

   fun removeReminder(uuid: UUID)

   fun removeReminder(reminder: Reminder) {
       removeReminder(reminder.uuid)
   }
}
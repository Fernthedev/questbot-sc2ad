package questbot.modules

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import questbot.reminders.FileReminderStorage
import questbot.reminders.IReminderStorage
import questbot.reminders.ReminderManager

class ReminderModule : AbstractModule() {

    override fun configure() {
        bind(IReminderStorage::class.java).to(FileReminderStorage::class.java)
        bind(ReminderManager::class.java).`in`(Scopes.SINGLETON)
    }
}
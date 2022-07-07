package questbot.modules

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.google.inject.multibindings.Multibinder
import org.reflections.Reflections
import org.reflections.scanners.Scanners.*
import questbot.api.*
import questbot.toType


class HandlerModule(private val reflections: Reflections) : AbstractModule() {
    /** Configures a [Binder] via the exposed methods.  */
    override fun configure() {
        // Bind all handlers
        val handler: Multibinder<MessageListener> = Multibinder.newSetBinder(binder(), MessageListener::class.java)
        val commandBinder: Multibinder<CommandHandler> = Multibinder.newSetBinder(binder(), CommandHandler::class.java)

        for (clazz in reflections.getSubTypesOf(MessageListener::class.java)) {
            handler.addBinding().toType(clazz.asSubclass(MessageListener::class.java)).`in`(Scopes.SINGLETON)
        }

        for (clazz in reflections.getSubTypesOf(CommandHandler::class.java)) {
            commandBinder.addBinding().toType(clazz.asSubclass(CommandHandler::class.java)).`in`(Scopes.SINGLETON)
        }
    }
}
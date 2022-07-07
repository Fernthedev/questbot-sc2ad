package questbot.modules

import com.google.inject.AbstractModule
import com.google.inject.MembersInjector
import com.google.inject.TypeLiteral
import com.google.inject.matcher.Matchers
import com.google.inject.spi.TypeEncounter
import com.google.inject.spi.TypeListener
import com.google.inject.util.Providers
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Field


class LoggerModule : AbstractModule() {
    /** Configures a [Binder] via the exposed methods.  */
    override fun configure() {
        bindListener(Matchers.any(), SLF4J())
        bind(Logger::class.java).toProvider(Providers.of(LoggerFactory.getLogger("QuestBot")))
    }
}



internal class SLF4J : TypeListener {
    override fun <T> hear(typeLiteral: TypeLiteral<T>, typeEncounter: TypeEncounter<T>) {
        var clazz: Class<*>? = typeLiteral.rawType
        while (clazz != null) {
            for (field in clazz.declaredFields) {
                if (field.type === Logger::class.java) {
                    typeEncounter.register(SLF4JMembersInjector<T>(field))
                }
            }
            clazz = clazz.superclass
        }
    }
}

internal class SLF4JMembersInjector<T>(private val field: Field) : MembersInjector<T> {
    private val logger = LoggerFactory.getLogger(field.declaringClass)

    init {
        field.isAccessible = true
    }

    override fun injectMembers(t: T) {
        try {
            field.set(t, logger)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }
}
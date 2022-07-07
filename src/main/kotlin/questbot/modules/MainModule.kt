package questbot.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.util.Providers
import org.javacord.api.DiscordApi
import org.reflections.Reflections

class MainModule(private val api: DiscordApi) : AbstractModule() {

    /** Configures a [Binder] via the exposed methods.  */
    override fun configure() {
        val reflections = Reflections("questbot");
        bind(Reflections::class.java).toProvider(Providers.of(reflections))

        install(LoggerModule())
        install(HandlerModule(reflections))
        install(BootstrapModule())
    }


    @Provides
    fun getDiscordAPI(): DiscordApi {
        return api
    }
}

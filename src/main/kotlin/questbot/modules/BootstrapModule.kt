package questbot.modules

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import questbot.api.IBootstrap
import questbot.internal.Bootstrap

class BootstrapModule : AbstractModule() {

    /** Configures a [Binder] via the exposed methods.  */
    override fun configure() {
        bind(IBootstrap::class.java).to(Bootstrap::class.java).`in`(Scopes.SINGLETON)
    }
}
package questbot.modules

import com.google.inject.AbstractModule
import com.squareup.moshi.Moshi

class MoshiModule : AbstractModule() {
    override fun configure() {
        bind(Moshi::class.java).toInstance(Moshi.Builder().build())
    }
}
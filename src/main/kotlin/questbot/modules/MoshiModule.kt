package questbot.modules

import com.google.inject.AbstractModule
import com.squareup.moshi.Moshi
import questbot.moshi.MoshiLocalDateTimeAdapter
import questbot.moshi.MoshiUUIDAdapter

class MoshiModule : AbstractModule() {
    override fun configure() {
        bind(Moshi::class.java).toInstance(
            Moshi.Builder()
                .add(MoshiLocalDateTimeAdapter())
                .add(MoshiUUIDAdapter())
                .build()
        )
    }
}
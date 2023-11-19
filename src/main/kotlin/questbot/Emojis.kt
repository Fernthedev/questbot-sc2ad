package questbot

import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.javacord.api.DiscordApi
import org.javacord.api.entity.emoji.KnownCustomEmoji

@Singleton
class Emojis
@Inject constructor(
    private val api: DiscordApi
) {

    val flamingoEmoji: KnownCustomEmoji by lazy {
        api.getCustomEmojisByName("flamingo").first()
    }


    val flamingoMetaEmoji: KnownCustomEmoji by lazy {
        api.getCustomEmojisByName("metaflamingo").first()
    }

}
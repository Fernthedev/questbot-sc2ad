package questbot

import org.javacord.api.DiscordApi
import org.javacord.api.entity.emoji.KnownCustomEmoji
import javax.inject.Inject
import javax.inject.Singleton

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
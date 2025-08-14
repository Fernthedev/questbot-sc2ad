package questbot.internal

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import org.javacord.api.DiscordApi
import org.javacord.api.audio.AudioSource
import org.javacord.api.audio.AudioSourceBase


class LavaplayerAudioSource(api: DiscordApi, audioPlayer: AudioPlayer) : AudioSourceBase(api) {
    private val audioPlayer: AudioPlayer
    private var lastFrame: AudioFrame? = null

    /**
     * Creates a new lavaplayer audio source.
     *
     * @param api A discord api instance.
     * @param audioPlayer An audio player from Lavaplayer.
     */
    init {
        this.audioPlayer = audioPlayer
    }

    override fun getNextFrame(): ByteArray? {
        if (lastFrame == null) {
            return null
        }
        return applyTransformers(lastFrame!!.data)
    }

    override fun hasFinished(): Boolean {
        return false
    }

    override fun hasNextFrame(): Boolean {
        lastFrame = audioPlayer.provide()
        return lastFrame != null
    }

    override fun copy(): AudioSource {
        return LavaplayerAudioSource(api, audioPlayer)
    }
}

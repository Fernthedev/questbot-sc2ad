package questbot.listeners

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.javacord.api.event.message.MessageCreateEvent
import questbot.api.MessageListener
import questbot.internal.LavaplayerAudioSource
import kotlin.jvm.optionals.getOrNull


class ScotlandMessage : MessageListener {
    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.equals("scotland", ignoreCase = true)) return
        if (e.message.author.isYourself) return;

        e.channel.sendMessage("SCOTLAND FOREVER")


        // https://pixabay.com/sound-effects/bp-samples-demo-27401/
        // Bagpipes
        val voiceChannel = e.messageAuthor.connectedVoiceChannel.getOrNull() ?: return

        val connection = voiceChannel.connect().join()
        if (connection.isSelfMuted) return

        val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
        playerManager.registerSourceManager(LocalAudioSourceManager())
        val player = playerManager.createPlayer()

        connection.setAudioSource(LavaplayerAudioSource(e.api, player))

        playerManager.loadItem("local: ./bagpipes.mp3", object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                player.playTrack(track)
            }
            override fun playlistLoaded(playlist: AudioPlaylist) {
                player.playTrack(playlist.tracks.first())
            }
            override fun noMatches() {
                e.channel.sendMessage("Audio file not found.")
            }
            override fun loadFailed(exception: FriendlyException) {
                e.channel.sendMessage("Failed to load audio: ${exception.message}")
            }
        })
    }
}
package questbot.listeners

import org.javacord.api.event.message.MessageCreateEvent
import questbot.api.MessageListener

class WhatDoesTheFishSay : MessageListener {

    private val fishRegex = Regex("what does the (\\w+) say\\?")

    override fun handleCreatedMessage(e: MessageCreateEvent) {
        if (!e.messageContent.lowercase().matches(fishRegex)) return

        val target = fishRegex.find(e.messageContent.lowercase())?.groups?.get(1)?.value ?: return

        val message = when (target.lowercase()) {
            "fish" -> "blup blup blup"
            "flamingo" -> "wen wen wen wen"
            "questkid" -> "noodle wen"
            "quest" -> "SIG_ABRT pc 0"
            "stack" -> "bad"
            "fern" -> "this is so stupid"
            "pink" -> "pink cute"
            else -> return
        }

        e.message.reply(message)
    }
}
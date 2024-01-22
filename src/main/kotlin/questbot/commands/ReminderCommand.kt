package questbot.commands

import jakarta.inject.Inject
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.SlashCommand
import org.javacord.api.interaction.SlashCommandBuilder
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionType
import questbot.api.CommandHandler
import questbot.reminders.ReminderManager
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class ReminderCommand @Inject
constructor(private val reminderManager: ReminderManager) : CommandHandler {
    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(
            "reminder", "Add reminder",
            arrayListOf(
                SlashCommandOption.create(
                    SlashCommandOptionType.SUB_COMMAND,
                    "list",
                    "List reminders",
                    false
                ),
                SlashCommandOption.createWithOptions(
                    SlashCommandOptionType.SUB_COMMAND, "add", "Add a reminder",
                    arrayListOf(
                        SlashCommandOption.createWithOptions(
                            SlashCommandOptionType.STRING,
                            "amountType",
                            "Type of time",
                            ChronoUnit.entries.map {
                                SlashCommandOption.createStringOption(
                                    it.toString(),
                                    "",
                                    true,
                                    true
                                )
                            }
                        ),
                        SlashCommandOption.create(SlashCommandOptionType.LONG, "amount", "Amount of time", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "Message", true),
                    )
                ),
                SlashCommandOption.createWithOptions(
                    SlashCommandOptionType.SUB_COMMAND, "remove", "Remove a reminder",
                    arrayListOf(
                        SlashCommandOption.create(
                            SlashCommandOptionType.STRING,
                            "uuid",
                            "UUID of reminder",
                            true
                        ),
                    )
                )
            )
        )
    }

    override fun onCommandInvoke(command: SlashCommand, event: SlashCommandCreateEvent) {
        val interaction = event.slashCommandInteraction
        val list = interaction.getOptionByName("list")
        val add = interaction.getOptionByName("add")
        val remove = interaction.getOptionByName("remove")
        return when {
            list.isPresent -> listReminders(event)
            add.isPresent -> addReminder(event)
            remove.isPresent -> removeReminder(event)
            else -> {}
        }
    }

    private fun listReminders(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val content = reminderManager.listReminders(slashCommandInteraction.user).map {
            "${it.key}  -> ${it.value.message} (${it.value.time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}) "
        }.joinToString("\n")

        slashCommandInteraction.createFollowupMessageBuilder()
            .setContent(content)
            .send()
    }

    private fun addReminder(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val messageBuilder = slashCommandInteraction.createFollowupMessageBuilder()

        val add = slashCommandInteraction.getOptionByName("add")

        try {
            val msg = add.flatMap { it.getArgumentStringValueByName("message") }
            val amount = add.flatMap { it.getArgumentLongValueByName("message") }
            val type =
                add.flatMap { it.getArgumentStringValueByName("amountType") }.map { ChronoUnit.valueOf(it.uppercase()) }

            reminderManager.addReminder(slashCommandInteraction.user, msg.get(), Duration.of(amount.get(), type.get()))
        } catch (e: Exception) {
            messageBuilder.setContent("Suffered error: ${e.message}")
            messageBuilder.send()
        }
    }

    private fun removeReminder(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val messageBuilder = slashCommandInteraction.createFollowupMessageBuilder()
        messageBuilder.setFlags(MessageFlag.LOADING)
        messageBuilder.send()

        val upload = slashCommandInteraction.getOptionByName("remove")
        val uuid = upload.flatMap { it.getArgumentStringValueByName("uuid") }

        try {
            reminderManager.removeReminder(slashCommandInteraction.user, UUID.fromString(uuid.get()))
        } catch (e: Exception) {
            messageBuilder.setFlags()
            messageBuilder.setContent("Suffered error: ${e.message}")
            messageBuilder.send()
        }
    }
}



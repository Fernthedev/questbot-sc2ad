package questbot.commands

import jakarta.inject.Inject
import org.javacord.api.event.interaction.SlashCommandCreateEvent
import org.javacord.api.interaction.*
import questbot.api.CommandHandler
import questbot.reminders.ReminderManager
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class ReminderCommand @Inject constructor(private val reminderManager: ReminderManager) : CommandHandler {

    override val name: String = "reminder"

    override fun buildCommand(): SlashCommandBuilder {
        return SlashCommand.with(
            name, "Add reminder", arrayListOf(
                SlashCommandOption.create(
                    SlashCommandOptionType.SUB_COMMAND, "list", "List reminders", false
                ),
                SlashCommandOption.createWithOptions(
                    SlashCommandOptionType.SUB_COMMAND,
                    "add",
                    "Add a reminder",
                    arrayListOf(
                        SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING,
                            "amountType",
                            "Type of time",
                            true,
                            ChronoUnit.entries.map {
                                SlashCommandOptionChoice.create(
                                    it.toString(), it.toString()
                                )
                            }),
                        SlashCommandOption.create(SlashCommandOptionType.LONG, "amount", "Amount of time", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "message", "Message", true),
                    )
                ),
                SlashCommandOption.createWithOptions(
                    SlashCommandOptionType.SUB_COMMAND, "remove", "Remove a reminder", arrayListOf(
                        SlashCommandOption.create(
                            SlashCommandOptionType.STRING, "uuid", "UUID of reminder", true
                        ),
                    )
                )
            )
        )
    }

    override fun onCommandInvoke(command: ApplicationCommand, event: SlashCommandCreateEvent) {
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

        slashCommandInteraction.createImmediateResponder().setContent(content).respond()
    }

    private fun addReminder(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val messageBuilder = slashCommandInteraction.createImmediateResponder()

        val add = slashCommandInteraction.getOptionByName("add")

        try {
            val msg = add.flatMap { it.getArgumentStringValueByName("message") }.get()
            val amount = add.flatMap { it.getArgumentLongValueByName("amount") }.get()
            val type =
                add.flatMap { it.getArgumentStringValueByName("amountType") }.map { ChronoUnit.valueOf(it.uppercase()) }
                    .get()

            val reminder = reminderManager.addReminder(slashCommandInteraction.user, msg, Duration.of(amount, type))
            messageBuilder.setContent("Created reminder $msg for ${reminder.time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}")
            messageBuilder.respond()
        } catch (e: Exception) {
            messageBuilder.setContent("Suffered error: ${e.message}")
            messageBuilder.respond()
        }
    }

    private fun removeReminder(event: SlashCommandCreateEvent) {
        val slashCommandInteraction = event.slashCommandInteraction

        val messageBuilder = slashCommandInteraction.createImmediateResponder()

        val upload = slashCommandInteraction.getOptionByName("remove")
        val uuid = upload.flatMap { it.getArgumentStringValueByName("uuid") }.get()

        try {
            reminderManager.removeReminder(slashCommandInteraction.user, UUID.fromString(uuid))
            messageBuilder.setContent("Removed reminder $uuid")
            messageBuilder.respond()
        } catch (e: Exception) {
            messageBuilder.setContent("Suffered error: ${e.message}")
            messageBuilder.respond()
        }
    }
}



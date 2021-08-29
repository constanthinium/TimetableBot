import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TimetableBot(token: String) : TelegramBot(token) {
    private val commands = mapOf<String, (MutableMap<Long, String>, Long, List<String>) -> String>(
        "group" to { groups, chatId, args ->
            if (args.size > 1) {
                val group = args[1]
                if (Timetable.groupExists(group)) {
                    groups[chatId] = group
                    GroupsData.saveGroups()
                    "Группа чата изменена на $group"
                } else "Такой группы не существует"
            } else "Укажите группу чата в аргументе"
        }, "today" to { groups, chatId, _ ->
            groups[chatId]?.let { Timetable.get(it) } ?: "Сначала укажите группу чата"
        }, "tomorrow" to { groups, chatId, _ ->
            groups[chatId]?.let { Timetable.get(it, true) } ?: "Сначала укажите группу чата"
        }
    )

    fun run() {
        messageListener = { chatId, message ->
            log("Message received: $message")
            val args = message.split(' ')
            val firstWord = args.first()
            if (firstWord.startsWith('/')) {
                commands[firstWord.replaceFirst("/", "")]
                    ?.let { sendMessage(chatId, it.invoke(GroupsData.groups, chatId, args)) }
            }
        }

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
            {
                GroupsData.groups.forEach { (chatId, group) ->
                    sendMessage(chatId, Timetable.get(group, true))
                }
            },
            Calendar.getInstance().apply {
                if (get(Calendar.HOUR_OF_DAY) >= HOUR) {
                    add(Calendar.DATE, 1)
                }
                set(Calendar.HOUR_OF_DAY, HOUR)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis - Date().time,
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.MILLISECONDS
        )

        receiveMessages()
    }

    companion object {
        const val HOUR = 22
    }
}
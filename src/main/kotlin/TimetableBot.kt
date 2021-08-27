import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

object TimetableBot {
    private val commands = mapOf<String, (MutableMap<Long, String>, Long, List<String>) -> String>(
        "group" to { groups, chatId, args ->
            if (args.size > 1) {
                val group = args[1]
                groups[chatId] = group
                GroupsData.saveGroups(groups)
                "Группа чата изменена на $group"
            } else {
                "Укажите группу чата в аргументе"
            }
        }, "today" to { groups, chatId, _ ->
            groups[chatId]?.let { Timetable.get(it) } ?: "Сначала укажите группу чата"
        }, "tomorrow" to { groups, chatId, _ ->
            groups[chatId]?.let { Timetable.get(it, true) } ?: "Сначала укажите группу чата"
        }
    )

    fun run() {
        val props = Properties()
        props.load(FileInputStream("gradle.properties"))
        val token = props.getProperty("token")

        val bot = TelegramBot(token)
        val groups = GroupsData.loadGroups()

        bot.messageListener = { chatId, message ->
            val args = message.split(' ')
            val firstWord = args.first()
            if (firstWord.startsWith('/')) {
                commands[firstWord.replaceFirst("/", "")]
                    ?.let { bot.sendMessage(chatId, it.invoke(groups, chatId, args)) }
            }
        }

        bot.receiveMessages()
    }
}
import java.io.FileInputStream
import java.util.*

object TimetableBot {
    fun run() {
        val props = Properties()
        props.load(FileInputStream("gradle.properties"))
        val token = props.getProperty("token")

        val bot = TelegramBot(token)
        val groups = GroupsData.loadGroups()

        bot.messageListener = { chatId, message ->
            if (message.startsWith("/group")) {
                val split = message.split(' ')
                if (split.size > 1) {
                    val group = split[1]
                    groups[chatId] = group
                    GroupsData.saveGroups(groups)
                    bot.sendMessage(chatId, "Группа чата изменена на $group")
                } else {
                    bot.sendMessage(chatId, "Укажите группу чата в аргументе")
                }
            } else if (message == "/today") {
                groups[chatId]?.let {
                    bot.sendMessage(chatId, Timetable.get(it))
                } ?: bot.sendMessage(chatId, "Сначала укажите группу чата")
            } else if (message == "/tomorrow") {
                groups[chatId]?.let {
                    bot.sendMessage(chatId, Timetable.get(it, true))
                } ?: bot.sendMessage(chatId, "Сначала укажите группу чата")
            }
        }

        bot.receiveMessages()
    }
}
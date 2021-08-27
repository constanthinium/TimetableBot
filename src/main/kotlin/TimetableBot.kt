import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.util.*

object TimetableBot {
    object Keys {
        const val CHAT_ID = "chat_id"
        const val GROUP = "group"
    }

    private const val GROUPS_PATH = "groups.json"

    fun run() {
        val props = Properties()
        props.load(FileInputStream("gradle.properties"))
        val token = props.getProperty("token")

        val bot = TelegramBot(token)
        val groups = loadGroups()

        bot.messageListener = { chatId, message ->
            if (message.startsWith("/group")) {
                val split = message.split(' ')
                if (split.size > 1) {
                    val group = split[1]
                    groups[chatId] = group
                    saveGroups(groups)
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

    private fun saveGroups(groups: Map<Long, String>) {
        val groupsJson = JSONArray()
        groups.forEach {
            val entryJson = JSONObject()
            entryJson.put(Keys.CHAT_ID, it.key)
            entryJson.put(Keys.GROUP, it.value)
            groupsJson.put(entryJson)
        }
        File(GROUPS_PATH).writeText(groupsJson.toString(4))
    }

    private fun loadGroups(): MutableMap<Long, String> {
        val file = File(GROUPS_PATH)
        return if (!file.exists()) {
            mutableMapOf()
        } else {
            val json = file.readText()
            val groups = JSONArray(json)
            val result = mutableMapOf<Long, String>()
            groups.forEach {
                val entryJson = it as JSONObject
                val key = entryJson.getLong(Keys.CHAT_ID)
                val value = entryJson.getString(Keys.GROUP)
                result[key] = value
            }
            result
        }
    }
}
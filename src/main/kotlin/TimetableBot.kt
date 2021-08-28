class TimetableBot(token: String) : TelegramBot(token) {
    private val commands = mapOf<String, (MutableMap<Long, String>, Long, List<String>) -> String>(
        "group" to { groups, chatId, args ->
            if (args.size > 1) {
                val group = args[1]
                if (Timetable.groupExists(group)) {
                    groups[chatId] = group
                    GroupsData.saveGroups(groups)
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
        val groups = GroupsData.loadGroups()

        messageListener = { chatId, message ->
            Companion.log("Message received: $message")
            val args = message.split(' ')
            val firstWord = args.first()
            if (firstWord.startsWith('/')) {
                commands[firstWord.replaceFirst("/", "")]
                    ?.let { sendMessage(chatId, it.invoke(groups, chatId, args)) }
            }
        }

        receiveMessages()
    }
}
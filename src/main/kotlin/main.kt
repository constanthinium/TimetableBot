import java.io.FileInputStream
import java.util.*

fun main() {
    val props = Properties()
    props.load(FileInputStream("gradle.properties"))
    val token = props.getProperty("token")
    val bot = TelegramBot(token)
    bot.messageListener = { chatId: Int, text: String -> bot.sendMessage(chatId, text) }
    bot.receiveMessages()
}
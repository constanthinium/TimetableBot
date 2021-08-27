import java.io.FileInputStream
import java.util.*

fun main() {
    val props = Properties()
    props.load(FileInputStream("gradle.properties"))
    val token = props.getProperty("token")
    val bot = TelegramBot(token)
    bot.messageListener = { _: Int, text: String -> println("Message received: $text") }
    bot.receiveMessages()
}
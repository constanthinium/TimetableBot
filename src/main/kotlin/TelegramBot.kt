import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

open class TelegramBot(token: String) {
    var messageListener: ((Long, String) -> Unit)? = null
    private val tokenizedUrl = "https://api.telegram.org/bot$token"

    fun sendMessage(chatId: Long, text: String) {
        val url = "$tokenizedUrl/sendMessage"
        val query = "chat_id=$chatId&text=${URLEncoder.encode(text, Charsets.UTF_8)}"
        URL("$url?$query").openStream()
    }

    fun receiveMessages() {
        var offset = 0

        while (true) {
            try {
                val connection = URL("$tokenizedUrl/getUpdates?offset=$offset&timeout=$TIMEOUT").openConnection()
                connection.readTimeout = TIMEOUT * 1000
                val json = connection.getInputStream().bufferedReader().readText()
                val result = JSONObject(json).getJSONArray("result")
                if (!result.isEmpty) {
                    val update = result.getJSONObject(0)
                    offset = update.getInt("update_id") + 1
                    if (update.has("message")) {
                        val message = update.getJSONObject("message")
                        val chatId = message.getJSONObject("chat").getLong("id")
                        if (message.has("text")) {
                            val text = message.getString("text")
                            messageListener?.invoke(chatId, text)
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                log(e.message)
            }
        }
    }

    companion object {
        const val TIMEOUT = 60
        private val timeFormat = SimpleDateFormat("HH:mm:ss")
        fun log(message: String?) {
            println(timeFormat.format(Date()) + ": " + message)
        }
    }
}
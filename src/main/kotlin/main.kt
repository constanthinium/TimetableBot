import java.io.FileInputStream
import java.util.*

fun main() {
    TimetableBot(Properties().run {
        load(FileInputStream("gradle.properties"))
        getProperty("token")
    }).run()
}
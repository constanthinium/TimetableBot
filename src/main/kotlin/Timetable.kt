import org.jsoup.Jsoup
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Timetable {
    private const val CIRCLE_RED = "🔴"
    private const val CIRCLE_BLUE = "🔵"

    fun get(group: String, nextDay: Boolean = false): String {
        val calendar = Calendar.getInstance()

        if (nextDay) {
            calendar.add(Calendar.DATE, 1)
        }

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek == Calendar.SUNDAY) {
            return "В воскресенье нет пар"
        }

        val weekIsEven = calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0
        val doc = getDoc()
        val dayTrs = doc.select("h3:contains($group) + table > thead:eq(${dayOfWeek - 1}) tr")
        val lessons = dayTrs.drop(2).dropLast(if (dayOfWeek != Calendar.SATURDAY) 1 else 0)
        val dayOfWeekName = SimpleDateFormat("EEEE").format(calendar.time)
        val building = dayTrs.first()?.select("span")?.text()

        return buildString {
            append(group, ", ")
            append(dayOfWeekName, ", ")
            append(building?.let { if (it.isNotEmpty()) it.replace(Regex("[()]"), "") + ", " else "" })
            append(if (weekIsEven) "числитель $CIRCLE_RED" else "знаменатель $CIRCLE_BLUE", "\n\n")

            lessons.forEach {
                append(it.child(0).text(), ". ")

                val lesson = it.child(1)
                val teacherElem = it.child(2)

                if (lesson.childrenSize() == 0) {
                    appendLine(lesson.text())
                    val teacher = teacherElem.text()
                    if (teacher.isNotEmpty()) {
                        append(teacher, "\n\n")
                    }
                } else {
                    val (elemIndex, circle) = if (weekIsEven) {
                        0 to CIRCLE_RED
                    } else {
                        2 to CIRCLE_BLUE
                    }

                    appendLine(lesson.child(elemIndex).text())
                    val teacher = teacherElem.child(elemIndex).text()
                    if (teacher.isNotEmpty()) {
                        append(teacher, " $circle\n\n")
                    }
                }
            }
        }
    }

    fun groupExists(group: String): Boolean {
        val doc = getDoc()
        val groups = doc.select("h3").map { it.text().replaceFirst("Группа ", "") }
        return groups.contains(group)
    }

    private fun getDoc() =
        Jsoup.parse(File("${System.getProperty("user.home")}/Downloads/Расписание занятий.html"), null)
}
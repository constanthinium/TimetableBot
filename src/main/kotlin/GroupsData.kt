import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.nio.file.Files
import java.nio.file.Path

object GroupsData {
    private val groupsPath = Path.of("groups.json")

    private const val CHAT_ID = "chat_id"
    private const val GROUP = "group"

    fun saveGroups() {
        val groupsJson = JSONArray()
        groups.forEach {
            val entryJson = JSONObject()
            entryJson.put(CHAT_ID, it.key)
            entryJson.put(GROUP, it.value)
            groupsJson.put(entryJson)
        }
        Files.writeString(groupsPath, groupsJson.toString(4))
    }

    val groups: MutableMap<Long, String> by lazy {
        try {
            val json = Files.readString(groupsPath)
            val groupsJson = JSONArray(json)
            val result = mutableMapOf<Long, String>()
            groupsJson.forEach {
                val entryJson = it as JSONObject
                val key = entryJson.getLong(CHAT_ID)
                val value = entryJson.getString(GROUP)
                result[key] = value
            }
            result
        } catch (ex: Exception) {
            if (ex is FileNotFoundException || ex is JSONException) {
                mutableMapOf()
            } else throw ex
        }
    }
}
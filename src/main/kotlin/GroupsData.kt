import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object GroupsData {

    private const val GROUPS_PATH = "groups.json"
    private const val CHAT_ID = "chat_id"
    private const val GROUP = "group"

    fun saveGroups(groups: Map<Long, String>) {
        val groupsJson = JSONArray()
        groups.forEach {
            val entryJson = JSONObject()
            entryJson.put(CHAT_ID, it.key)
            entryJson.put(GROUP, it.value)
            groupsJson.put(entryJson)
        }
        File(GROUPS_PATH).writeText(groupsJson.toString(4))
    }

    fun loadGroups(): MutableMap<Long, String> {
        val file = File(GROUPS_PATH)
        return if (!file.exists()) {
            mutableMapOf()
        } else {
            val json = file.readText()
            val groups = JSONArray(json)
            val result = mutableMapOf<Long, String>()
            groups.forEach {
                val entryJson = it as JSONObject
                val key = entryJson.getLong(CHAT_ID)
                val value = entryJson.getString(GROUP)
                result[key] = value
            }
            result
        }
    }
}
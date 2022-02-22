import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

object CascadeJsonUtil {

    fun fromStringToMap(jsonString: String): MutableMap<String, String> {
        if (jsonString.isEmpty()) throw IllegalArgumentException("jsonString is empty")
        if (jsonString == "{}") return mutableMapOf()

        val json = Json.parseToJsonElement(jsonString)
        if (json !is JsonObject) throw IllegalArgumentException("jsonString is not a JsonObject")

        val result = mutableMapOf<String, String>()
        val layers = mutableListOf<String>()

        fun dfs(jsonElement: JsonElement) {
            when (jsonElement) {
                is JsonObject -> {
                    jsonElement.forEach { (key, value) ->
                        layers.add(key)
                        dfs(value)
                        layers.removeLast()
                    }
                }
                is JsonPrimitive -> {
                    result[layers.joinToString("/")] = jsonElement.content
                }
                is JsonArray -> throw IllegalArgumentException("JsonArray is not expected")
            }
        }

        dfs(json.jsonObject)
        return result
    }

    fun fromStringToJsonString(jsonString: String): String {
        return Json.encodeToString(fromStringToMap(jsonString))
    }

}

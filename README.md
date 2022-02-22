# CascadeJson.kt

A specific format represent file directory in Json

Provide a function for convert to map or json string in Kotlin

### Usage

Download zip file in [Release](https://github.com/CWKSC/CascadeJson.kt/releases), unzip it, put into `~/.m2/repository/`

In your `build.gradle.kts`

```kotlin
repositories {
    ...
    mavenLocal()
}

dependencies {
    ...
    implementation("cwksc:CascadeJsonKt-jvm:0.0.0")
}
```

### Interface

```kotlin
fromStringToMap(jsonString: String):        MutableMap<String, String>
fromStringToJsonString(jsonString: String): String
```

### Example

Input:

```json
{
    "file1": "value1",
    "file2": "value2",
    "dir1": {
        "file3": "value3",
        "file4": "value4"
    },
    "dir2": {
        "file5": "value5",
        "file6": "value6",
        "dir3" : {
            "file7": "value7",
            "file8": "value8"
        }
    }
}
```

Convert by `fromStringToMap`:

```kotlin
mapOf(
    "file1"           to "value1",
    "file2"           to "value2",
    "dir1/file3"      to "value3",
    "dir1/file4"      to "value4",
    "dir2/file5"      to "value5",
    "dir2/file6"      to "value6",
    "dir2/dir3/file7" to "value7",
    "dir2/dir3/file8" to "value8"
)
```

Convert by `fromStringToJsonString`:

```json
{"file1":"value1","file2":"value2","dir1/file3":"value3","dir1/file4":"value4","dir2/file5":"value5","dir2/file6":"value6","dir2/dir3/file7":"value7","dir2/dir3/file8":"value8"}
```

### Implementation

```kotlin
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
```

### Project environment

open folder `CascadeJsonKt/` by IntelliJ IDEA

### License

MIT

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class CascadeJsonUtilTest {

    @Test
    fun empty() {
        assertEquals(mutableMapOf(), CascadeJsonUtil.fromStringToMap("{}"))
    }

    private val testcase1 = """
{
    "file1": "value1",
    "file2": "value2",
    "dir1": {
        "file3": "value3",
        "file4": "value4"
    },
    "dir2": ["dir2-key", {
        "file5": "value5",
        "file6": "value6",
        "dir3" : {
            "file7": "value7",
            "file8": "value8"
        }
    }]
}
"""

    private val testcase1_result = mapOf(
        "file1" to "value1",
        "file2" to "value2",
        "dir1/file3" to "value3",
        "dir1/file4" to "value4",
        "dir2" to "dir2-key",
        "dir2/file5" to "value5",
        "dir2/file6" to "value6",
        "dir2/dir3/file7" to "value7",
        "dir2/dir3/file8" to "value8"
    )

    @Test
    fun fromStringToMap_testcase1() {
        val map = CascadeJsonUtil.fromStringToMap(testcase1)
        assertEquals(testcase1_result, map)
    }

    @Test
    fun fromStringToJsonString_testcase1() {
        val jsonString = CascadeJsonUtil.fromStringToJsonString(testcase1)
        assertEquals(
            Json.encodeToJsonElement(testcase1_result),
            Json.parseToJsonElement(jsonString)
        )
    }

    @Test
    fun throwIllegalArgumentException() {
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("") }
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("[]") }
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("1") }
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("\"\"") }
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("true") }
        assertFailsWith(IllegalArgumentException::class) { CascadeJsonUtil.fromStringToMap("{ \"\": [] }") }
    }

}
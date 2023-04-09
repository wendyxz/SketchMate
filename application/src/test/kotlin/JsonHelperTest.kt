package wb.helper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonHelperTest {

    @Test
    fun `test processJsonString with simple json`() {
        val jsonString = """{"name": "John", "age": 30}"""
        val expected = """{"name": "John", "age": 30}"""
        assertEquals(expected, processJsonString(jsonString))
    }

    @Test
    fun `test processJsonString with nested json`() {
        val jsonString = """{"name": "John", "age": 30, "address": {"city": "New York", "state": "NY"}}"""
        val expected = """{"name": "John", "age": 30, "address": {\"city\": \"New York\", \"state\": \"NY\"}}"""
        assertEquals(expected, processJsonString(jsonString))
    }

    @Test
    fun `test processJsonString with escaped quotes`() {
        val jsonString = """{"name": "John \"Doe\"", "age": 30}"""
        val expected = """{"name": "John \"Doe\"", "age": 30}"""
        assertEquals(expected, processJsonString(jsonString))
    }

    @Test
    fun `test processJsonString with multiple levels of nesting and escaped quotes`() {
        val jsonString = """{"name": "John \"Doe\"", "age": 30, "address": {"city": "New \"York\"", "state": "NY"}}"""
        val expected = """{"name": "John \"Doe\"", "age": 30, "address": {\"city\": \"New \\"York\\"\", \"state\": \"NY\"}}"""
        assertEquals(expected, processJsonString(jsonString))
    }

    @Test
    fun `test removeDoubleQuotes with string surrounded by quotes`() {
        val str = "\"hello world\""
        val expected = "hello world"
        assertEquals(expected, removeDoubleQuotes(str))
    }

    @Test
    fun `test removeDoubleQuotes with string not surrounded by quotes`() {
        val str = "hello world"
        val expected = "hello world"
        assertEquals(expected, removeDoubleQuotes(str))
    }

}

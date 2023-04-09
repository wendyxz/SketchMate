package wb.backend

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BoardServiceTest {
    @BeforeEach
    fun setUp() {
        // Set up any required test data here
    }

    @Test
    @Order(1)
    fun testCreateBoard() {
        val boardname = "testBoard"
        val json = "{}"
        val result = createBoard(boardname, json)
        assertNotEquals("", result)
    }

    @Test
    @Order(2)
    fun testBlogin() {
        val boardname = "testBoard"
        val json = "{}"
        val result = Blogin(boardname, json)
        assertEquals("Success", result)
    }

    @Test
    @Order(3)
    fun testGetSingleBoard() {
        val result = getSingleBoard()
        assertEquals("{}", result)
    }

    @Test
    @Order(4)
    fun testUpdateBoard() {
        val boardname = "testBoard"
        val json = """"{\"time\":1680918935412,\"whiteboard\":[]}""""
        val result = updateBoard(boardname, json)
        assertEquals("Success", result)
    }

    @Test
    @Order(5)
    fun testGetBoards() {
        val result = getBoards()
        assertNotEquals(result.size, 0)
    }

    @Test
    @Order(6)
    fun testBlogout() {
        val result = Blogout()
        assertEquals("Success", result)
    }

    @Test
    @Order(7)
    fun testDeleteBoard() {
        val result = deleteBoard()
        assertEquals("1", result)
    }
}

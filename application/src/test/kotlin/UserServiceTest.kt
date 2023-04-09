package wb.backend

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotEquals

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserServiceTest {
    @BeforeEach
    fun setUp() {
        // Set up any required test data here
    }

    @Test
    @Order(1)
    fun testCreateUser() {
        val result = createUser("test", "test")
        assertNotEquals("", result)
    }

    @Test
    @Order(2)
    fun testLogin() {
        val result = login("test", "test")
        assertNotEquals("", result)
    }

    @Test
    @Order(3)
    fun testGetSingleUser() {
        val result = getSingleUser()
        assertNotEquals(result, "")
    }

    @Test
    @Order(4)
    fun testUpdateUser() {
        val result = updateUser("test", "test")
        assertNotEquals(result, "")
    }

    @Test
    @Order(5)
    fun testGetUsers() {
        val result = getUsers()
        assertNotEquals(result, "")
    }

    @Test
    @Order(6)
    fun testLogout() {
        val result = logout()
        assertNotEquals(result, "")
    }

    @Test
    @Order(7)
    fun testDeleteUser() {
        val result = deleteUser()
        assertNotEquals(result, "")
    }
}

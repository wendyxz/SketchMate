package com.cs346.web

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import com.cs346.web.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.Cookie

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.datasource.url=jdbc:postgresql://grove-narwhal-6387.7tt.cockroachlabs.cloud:26257/integrationTest?sslmode=verify-full&password=WixY-RJ9dUBq1-ziln-hNg&user=kevin"
    ]
)
@AutoConfigureMockMvc
class BaseIntegrationTest{

    @Autowired
    private val jdbcTemplate: JdbcTemplate = JdbcTemplate()

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var groupService: GroupService

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var entryService: GroupService


    @Transactional
    protected fun initDB(){
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id String PRIMARY KEY, name VARCHAR(20), password VARCHAR(30), groups TEXT[]);")
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS entries (" +
                    "id String PRIMARY KEY, " +
                    "userId String, " +
                    "created_at String, " +
                    "updated_at String, " +
                    "entryType String, " +
                    "title String, " +
                    "description String, " +
                    "startDate String, " +
                    "endDate String, " +
                    "location String, " +
                    "priority String, " +
                    "grouping String, " +
                    "isPublic Boolean, " +
                    "timeZone String," +
                    "completed Boolean);")
        cleanDB()
    }


    @Transactional
    protected fun cleanDB() {
        val tablesToTruncate = listOf("users","entries").joinToString()
        val sql = """
            TRUNCATE TABLE $tablesToTruncate CASCADE
        """.trimIndent()
        jdbcTemplate?.execute(sql)
    }

    protected fun getSession(newUser: CreateUserDTO): Cookie?{
        // when/then
        val baseUrl = "/login/create"
        val createResult = mockMvc.post(baseUrl) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(newUser)
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(objectMapper.writeValueAsString(1))
//                    cookie { exists("jwt") }
                }
            }

        val loginResult =
            mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect (
                    MockMvcResultMatchers.status().isOk(),
                )
                .andExpect(
                    MockMvcResultMatchers.content().string("Success")
                )
                .andExpect(
                    MockMvcResultMatchers.cookie().exists("jwt")
                )
                .andReturn()
        val session = loginResult.response.cookies.get(0)
        return session
    }
}


//@SpringBootTest
//class WebApplicationTests {
//
//    @Test
//    fun contextLoads() {
//    }
//
//}

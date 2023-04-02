package com.cs346.web

//import com.cs346.web.*
import com.cs346.web.user.CreateUserDTO
import com.cs346.web.user.UserService
import org.junit.jupiter.api.Test
import com.fasterxml.jackson.databind.ObjectMapper
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
import jakarta.servlet.http.Cookie

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.datasource.url=jdbc:postgresql://testdb.cqrw8xjhmymn.ca-central-1.rds.amazonaws.com:5432/whiteboard?user=cs346&password=12345678"
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
    protected lateinit var userService: UserService



    @Transactional
    protected fun initDB(){
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id text PRIMARY KEY, name VARCHAR(20), password VARCHAR(30));")
        cleanDB()
    }


    @Transactional
    protected fun cleanDB() {
        val tablesToTruncate = listOf("users").joinToString()
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

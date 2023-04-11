package com.cs346.web.integration

import com.cs346.web.BaseIntegrationTest
import com.cs346.web.user.CreateUserDTO
import io.mockk.InternalPlatformDsl.toArray
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

@AutoConfigureMockMvc
internal class UserTest : BaseIntegrationTest() {
    @Test
    @DirtiesContext
    fun `Test_Add_New_User`() {
        initDB()
        // given
        val newUser = CreateUserDTO("TestAccName", "TestAccPassword")
        val session = getSession(newUser)

        mockMvc.perform(
            get("/login/user")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

            .andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )

        var logoutResult = mockMvc.perform(
            post("/login/logout")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

        logoutResult.andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )
            .andExpect(
                cookie().maxAge("jwt", 0)
            )

    }

    @Test
    @DirtiesContext
    fun `Test_Update_User_and_Delete`() {
        initDB()
        // given
        val newUser = CreateUserDTO("TestAccName", "TestAccPassword")
        val session = getSession(newUser)

        val newUser2 = CreateUserDTO("TestAccName2", "TestAccPassword2")
        mockMvc.perform(
            patch("/login/update")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser2))
                .accept(MediaType.APPLICATION_JSON)
        )

            .andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )
            .andExpect(
                content().json(objectMapper.writeValueAsString(1))
            )

        mockMvc.perform(
            delete("/login/delete")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

            .andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )
            .andExpect(
                content().json(objectMapper.writeValueAsString(1))
            )

        mockMvc.perform(
            get("/login/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

            .andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )
            .andExpect(
                content().string("[]")
            )
    }

}
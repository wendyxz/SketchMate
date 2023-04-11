package com.cs346.web.integration

import com.cs346.web.BaseIntegrationTest
import com.cs346.web.board.CreateBoardDTO
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
internal class BoardTest : BaseIntegrationTest() {
    @Test
    @DirtiesContext
    fun `Test_Add_New_Board`() {
        initDB()
        // given
        val newBoard = CreateBoardDTO("TestBoardName", "TestBoardJson")
        val session = getBSession(newBoard)

        mockMvc.perform(
            get("/draw/board")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )

            .andDo(MockMvcResultHandlers.print())
            .andExpect(
                status().isOk(),
            )

        var logoutResult = mockMvc.perform(
            post("/draw/logout")
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
    fun `Test_Update_Board_and_Delete`() {
        initDB()
        // given
        val newBoard = CreateBoardDTO("TestBoardName", "TestBoardJson")
        val session = getBSession(newBoard)

        val newBoard2 = CreateBoardDTO("TestBoardName2", "TestBoardJson2")
        mockMvc.perform(
            patch("/draw/update")
                .cookie(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBoard2))
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
            delete("/draw/delete")
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
            get("/draw/boards")
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
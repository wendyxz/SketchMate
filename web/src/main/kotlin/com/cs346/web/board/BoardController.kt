package com.cs346.web.board

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

//import jakarta.xml.bind.DatatypeConverter as DatatypeConverter

@RestController
@RequestMapping("/draw")
class BoardController(var boardService: BoardService) {

    // http://localhost:8080/draw/boards
    @GetMapping(value = ["/boards"])
    fun getAllBoards(): List<Board>? {
        return boardService.getAllBoards()
    }

    // http://localhost:8080/draw/board
    @GetMapping(value = ["/board"])
    fun findBoard(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }
            var usr = boardService.findBoard(jwt)
            return ResponseEntity.ok(usr)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }

    // POST
    // http://localhost:8080/draw/create?id={id}&name={name}&json={json}
    @PostMapping(value = ["/create"])
    fun createBoard(@RequestBody board: Board): Int? {
        return boardService.createBoard(board)
    }

    @PostMapping(value = ["/login"])
    fun login(@RequestBody req: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        var brd: Board? = boardService.login(req.name) ?: return ResponseEntity.badRequest().body("Board not found")

        val issuer = brd!!.id

//        val jwt = Jwts.builder()
//            .setIssuer(issuer)
//            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
//            .signWith(SignatureAlgorithm.HS512, "secret")
//            .compact()
        val jwt = issuer
        var cookie = Cookie("jwt", jwt)
        cookie.maxAge = -1
        cookie.domain = "localhost"
        cookie.path = "/"
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok("Success")
    }

    @PostMapping(value = ["/logout"])
    fun logout(response: HttpServletResponse): ResponseEntity<Any> {

        var cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        cookie.domain = "localhost"
        cookie.path = "/"
        cookie.isHttpOnly = true

        response.addCookie(cookie)

        return ResponseEntity.ok("Success")
    }

    // PATCH
    // http://localhost:8080/draw/update/{id}
    @PatchMapping(value = ["/update"])
    fun updateBoard(
        @RequestBody req: LoginDTO,
//        @RequestBody board: Board,
        @CookieValue("jwt") jwt: String
    ): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }
//            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
//            var suc = boardService.updateBoard(body.issuer, board)
            var suc = boardService.updateBoard(jwt, req)
            return ResponseEntity.ok(suc)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }

    @PatchMapping(value = ["/update/{id}"])
    fun updateBoard(@PathVariable id: String, req: LoginDTO): ResponseEntity<Any>? {
        return ResponseEntity.ok(boardService.updateBoard(id, req))

    }

    // DELETE
    // http://localhost:8080/draw/delete/{UUID}/
    @DeleteMapping(value = ["/delete"])
    fun deleteBoard(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }
//            val body = Jwts.parser().setSigningKey("secret").parseClaimsJws(jwt).body
//            var suc = boardService.deleteBoard(body.issuer)
            var suc = boardService.deleteBoard(jwt)
            return ResponseEntity.ok(suc)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }
}

package com.cs346.web.user

import com.cs346.web.user.LoginDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/login")
class UserController(var userService: UserService) {

    // http://localhost:8080/login/users
    @GetMapping(value = ["/users"])
    fun getAllUsers(): List<User>? {
        return userService.getAllUsers()
    }

    // http://localhost:8080/login/user
    @GetMapping(value = ["/user"])
    fun findUser(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }

            var usr = userService.findUser(jwt)
            return ResponseEntity.ok(usr)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }

    // POST
    // http://localhost:8080/login/create?id={id}&name={name}&password={password}
    @PostMapping(value = ["/create"])
    fun createUser(@RequestBody user: User): Int? {
        return userService.createUser(user)
    }

    @PostMapping()
    fun login(@RequestBody req: LoginDTO, response: HttpServletResponse): ResponseEntity<Any> {
        var usr: User? = userService.login(req.name) ?: return ResponseEntity.badRequest().body("User not found")

        if (usr!!.password != req.password) {
            return ResponseEntity.badRequest().body("Wrong password")
        }

        val issuer = usr!!.id

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
    // http://localhost:8080/login/update/{id}
    @PatchMapping(value = ["/update"])
    fun updateUser(
        @RequestBody user: User,
        @CookieValue("jwt") jwt: String
    ): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }
            var suc = userService.updateUser(jwt, user)
            return ResponseEntity.ok(suc)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }

    @PatchMapping(value = ["/update/{id}"])
    fun updateUser(@PathVariable id: String, @RequestBody user: User): ResponseEntity<Any>? {
        return ResponseEntity.ok(userService.updateUser(id, user))

    }

    // DELETE
    // http://localhost:8080/login/delete/{UUID}/
    @DeleteMapping(value = ["/delete"])
    fun deleteUser(@CookieValue("jwt") jwt: String): ResponseEntity<Any>? {
        try {
            if (jwt == null) {
                return ResponseEntity.status(401).body("Unauthorized")
            }
            var suc = userService.deleteUser(jwt)
            return ResponseEntity.ok(suc)
        } catch (e: Exception) {
            return ResponseEntity.status(401).body("Unauthorized")
        }
    }
}

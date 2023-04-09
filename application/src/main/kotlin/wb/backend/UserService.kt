package wb.backend

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val baseURL = "http://18.117.110.147:8080"
var cookieValue = ""

var userId = ""
var username = ""
var password = ""

/*User*/

fun createUser(username: String, password: String): String {
    val body = "{\"name\": \"$username\", \"password\": \"$password\"}"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/create"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[CREATEUSER] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

fun updateUser(username: String, password: String): String {
    val body = "{\"name\": \"$username\", \"password\": \"$password\"}"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/update"))
        .header("Content-Type", "application/json")
        .header("Cookie", cookieValue)
        .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[UPDATEUSER] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

fun deleteUser(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/delete"))
        .header("Content-Type", "application/json")
        .header("Cookie", cookieValue)
        .DELETE()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[DELETEUSER] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

/*Account*/

fun login(username: String, password: String): String {
    val body = "{\"name\": \"$username\", \"password\": \"$password\"}"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
    println("[LOGIN] $request")
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val setCookie = response.headers().map()["set-cookie"]
    cookieValue = setCookie?.get(0)?.substringBefore(';').toString()

    println("[LOGIN] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

fun logout(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/logout"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(""))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[LOGOUT] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}


fun getUsers(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/users"))
        .header("Content-Type", "application/json")
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[GETUSERS] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""

}

fun getSingleUser(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/login/user"))
        .header("Content-Type", "application/json")
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[GETSINGLEUSER] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

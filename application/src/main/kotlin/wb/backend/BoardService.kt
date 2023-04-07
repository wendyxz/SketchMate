package wb.backend

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.json.*

private val baseURL = "http://localhost:8080"
var cookieValueB = ""

var boardId = ""
var boardname = ""
var json = ""

/*Board*/

fun createBoard(boardname: String, json: String): String {
    val body = "{\"name\": \"$boardname\", \"json\": \"$json\"}"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/create"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[CREATEBOARD] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

fun updateBoard(boardname: String, json: String): String {
//fun updateBoard(json: String): String {
//    val body = "{\"name\": \"$boardname\", \"json\": \"$json\"}"
    val body = "{\"name\": \"$boardname\", \"json\": $json}"
    println("!!!!!!!!!!!")
    println(body)
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/update"))
        .header("Content-Type", "application/json")
        .header("Cookie", cookieValueB)
        .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[UPDATEBOARD] <$response> ${response.body()}")
    return if (response.statusCode() == 200) "Success" else ""
}

fun deleteBoard(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/delete"))
        .header("Content-Type", "application/json")
        .header("Cookie", cookieValueB)
        .DELETE()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[DELETEBOARD] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

/*Account*/

fun Blogin(boardname: String, json: String): String {
    val body = "{\"name\": \"$boardname\", \"json\": \"$json\"}"
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/login"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build()
    println("[BLOGIN] $request")
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    val setCookie = response.headers().map()["set-cookie"]
    cookieValueB = setCookie?.get(0)?.substringBefore(';').toString()

    println("[BLOGIN] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

fun Blogout(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/logout"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(""))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())

    println("[LOGOUT] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}


//fun getBoards(): String {
//    val client = HttpClient.newBuilder().build()
//    val request = HttpRequest.newBuilder()
//        .uri(URI.create("$baseURL/draw/boards"))
//        .header("Content-Type", "application/json")
//        .GET()
//        .build()
//
//    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
//    println("[GETALLBOARDS] <$response> ${response.body()}")
//    return if (response.statusCode() == 200) response.body() else ""
//}

fun getBoards(): List<Pair<String, String>> {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/boards"))
        .header("Content-Type", "application/json")
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[GETALLBOARDS] <$response> ${response.body()}")

    if (response.statusCode() == 200) {
        val jsonString = response.body()
        val jsonArr = JSONArray(jsonString)
        val boardList = mutableListOf<Pair<String, String>>()
        for (i in 0 until jsonArr.length()) {
            val jsonObj = jsonArr.getJSONObject(i)
            val boardId = jsonObj.getString("first")
            val boardName = jsonObj.getString("second")
            val boardPair = Pair(boardId, boardName)
            boardList.add(boardPair)
        }
        return boardList
    } else {
        return emptyList()
    }
}


fun getSingleBoard(): String {
    val client = HttpClient.newBuilder().build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$baseURL/draw/board"))
        .header("Content-Type", "application/json")
        .header("Cookie", cookieValueB)
        .GET()
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    println("[GETSINGLEBOARD] <$response> ${response.body()}")
    return if (response.statusCode() == 200) response.body() else ""
}

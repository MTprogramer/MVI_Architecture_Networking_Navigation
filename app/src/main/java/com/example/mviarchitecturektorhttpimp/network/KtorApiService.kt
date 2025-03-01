package com.example.mviarchitecturektorhttpimp.network

import com.example.mviarchitecturektorhttpimp.model.Post
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorApiService : ApiService {

    // Initializes an HttpClient instance with the CIO engine, configured as a private val
    // CIO is a coroutine-based, non-blocking engine for HTTP requests
    private val client = HttpClient(CIO) {
        // Installs the ContentNegotiation plugin to handle JSON serialization/deserialization
        install(ContentNegotiation) {
            // Configures the plugin to use Kotlinx Serialization's Json with specific settings
            json(Json {
                prettyPrint = true // Formats JSON output with indentation (useful for debugging)
                isLenient = true // Allows lenient parsing (e.g., tolerates malformed JSON)
                ignoreUnknownKeys = true // Ignores unrecognized JSON keys instead of throwing errors
            })
        }
    }

    // Implements the getPosts() function from ApiService, marked as suspend for coroutine use
    // Returns a List<Post> fetched from the API
    override suspend fun getPosts(): List<Post> {
        // Performs an HTTP GET request to the specified URL using the HttpClient
        // Suspends until the response is received (non-blocking due to CIO engine)
        val response = client.get("https://jsonplaceholder.typicode.com/posts")
        // Converts the response body (raw JSON string) into a List<Post> using Json.decodeFromString
        // Uses the Post data class's serializer (from @Serializable annotation) to deserialize
        return Json.decodeFromString(response.bodyAsText())
    }

    // Defines a cleanup function to close the HttpClient and release resources
    override fun close() {
        // Closes the client, shutting down connections and freeing memory
        client.close()
    }
}
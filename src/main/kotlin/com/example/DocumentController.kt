package com.example

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/search") {
            try {
                val searchRequest = call.parameters["value"]
                if (searchRequest == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                call.respond(searchRequest)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        post("/index") {
            try {
                var fileDescription = ""
                var fileName = ""
                val multipart = call.receiveMultipart()

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            fileDescription = part.value
                        }

                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            val bytes = part.streamProvider().readBytes()
                            File("receive/$fileName").writeBytes(bytes)
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                call.respond(fileDescription)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}

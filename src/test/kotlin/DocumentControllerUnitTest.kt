import com.example.module
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class DocumentControllerUnitTest {
    @Test
    fun shouldSearchReturnAnyString() = testApplication {
        application {
            module()
        }
        val response = client.get("/search?value=google")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("google", response.bodyAsText())
    }

    @Test
    fun shouldSearchWithoutValueReturn400() = testApplication {
        application {
            module()
        }
        val response = client.get("/search")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun shouldIndexReturnOK() = testApplication {
        application {
            module()
        }
        val file = this::class.java.classLoader.getResource("test.txt")
        val response = client.post("/index") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", file.readText(), Headers.build {
                            append(HttpHeaders.ContentType, "plain/text")
                        })
                    },
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(file.readText(), response.bodyAsText())
    }

    @Test
    fun shouldIndexWithoutFileReturn400() = testApplication {
        application {
            module()
        }
        val file = this::class.java.classLoader.getResource("test.txt")
        val response = client.post("/index") {
            setBody("")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
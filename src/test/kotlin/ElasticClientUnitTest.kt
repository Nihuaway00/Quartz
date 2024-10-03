import co.elastic.clients.elasticsearch.core.*
import co.elastic.clients.elasticsearch.core.search.Hit
import com.example.ElasticClient
import com.example.module
import io.ktor.server.testing.*
import kotlinx.io.IOException
import kotlin.test.Test

class ElasticClientUnitTest {
    @Test
    fun testConnect() = testApplication {
        application {
            module()
        }

        ElasticClient().use { wrapper ->
            val client = wrapper.client

            val indexRequest = IndexRequest.Builder<Map<String, String>>()
                .index("my_index")
                .id("1")
                .document(mapOf("field" to "va2222§lue"))
                .build()

            val response = client.index(indexRequest)
            println("Document indexed with id: ${response.id()}")

            val getRequest = GetRequest.Builder()
                .index("my_index")
                .id("1")
                .build()

            try {
                val getResponse: GetResponse<Map<*, *>> = client.get(getRequest, Map::class.java)

                if (getResponse.found()) {
                    val document = getResponse.source()
                    println("Документ найден: $document")
                } else {
                    println("Документ не найден")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Пример полнотекстового поиска с помощью match-запроса
            val searchRequest = SearchRequest.Builder()
                .index("my_index")  // Укажите индекс, в котором хотите искать
                .query { q ->
                    q
                        .match { m ->
                            m
                                .field("field")  // Укажите поле для поиска
                                .query("va2222§lue")  // Укажите искомое значение
                        }
                }
                .build()

            // Выполнение поискового запроса
            val searchResponse: SearchResponse<Map<*, *>> = client.search(searchRequest, Map::class.java)

            // Обработка результатов поиска
            val hits: List<Hit<Map<*, *>>> = searchResponse.hits().hits()
            if (hits.isNotEmpty()) {
                for (hit in hits) {
                    println("Найден документ: ${hit.source()}")
                }
            } else {
                println("Ничего не найдено")
            }
        }

    }
}
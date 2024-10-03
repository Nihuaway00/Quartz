package com.example

import io.ktor.server.application.*
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.indices.rollover.IndexRolloverMappingBuilders.single
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import io.ktor.utils.io.core.*
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

class ElasticClient : Closeable {
    private val restClient: RestClient
    private val transport: RestClientTransport
    val client: ElasticsearchClient

    init {
        // Получаем переменные окружения или используем значения по умолчанию
        val host = System.getenv("ELASTIC_HOST") ?: "localhost"
        val port = System.getenv("ELASTIC_PORT")?.toInt() ?: 9200

        restClient = RestClient.builder(HttpHost(host, port)).build()
        transport = RestClientTransport(restClient, JacksonJsonpMapper())

        client = ElasticsearchClient(transport)
    }

    override fun close() {
        try {
            restClient.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

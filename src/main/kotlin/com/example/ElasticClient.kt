package com.example

import co.elastic.clients.elasticsearch.ElasticsearchClient
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
        restClient = RestClient.builder(HttpHost("localhost", 9200)).build()
        transport = RestClientTransport(restClient, JacksonJsonpMapper())

        client = ElasticsearchClient(transport)
    }

    override fun close() {
        try{
            restClient.close()
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}
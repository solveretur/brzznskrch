package com.brzezinski.roche.ipapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import reactor.core.publisher.Mono
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface IpApiClient {

    @POST("/batch")
    fun batch(
        @Body queries: List<IpApiQuery>,
        @Query("fields") fields: String = FIELDS
    ): Mono<List<IpApiBatchResponse>>

    companion object {
        private const val CONNECT_TIMEOUT_SEC = 5L
        private const val READ_TIMEOUT_SEC = 5L
        private const val WRITE_TIMEOUT_SEC = 5L
        private const val FIELDS = "country,countryCode"

        fun create(baseUrl: String, objectMapper: ObjectMapper): IpApiClient {
            val httpClient = OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
                .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addCallAdapterFactory(ReactorCallAdapterFactory.create())
                .client(httpClient.build())
                .build()
            return retrofit.create(IpApiClient::class.java)
        }
    }
}

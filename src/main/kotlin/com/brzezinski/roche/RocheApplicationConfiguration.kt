package com.brzezinski.roche

import com.brzezinski.roche.ipaddress.IpApiService
import com.brzezinski.roche.ipapi.IpApiClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val IP_ADDRESS_LIST_MIN_SIZE = 1
const val IP_ADDRESS_LIST_MAX_SIZE = 5
const val FIND_NORTHERN_COUNTRIES_ENDPOINT = "/northcountries"

@Configuration
class RocheApplicationConfiguration(
    @Value("\${com.brzezinski.roche.ipapi.baseUrl}") private val ipApiBaseUrl: String,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun ipApiClient(): IpApiClient = IpApiClient.create(ipApiBaseUrl, objectMapper)

    @Bean
    fun ipAddressService(ipApiClient: IpApiClient) = IpApiService(ipApiClient)
}

package com.brzezinski.roche.rest

import com.brzezinski.roche.FIND_NORTHERN_COUNTRIES_ENDPOINT
import com.brzezinski.roche.IP_ADDRESS_LIST_MAX_SIZE
import com.brzezinski.roche.IP_ADDRESS_LIST_MIN_SIZE
import com.brzezinski.roche.ipaddress.NorthernCountriesCodes
import com.brzezinski.roche.ipapi.IpApiBatchResponse
import com.brzezinski.roche.ipapi.IpApiClient
import okhttp3.ResponseBody
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.max
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class IpAddressControllerTest {

    @MockBean
    lateinit var ipApiClient: IpApiClient

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun findNorthernCountriesShouldReturnBadRequestWhenIpAddressListSizeIsLessThanMin() {
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", (0 until max(0, IP_ADDRESS_LIST_MIN_SIZE - 1)).map { generateRandomIpAddress() })
                    .build()
            }
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun findNorthernCountriesShouldReturnBadRequestWhenIpAddressListSizeIsGreaterThanMax() {
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", (0 until IP_ADDRESS_LIST_MAX_SIZE + 1).map { generateRandomIpAddress() })
                    .build()
            }
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun findNorthernCountriesShouldReturnBadRequestWhenProvidedWithNotValidIpAddress() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = "google.com"
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2)
                    .build()
            }
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun findNorthernCountriesShouldReturnOnlyNorthernCountries() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = generateRandomIpAddress()
        val ip3 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        listOf(
                            IpApiBatchResponse("AnotherNorthernCountry", it.next()),
                            IpApiBatchResponse("NorthernCountry", it.next()),
                            IpApiBatchResponse("NotNorthernCountry", "XYZ")
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2, ip3)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
            .jsonPath("$.northcountries[0]").isEqualTo("AnotherNorthernCountry")
            .jsonPath("$.northcountries[1]").isEqualTo("NorthernCountry")
            .jsonPath("$.northcountries[2]").doesNotExist()
    }

    @Test
    fun findNorthernCountriesShouldSortNorthernCountries() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        listOf(
                            IpApiBatchResponse("NorthernCountry", it.next()),
                            IpApiBatchResponse("AnotherNorthernCountry", it.next())
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
            .jsonPath("$.northcountries[0]").isEqualTo("AnotherNorthernCountry")
            .jsonPath("$.northcountries[1]").isEqualTo("NorthernCountry")
            .jsonPath("$.northcountries[2]").doesNotExist()
    }

    @Test
    fun findNorthernCountriesShouldReturnUniqueNorthernCountries() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        val v = it.next()
                        listOf(
                            IpApiBatchResponse("NorthernCountry", v),
                            IpApiBatchResponse("NorthernCountry", v)
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
            .jsonPath("$.northcountries[0]").isEqualTo("NorthernCountry")
            .jsonPath("$.northcountries[1]").doesNotExist()
    }

    @Test
    fun findNorthernCountriesShouldWorkWhenProvidedWithDuplicates() {
        // given
        val ip1 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        val v = it.next()
                        listOf(
                            IpApiBatchResponse("NorthernCountry", v),
                            IpApiBatchResponse("NorthernCountry", v)
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip1)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
            .jsonPath("$.northcountries[0]").isEqualTo("NorthernCountry")
            .jsonPath("$.northcountries[1]").doesNotExist()
    }

    @Test
    fun findNorthernCountriesShouldWorkWhenIpApiReturnsResponseWithNull() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        val v = it.next()
                        listOf(
                            IpApiBatchResponse("NorthernCountry", v),
                            IpApiBatchResponse(null, null)
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
            .jsonPath("$.northcountries[0]").isEqualTo("NorthernCountry")
            .jsonPath("$.northcountries[1]").doesNotExist()
    }

    @Test
    fun findNorthernCountriesShouldReturnServiceUnavailableWhenIpApiReturnsHttpException() {
        // given
        val ip1 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.error(
                HttpException(
                    Response.error<IpApiBatchResponse>(
                        500,
                        ResponseBody.create(
                            okhttp3.MediaType.get("application/json"),
                            "{\"status\": 500, \"error\": \"messageSendingFailed\", \"message\": \"\"}"
                        )
                    )
                )
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1)
                    .build()
            }
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
    }

    @Test
    fun findNorthernCountriesShouldWorkWhenIpApiReturnsOnlyResponsesWithNull() {
        // given
        val ip1 = generateRandomIpAddress()
        val ip2 = generateRandomIpAddress()
        Mockito.`when`(ipApiClient.batch(Mockito.anyList(), Mockito.anyString())).thenReturn(
            Mono.just(
                NorthernCountriesCodes.ALL.iterator()
                    .let {
                        val v = it.next()
                        listOf(
                            IpApiBatchResponse(null, null),
                            IpApiBatchResponse(null, null)
                        )
                    }
            )
        )
        // expect
        webTestClient.get()
            .uri {
                it
                    .path(FIND_NORTHERN_COUNTRIES_ENDPOINT)
                    .queryParam("ip", ip1, ip2)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.northcountries").isArray
    }

    private fun generateRandomIpAddress(): String = "${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}.${Random.nextInt(256)}"
}

package com.brzezinski.roche.ipaddress

import com.brzezinski.roche.ipapi.IpApiClient
import com.brzezinski.roche.ipapi.toIpApiQuery
import mu.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class IpApiService(private val ipApiClient: IpApiClient) : IpAddressService {

    override fun findNorthernCountries(ipAddressList: List<String>): Mono<Set<String>> {
        log.debug("Finding northern countries for: $ipAddressList")
        return ipApiClient.batch(ipAddressList.map { it.toIpApiQuery() })
            .onErrorResume(
                { it is retrofit2.HttpException },
                {
                    log.error("Couldn't fetch countries for ip: $ipAddressList due to: $it")
                    throw IpApiClientException(it)
                }
            )
            .map { response ->
                log.debug("IpApi client response is: $response for: $ipAddressList")
                response
                    .filter { it.isValid() }
                    .filter { it.countryCode!! in NorthernCountriesCodes.ALL }
                    .map { it.country!! }
                    .toSortedSet()
            }
    }
}

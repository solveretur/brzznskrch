package com.brzezinski.roche.rest

import com.brzezinski.roche.FIND_NORTHERN_COUNTRIES_ENDPOINT
import com.brzezinski.roche.ipaddress.IpAddressService
import com.brzezinski.roche.rest.validation.IpAddressList
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Validated
@RestController
@RequestMapping("/")
class IpAddressController(
    private val ipAddressService: IpAddressService
) {

    @GetMapping(FIND_NORTHERN_COUNTRIES_ENDPOINT)
    fun findNorthernCountries(
        @RequestParam(name = "ip") @IpAddressList ipAddressList: List<String>
    ): Mono<ResponseEntity<FindNorthernCountriesResponse>> {
        log.info("Finding northern countries for ips: $ipAddressList")
        return ipAddressService
            .findNorthernCountries(ipAddressList)
            .map { FindNorthernCountriesResponse(it) }
            .doOnNext {
                log.info { "For ips: $ipAddressList returning $it" }
            }
            .map { ResponseEntity.ok(it) }
    }
}

package com.brzezinski.roche.ipaddress

import reactor.core.publisher.Mono

interface IpAddressService {
    fun findNorthernCountries(ipAddressList: List<String>): Mono<Set<String>>
}

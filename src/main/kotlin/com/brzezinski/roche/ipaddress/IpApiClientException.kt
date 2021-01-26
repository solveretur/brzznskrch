package com.brzezinski.roche.ipaddress

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class IpApiClientException(t: Throwable) : ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, t.message, t)

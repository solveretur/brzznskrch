package com.brzezinski.roche.ipapi

data class IpApiQuery(
    val query: String
)

fun String.toIpApiQuery(): IpApiQuery = IpApiQuery(this)

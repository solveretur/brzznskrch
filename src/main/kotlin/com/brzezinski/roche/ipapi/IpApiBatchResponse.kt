package com.brzezinski.roche.ipapi

data class IpApiBatchResponse(
    val country: String?,
    val countryCode: String?
) {
    fun isValid(): Boolean = !this.countryCode.isNullOrBlank() && !this.country.isNullOrBlank()
}

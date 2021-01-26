package com.brzezinski.roche.rest.validation

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.Retention
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Constraint(validatedBy = [IpAddressListValidator::class])
annotation class IpAddressList(
    val message: String = "provided ip address list is not valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

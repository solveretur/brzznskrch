package com.brzezinski.roche.rest.validation

import com.brzezinski.roche.IP_ADDRESS_LIST_MAX_SIZE
import com.brzezinski.roche.IP_ADDRESS_LIST_MIN_SIZE
import org.apache.commons.validator.routines.InetAddressValidator
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class IpAddressListValidator : ConstraintValidator<IpAddressList, List<String>> {

    private val inetAddressValidator = InetAddressValidator()

    override fun isValid(value: List<String>, context: ConstraintValidatorContext): Boolean {
        return when {
            (value.size < IP_ADDRESS_LIST_MIN_SIZE || value.size > IP_ADDRESS_LIST_MAX_SIZE) -> {
                context.disableDefaultConstraintViolation()
                context
                    .buildConstraintViolationWithTemplate("you must provide between $IP_ADDRESS_LIST_MIN_SIZE and $IP_ADDRESS_LIST_MAX_SIZE valid ip addresses")
                    .addConstraintViolation()
                false
            }
            value.any { !inetAddressValidator.isValid(it) } -> {
                context.disableDefaultConstraintViolation()
                context
                    .buildConstraintViolationWithTemplate("you provided a not valid ip address")
                    .addConstraintViolation()
                false
            }
            else -> true
        }
    }
}

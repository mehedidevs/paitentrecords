package com.patientrecords.doctorapp.addpaitents.validation

import com.patientrecords.doctorapp.addpaitents.components.Gender

/**
 * Validator for Patient form fields
 * Follows Single Responsibility Principle - only handles validation logic
 */
class PatientFormValidator {

    /**
     * Validation result wrapper
     */
    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val errorMessage: String) : ValidationResult()
    }

    /**
     * Validate full name
     * - Required field
     * - Minimum 2 characters
     * - Maximum 100 characters
     * - Only letters and spaces allowed
     */
    fun validateFullName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("Full name is required")
            name.length < 2 -> ValidationResult.Invalid("Name must be at least 2 characters")
            name.length > 100 -> ValidationResult.Invalid("Name cannot exceed 100 characters")
            !name.matches(NAME_REGEX) -> ValidationResult.Invalid("Name can only contain letters and spaces")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validate mobile number
     * - Required field
     * - Must be exactly 10 digits
     * - Only numbers allowed
     */
    fun validateMobileNumber(number: String): ValidationResult {
        val cleanNumber = number.replace(Regex("[^0-9]"), "")
        return when {
            cleanNumber.isBlank() -> ValidationResult.Invalid("Mobile number is required")
            cleanNumber.length < 10 -> ValidationResult.Invalid("Mobile number must be 10 digits")
            cleanNumber.length > 10 -> ValidationResult.Invalid("Mobile number cannot exceed 10 digits")
            !cleanNumber.matches(PHONE_REGEX) -> ValidationResult.Invalid("Invalid mobile number format")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validate age
     * - Required field
     * - Must be between 1 and 150
     * - Must be a valid number
     */
    fun validateAge(age: String): ValidationResult {
        if (age.isBlank()) {
            return ValidationResult.Invalid("Age is required")
        }
        
        val ageInt = age.toIntOrNull()
        return when {
            ageInt == null -> ValidationResult.Invalid("Please enter a valid age")
            ageInt < 1 -> ValidationResult.Invalid("Age must be at least 1")
            ageInt > 150 -> ValidationResult.Invalid("Please enter a valid age")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validate gender selection
     * - Required field
     */
    fun validateGender(gender: Gender?): ValidationResult {
        return when (gender) {
            null -> ValidationResult.Invalid("Please select a gender")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validate address (optional field)
     * - If provided, should not exceed 500 characters
     */
    fun validateAddress(address: String): ValidationResult {
        return when {
            address.length > 500 -> ValidationResult.Invalid("Address cannot exceed 500 characters")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validate entire form
     */
    fun validateForm(
        fullName: String,
        mobileNumber: String,
        age: String,
        gender: Gender?,
        address: String
    ): FormValidationResult {
        val nameResult = validateFullName(fullName)
        val mobileResult = validateMobileNumber(mobileNumber)
        val ageResult = validateAge(age)
        val genderResult = validateGender(gender)
        val addressResult = validateAddress(address)

        val isValid = nameResult is ValidationResult.Valid &&
                mobileResult is ValidationResult.Valid &&
                ageResult is ValidationResult.Valid &&
                genderResult is ValidationResult.Valid &&
                addressResult is ValidationResult.Valid

        return FormValidationResult(
            isValid = isValid,
            fullNameError = (nameResult as? ValidationResult.Invalid)?.errorMessage,
            mobileNumberError = (mobileResult as? ValidationResult.Invalid)?.errorMessage,
            ageError = (ageResult as? ValidationResult.Invalid)?.errorMessage,
            genderError = (genderResult as? ValidationResult.Invalid)?.errorMessage,
            addressError = (addressResult as? ValidationResult.Invalid)?.errorMessage
        )
    }

    /**
     * Form validation result containing all field errors
     */
    data class FormValidationResult(
        val isValid: Boolean,
        val fullNameError: String? = null,
        val mobileNumberError: String? = null,
        val ageError: String? = null,
        val genderError: String? = null,
        val addressError: String? = null
    )

    companion object {
        // Regex patterns
        private val NAME_REGEX = Regex("^[a-zA-Z\\s]+$")
        private val PHONE_REGEX = Regex("^[0-9]{10}$")
    }
}

/**
 * Extension functions for ValidationResult
 */
fun PatientFormValidator.ValidationResult.isValid(): Boolean = this is PatientFormValidator.ValidationResult.Valid

fun PatientFormValidator.ValidationResult.errorOrNull(): String? =
    (this as? PatientFormValidator.ValidationResult.Invalid)?.errorMessage

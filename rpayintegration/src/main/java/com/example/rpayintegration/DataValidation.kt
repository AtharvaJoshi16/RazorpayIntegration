package com.example.rpayintegration

/*
    A data validation object which contains field name and error in the field.
 */
data class DataValidation(
    val fieldName : String,
    val error: String
)

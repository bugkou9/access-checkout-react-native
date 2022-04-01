package com.worldpay.access.checkout.reactnative.validation

class ValidationConfig(
        val baseUrl: String,
        val panId: String,
        val expiryDateId: String,
        val cvcId: String,
        val enablePanFormatting: Boolean,
        val acceptedCardBrands: Array<String>
)


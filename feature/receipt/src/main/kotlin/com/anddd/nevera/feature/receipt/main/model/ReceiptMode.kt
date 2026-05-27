package com.anddd.nevera.feature.receipt.main.model

sealed interface ReceiptMode {
    data object Camera : ReceiptMode
    data object Gallery : ReceiptMode
}

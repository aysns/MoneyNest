package com.moneynest.app.feature.scanner.model

data class ReceiptItem(
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long = 0L,
    val discount: Long = 0L
) {
    val subtotal: Long
        get() = (quantity.toLong() * unitPrice) - discount
}

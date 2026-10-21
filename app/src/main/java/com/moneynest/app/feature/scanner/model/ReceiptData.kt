
package com.moneynest.app.feature.scanner.model

data class ReceiptData(
    val storeName: String = "",
    val date: String = "",
    val items: List<ReceiptItem> = emptyList(),
    val totalAmount: Long? = null,
    val rawText: String = "",
    val totalDiscount: Long = 0L
) {
    val totalBeforeDiscount: Long
        get() = items.sumOf { item ->
            item.quantity.toLong() * item.unitPrice
        }

    val calculatedTotal: Long
        get() = (totalBeforeDiscount - totalDiscount)
            .coerceAtLeast(0L)
}

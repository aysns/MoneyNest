package com.moneynest.app.feature.scanner.model

data class OcrTextElement(
    val text: String,
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

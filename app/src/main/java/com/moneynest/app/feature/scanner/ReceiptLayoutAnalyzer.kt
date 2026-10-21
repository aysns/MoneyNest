
package com.moneynest.app.feature.scanner

import com.moneynest.app.feature.scanner.model.OcrTextElement
import kotlin.math.abs

class ReceiptLayoutAnalyzer {

    fun groupIntoLines(
        elements: List<OcrTextElement>
    ): List<String> {

        if (elements.isEmpty()) {
            return emptyList()
        }

        val sortedElements = elements.sortedWith(
            compareBy<OcrTextElement> { (it.top + it.bottom) / 2 }
                .thenBy { it.left }
        )

        val groups = mutableListOf<MutableList<OcrTextElement>>()

        for (element in sortedElements) {

            val centerY = (element.top + element.bottom) / 2

            val matchingGroup = groups.firstOrNull { group ->

                val averageY = group.map {
                    (it.top + it.bottom) / 2
                }.average()

                val averageHeight = group.map {
                    it.bottom - it.top
                }.average()

                abs(centerY - averageY) <=
                        averageHeight * 0.5
            }

            if (matchingGroup != null) {
                matchingGroup.add(element)
            } else {
                groups.add(mutableListOf(element))
            }
        }

        return groups
            .sortedBy { group ->
                group.map {
                    (it.top + it.bottom) / 2
                }.average()
            }
            .map { group ->
                group
                    .sortedBy { it.left }
                    .joinToString(" ") { it.text }
            }
    }
}

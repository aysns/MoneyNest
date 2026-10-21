
package com.moneynest.app.feature.scanner

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.moneynest.app.feature.scanner.model.OcrTextElement

class ReceiptOcrAnalyzer {

    private val recognizer = TextRecognition.getClient(
        TextRecognizerOptions.DEFAULT_OPTIONS
    )

    fun recognizeText(
        context: Context,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(
                context,
                imageUri
            )

            recognizer.process(image)
                .addOnSuccessListener { result ->
                    onSuccess(result.text)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }

        } catch (exception: Exception) {
            onError(exception)
        }
    }

    fun recognizeTextWithPosition(
        context: Context,
        imageUri: Uri,
        onSuccess: (List<OcrTextElement>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(
                context,
                imageUri
            )

            recognizer.process(image)
                .addOnSuccessListener { result ->

                    val elements = mutableListOf<OcrTextElement>()

                    for (block in result.textBlocks) {
                        for (line in block.lines) {
                            for (element in line.elements) {

                                val box = element.boundingBox
                                    ?: continue

                                elements.add(
                                    OcrTextElement(
                                        text = element.text,
                                        left = box.left,
                                        top = box.top,
                                        right = box.right,
                                        bottom = box.bottom
                                    )
                                )
                            }
                        }
                    }

                    onSuccess(elements)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }

        } catch (exception: Exception) {
            onError(exception)
        }
    }


    fun close() {
        recognizer.close()
    }
}

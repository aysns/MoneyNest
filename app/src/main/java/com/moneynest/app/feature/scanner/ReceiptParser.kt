package com.moneynest.app.feature.scanner

import com.moneynest.app.feature.scanner.model.ReceiptData
import com.moneynest.app.feature.scanner.model.ReceiptItem
import java.text.SimpleDateFormat
import java.util.Locale

class ReceiptParser {

    fun parse(rawText: String): ReceiptData {

        val normalizedText = rawText.replace(
            Regex("""(?<=\d)[.,]\s+(?=\d{3}(?:\D|$))"""),
            "."
        )

        val lines = normalizedText.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return ReceiptData(
            storeName = findStoreName(lines),
            date = findDate(normalizedText),
            items = findItems(lines),
            totalAmount = findTotal(lines),
            totalDiscount = findDiscount(lines),
            rawText = rawText
        )
    }

    private fun findStoreName(lines: List<String>): String {

        val ignoredKeywords = listOf(
            "STRUK", "RECEIPT", "NOTA", "INVOICE",
            "TELP", "PHONE", "JL.", "JALAN",
            "TANGGAL", "DATE", "KASIR", "CASHIER",
            "TOTAL", "NPWP"
        )

        return lines.take(5).firstOrNull { line ->

            val upper = line.uppercase(Locale.ROOT)

            ignoredKeywords.none { upper.contains(it) } &&
                    line.any { it.isLetter() } &&
                    line.length >= 3

        } ?: ""
    }

    private fun findDate(rawText: String): String {

        val pattern = Regex(
            """(?<!\d)(\d{1,2})[./-](\d{1,2})[./-](\d{4})(?!\d)"""
        )

        for (match in pattern.findAll(rawText)) {

            val day = match.groupValues[1].toIntOrNull()
                ?: continue
            val month = match.groupValues[2].toIntOrNull()
                ?: continue
            val year = match.groupValues[3].toIntOrNull()
                ?: continue

            val formatted = "%02d-%02d-%04d".format(
                Locale.US,
                day,
                month,
                year
            )

            val dateFormat = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.US
            ).apply {
                isLenient = false
            }

            val valid = runCatching {
                dateFormat.parse(formatted)
            }.getOrNull()

            if (valid != null) {
                return formatted
            }
        }

        return ""
    }

    private fun isSummaryOrPaymentLine(line: String): Boolean {

        val upper = line
            .uppercase(Locale.ROOT)
            .trim()
            .replace(Regex("""\s+"""), " ")

        val ignoredKeywords = listOf(
            "HARGA JUAL",
            "HARGA TOTAL",
            "TOTAL HARGA",
            "TOTAL BELANJA",
            "TOTAL BAYAR",
            "TOTAL PEMBAYARAN",
            "GRAND TOTAL",
            "SUBTOTAL",
            "TOTAL",
            "JUMLAH BAYAR",
            "JUMLAH BELANJA",
            "BAYAR",
            "PEMBAYARAN",
            "KEMBALI",
            "TUNAI",
            "CASH",
            "SHOPEEPAY",
            "GOPAY",
            "OVO",
            "DANA",
            "QRIS",
            "DEBIT",
            "CREDIT",
            "KREDIT",
            "DISKON",
            "DISCOUNT",
            "POTONGAN",
            "PROMO",
            "VOUCHER",
            "VCR",
            "VC PT",
            "PAJAK",
            "PPN",
            "TANGGAL",
            "DATE",
            "TELP",
            "PHONE",
            "NPWP",
            "POIN",
            "POINT",
            "MEMBER",
            "KRITIK",
            "SARAN",
            "KASIR",
            "TERIMA KASIH",
            "SELAMAT",
            "SALDO",
            "BON",
            "LUNAS",
            "ALAMAT",
            "STRUK",
            "RECEIPT",
            "INVOICE",
            "ANDA HEMAT",
            "HEMAT",
            "KARTU",
            "NO. REF",
            "NO REF",
            "TRANSAKSI",
            "ITEMS",
            "TOTAL ITEMS"
        )

        return ignoredKeywords.any { keyword ->
            upper.contains(keyword)
        }
    }

    private fun findItems(lines: List<String>): List<ReceiptItem> {

        val items = mutableListOf<ReceiptItem>()

        val quantityPricePattern = Regex(
            """(?i)^\s*(\d{1,3})\s*[x×]\s*(?:rp\s*)?(\d[\d.,]*)\s+(?:rp\s*)?(\d[\d.,]*)\s*$"""
        )

        val quantitySinglePricePattern = Regex(
            """(?i)^\s*(\d{1,3})\s*[x×]\s*(?:rp\s*)?(\d[\d.,]*)\s*$"""
        )

        val inlineItemPattern = Regex(
            """(?i)^(.+?)\s+(\d{1,3})\s*[x×]\s*(?:rp\s*)?(\d[\d.,]*)\s+(?:rp\s*)?(\d[\d.,]*)\s*$"""
        )

        val namePricePattern = Regex(
            """(?i)^(.+?)\s+(?:rp\s*)?(\d{1,3}(?:[.,]\d{3})+)\s*$"""
        )

        fun isValidName(name: String): Boolean {

            val cleaned = name.trim()
            val upper = cleaned.uppercase(Locale.ROOT)

            return cleaned.length >= 3 &&
                    cleaned.any { it.isLetter() } &&
                    !isSummaryOrPaymentLine(cleaned) &&
                    !upper.startsWith("JL ") &&
                    !upper.startsWith("JL.") &&
                    !upper.startsWith("JALAN ") &&
                    !upper.startsWith("BARANG YANG") &&
                    !upper.startsWith("ISA DIKEMBALIKAN") &&
                    !upper.endsWith(":")
        }

        fun addItem(
            name: String,
            quantity: Int,
            unitPrice: Long
        ) {

            if (
                isValidName(name) &&
                quantity in 1..1000 &&
                unitPrice > 0L
            ) {
                items.add(
                    ReceiptItem(
                        name = name.trim(),
                        quantity = quantity,
                        unitPrice = unitPrice
                    )
                )
            }
        }

        var index = 0

        while (index < lines.size) {

            val line = lines[index]
            val upper = line.uppercase(Locale.ROOT)

            // Bagian akhir daftar barang
            if (
                upper.matches(
                    Regex("""^(?:GRAND\s+)?TOTAL\b.*""")
                ) ||
                upper.startsWith("SUBTOTAL") ||
                upper.startsWith("TUNAI") ||
                upper.startsWith("BAYAR") ||
                upper.startsWith("KEMBALI") ||
                upper.startsWith("LUNAS")
            ) {
                break
            }

            // Abaikan informasi struk, tetapi lanjut
            // membaca barang lain jika masih ada.
            if (isSummaryOrPaymentLine(line)) {
                index++
                continue
            }

            val inlineMatch = inlineItemPattern.matchEntire(line)

            if (inlineMatch != null) {

                val name = inlineMatch.groupValues[1]
                val quantity = inlineMatch.groupValues[2]
                    .toIntOrNull()

                val unitPrice = parseAmount(
                    inlineMatch.groupValues[3]
                )

                val subtotal = parseAmount(
                    inlineMatch.groupValues[4]
                )

                if (
                    quantity != null &&
                    unitPrice != null &&
                    subtotal == quantity.toLong() * unitPrice
                ) {
                    addItem(name, quantity, unitPrice)
                    index++
                    continue
                }
            }

            if (
                isValidName(line) &&
                index + 1 < lines.size
            ) {

                val nextLine = lines[index + 1]

                val priceMatch =
                    quantityPricePattern.matchEntire(nextLine)

                if (priceMatch != null) {

                    val quantity = priceMatch.groupValues[1]
                        .toIntOrNull()

                    val unitPrice = parseAmount(
                        priceMatch.groupValues[2]
                    )

                    val subtotal = parseAmount(
                        priceMatch.groupValues[3]
                    )

                    if (
                        quantity != null &&
                        unitPrice != null &&
                        subtotal == quantity.toLong() * unitPrice
                    ) {
                        addItem(line, quantity, unitPrice)
                        index += 2
                        continue
                    }
                }

                val singleMatch =
                    quantitySinglePricePattern.matchEntire(nextLine)

                if (singleMatch != null) {

                    val quantity = singleMatch.groupValues[1]
                        .toIntOrNull()

                    val unitPrice = parseAmount(
                        singleMatch.groupValues[2]
                    )

                    if (
                        quantity != null &&
                        unitPrice != null
                    ) {
                        addItem(line, quantity, unitPrice)
                        index += 2
                        continue
                    }
                }
            }

            val namePriceMatch =
                namePricePattern.matchEntire(line)

            if (namePriceMatch != null) {

                val name = namePriceMatch.groupValues[1]
                val price = parseAmount(
                    namePriceMatch.groupValues[2]
                )

                if (price != null) {
                    addItem(name, 1, price)
                }
            }

            index++
        }

        return items
    }

    // Menjumlahkan hanya baris diskon aktual, bukan ringkasan ANDA HEMAT.
    private fun findDiscount(lines: List<String>): Long {
        val discountLabel = Regex(
            """(?i)^(?:DISKON|DISCOUNT|POTONGAN|VC\s+PT\b|VOUCHER\b|VCR\b)"""
        )
        val parenthesizedAmount = Regex(
            """\(\s*(?:Rp\.?\s*)?(\d{1,3}(?:[.,]\d{3})+|\d+)\s*\)""",
            RegexOption.IGNORE_CASE
        )
        val negativeAmount = Regex(
            """-\s*(?:Rp\.?\s*)?(\d{1,3}(?:[.,]\d{3})+|\d+)\b""",
            RegexOption.IGNORE_CASE
        )

        var totalDiscount = 0L
        for (line in lines) {
            val trimmed = line.trim()
            if (!discountLabel.containsMatchIn(trimmed)) continue

            // Baris seperti "DISKON (1)" dapat berarti jumlah promo,
            // sehingga angka kecil tanpa pemisah ribuan tidak dihitung.
            val amountText = parenthesizedAmount.find(trimmed)?.groupValues?.get(1)
                ?: negativeAmount.find(trimmed)?.groupValues?.get(1)
                ?: continue
            if (!amountText.contains('.') && !amountText.contains(',')) continue

            val amount = parseAmount(amountText) ?: continue
            if (amount > 0 && totalDiscount <= Long.MAX_VALUE - amount) {
                totalDiscount += amount
            }
        }
        return totalDiscount
    }

    private fun findTotal(lines: List<String>): Long? {

        val totalPattern = Regex(
            """(?i)^\s*(?:grand\s+total|total\s*(?:belanja|bayar|pembayaran|akhir)?|jumlah\s*(?:bayar|belanja)?)\s*[:=]?\s*(?:rp\.?\s*)?(\d[\d.,]*)\s*$"""
        )

        val itemTotalPattern = Regex(
            """(?i)^\s*total\s+ite[mn]s?\s+\d+\s+(?:rp\.?\s*)?(\d[\d.,]*)\s*$"""
        )

        for (line in lines.asReversed()) {

            val match = totalPattern.matchEntire(line)
                ?: itemTotalPattern.matchEntire(line)
                ?: continue

            val amount = parseAmount(
                match.groupValues[1]
            )

            if (amount != null && amount >= 0L) {
                return amount
            }
        }

        return null
    }

    private fun parseAmount(value: String): Long? {

        return value
            .replace(".", "")
            .replace(",", "")
            .trim()
            .toLongOrNull()
    }
}

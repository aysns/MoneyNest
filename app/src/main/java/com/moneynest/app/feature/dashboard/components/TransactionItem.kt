
package com.moneynest.app.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moneynest.app.data.local.entity.TransactionEntity

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == "EXPENSE"

    val amountColor = if (isExpense) {
        Color(0xFFE34B68)
    } else {
        Color(0xFF16885B)
    }

    val iconBackground = if (isExpense) {
        Color(0xFFF0EBFF)
    } else {
        Color(0xFFE6F8EE)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color = iconBackground,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpense) {
                    Icons.Default.ShoppingBag
                } else {
                    Icons.Default.AccountBalanceWallet
                },
                contentDescription = null,
                tint = if (isExpense) {
                    MaterialTheme.colorScheme.primary
                } else {
                    amountColor
                }
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = transaction.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${if (isExpense) "-" else "+"}Rp %,d"
                .format(transaction.amount)
                .replace(',', '.'),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor,
            maxLines = 1
        )
    }
}


package com.moneynest.app.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BalanceCard(
    balance: String,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF8064FF),
            Color(0xFF5938CF),
            Color(0xFF4226A6)
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = gradient,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White
                )

                Text(
                    text = "Total Balance",
                    color = Color(0xFFEAE4FF),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFEAE4FF)
            )
        }

        Text(
            text = balance,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.22f)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = Color(0xFFEAE4FF),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = "Your available money",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEAE4FF)
            )
        }
    }
}

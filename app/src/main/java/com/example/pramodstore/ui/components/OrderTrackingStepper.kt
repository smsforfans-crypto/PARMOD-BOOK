package com.example.pramodstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary

@Composable
fun OrderTrackingStepper(
    currentStatus: String,
    modifier: Modifier = Modifier
) {
    if (currentStatus == "Cancelled") {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color(0xFFFFEBEE), CircleShape)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD32F2F)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "This order was cancelled",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
        return
    }

    val steps = listOf("Order Placed", "Confirmed", "Processing", "Shipped", "Delivered")
    val currentIndex = steps.indexOf(currentStatus).let { if (it == -1) 0 else it }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index <= currentIndex
                val isCurrent = index == currentIndex

                // Step Circle
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> GreenSuccess
                                else -> Color(0xFFCBD5E1)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Connecting Line between steps
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(
                                if (index < currentIndex) GreenSuccess else Color(0xFFCBD5E1)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Step Labels Row
        Row(modifier = Modifier.fillMaxWidth()) {
            steps.forEachIndexed { index, step ->
                val isCompleted = index <= currentIndex
                Text(
                    text = step,
                    fontSize = 9.sp,
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (isCompleted) NavyPrimary else Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

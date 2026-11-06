package com.example.pramodstore.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pramodstore.data.model.Order
import com.example.pramodstore.ui.components.OrderTrackingStepper
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<Order>,
    isLoading: Boolean,
    onCancelOrder: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf("All Orders", "In Progress", "Delivered", "Cancelled")

    val filteredOrders = orders.filter { order ->
        when (selectedFilterIndex) {
            1 -> order.orderStatus in listOf("Order Placed", "Confirmed", "Processing", "Shipped")
            2 -> order.orderStatus == "Delivered"
            3 -> order.orderStatus == "Cancelled"
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // Top Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "My Orders & Tracking",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Track shipment status from Pramod Book Store to your doorstep",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Status Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedFilterIndex,
            containerColor = Color.White,
            edgePadding = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            filters.forEachIndexed { index, filter ->
                Tab(
                    selected = selectedFilterIndex == index,
                    onClick = { selectedFilterIndex = index },
                    text = {
                        Text(
                            text = filter,
                            fontSize = 13.sp,
                            fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFilterIndex == index) NavyPrimary else Color.Gray
                        )
                    }
                )
            }
        }

        if (isLoading && orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NavyPrimary)
            }
            return
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No orders found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Orders you place will appear here with live tracking.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                OrderCard(order = order, onCancelOrder = { onCancelOrder(order.id) })
            }
        }
    }
}

@Composable
fun OrderCard(
    order: Order,
    onCancelOrder: () -> Unit
) {
    val dateString = order.createdAt?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(it)
    } ?: "Recent"

    val statusColor = when (order.orderStatus) {
        "Delivered" -> GreenSuccess
        "Cancelled" -> Color(0xFFD32F2F)
        "Shipped" -> Color(0xFF0288D1)
        "Processing" -> AmberTertiary
        else -> NavyPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Order ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Order #${order.id.takeLast(8).uppercase()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.orderStatus,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Order Tracking Stepper
            OrderTrackingStepper(currentStatus = order.orderStatus)

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            // Items Purchased Breakdown
            Text(
                text = "Items (${order.items.size}):",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(4.dp))

            order.items.forEach { item ->
                val name = item["name"] as? String ?: "Book / Stationery Item"
                val qty = (item["quantity"] as? Number)?.toInt() ?: 1
                val price = (item["price"] as? Number)?.toDouble() ?: 0.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• $name (x$qty)",
                        fontSize = 12.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "₹${(price * qty).toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Total and Payment info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Payment: ${order.paymentMethod} (${order.paymentStatus})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Total: ₹${order.totalAmount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NavyPrimary
                    )
                }

                // If Order is placed and not yet confirmed/shipped, customer can cancel
                if (order.orderStatus == "Order Placed") {
                    OutlinedButton(
                        onClick = onCancelOrder,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("cancel_order_${order.id}"),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text("Cancel Order", fontSize = 11.sp, color = Color(0xFFD32F2F))
                    }
                }
            }
        }
    }
}

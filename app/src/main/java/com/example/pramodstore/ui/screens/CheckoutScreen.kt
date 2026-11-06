package com.example.pramodstore.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pramodstore.data.model.CartItem
import com.example.pramodstore.data.model.Order
import com.example.pramodstore.data.model.UserProfile
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary

@Composable
fun CheckoutScreen(
    userProfile: UserProfile?,
    cartItems: List<CartItem>,
    onBackClick: () -> Unit,
    onPlaceOrder: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    val subtotal = cartItems.sumOf { it.productPrice * it.quantity }
    val deliveryFee = if (subtotal >= 499.0 || cartItems.isEmpty()) 0.0 else 40.0
    val totalAmount = subtotal + deliveryFee

    var customerName by remember { mutableStateOf(userProfile?.name ?: "") }
    var customerPhone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var shippingAddress by remember { mutableStateOf(userProfile?.address ?: "") }
    var city by remember { mutableStateOf(userProfile?.city ?: "") }
    var postalCode by remember { mutableStateOf(userProfile?.postalCode ?: "") }
    var orderNotes by remember { mutableStateOf("") }

    var selectedPaymentMethod by remember { mutableStateOf("COD") } // "COD" or "UPI"
    var upiRefId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyPrimary)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("checkout_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Checkout & Payment",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(text = errorMessage ?: "", color = Color(0xFFD32F2F), fontSize = 13.sp)
                }
            }

            // 1. Delivery Address Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Delivery Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; errorMessage = null },
                        label = { Text("Recipient Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it; errorMessage = null },
                        label = { Text("Mobile Contact Number *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = shippingAddress,
                        onValueChange = { shippingAddress = it; errorMessage = null },
                        label = { Text("Street Address, House/Flat No, Landmark *") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("checkout_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it; errorMessage = null },
                            label = { Text("City *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("checkout_city_input")
                        )
                        OutlinedTextField(
                            value = postalCode,
                            onValueChange = { postalCode = it; errorMessage = null },
                            label = { Text("PIN Code *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("checkout_pincode_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = { orderNotes = it },
                        label = { Text("Delivery Instructions / Notes (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 2. Payment Method Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Select Payment Method",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option A: Cash on Delivery (COD)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedPaymentMethod == "COD") Color(0xFFE3F2FD) else Color(0xFFF8FAFC))
                            .clickable { selectedPaymentMethod = "COD" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == "COD",
                            onClick = { selectedPaymentMethod = "COD" },
                            colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.LocalAtm,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cash on Delivery (COD)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Pay with cash or scan delivery agent's QR upon arrival",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option B: UPI Payment
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedPaymentMethod == "UPI") Color(0xFFE3F2FD) else Color(0xFFF8FAFC))
                            .clickable { selectedPaymentMethod = "UPI" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPaymentMethod == "UPI",
                            onClick = { selectedPaymentMethod = "UPI" },
                            colors = RadioButtonDefaults.colors(selectedColor = NavyPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UPI Payment (GPay, PhonePe, Paytm)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Instant UPI transfer to Pramod Book Store VPA",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // UPI Details Expansion
                    if (selectedPaymentMethod == "UPI") {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Store UPI ID: pramodbooks@upi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Pay exact amount ₹${totalAmount.toInt()} via any UPI app, then paste your 12-digit UPI Reference / UTR Number below:",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = upiRefId,
                                    onValueChange = { upiRefId = it; errorMessage = null },
                                    label = { Text("12-Digit UPI Transaction / UTR ID") },
                                    placeholder = { Text("e.g. 431289104523") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().testTag("upi_ref_input")
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = AmberTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Online payment verification is verified before shipment.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Order Summary & Place Order Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Order Total",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Items Subtotal", color = Color.Gray, fontSize = 13.sp)
                        Text("₹${subtotal.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", color = Color.Gray, fontSize = 13.sp)
                        Text(
                            text = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (deliveryFee == 0.0) GreenSuccess else Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("₹${totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NavyPrimary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (customerName.isBlank() || customerPhone.isBlank() || shippingAddress.isBlank() || city.isBlank() || postalCode.isBlank()) {
                                errorMessage = "Please fill in all required delivery details (Name, Phone, Address, City, PIN Code)."
                                return@Button
                            }
                            if (selectedPaymentMethod == "UPI" && upiRefId.trim().length < 6) {
                                errorMessage = "Please enter a valid UPI Reference / UTR Number for verification."
                                return@Button
                            }

                            isPlacingOrder = true
                            errorMessage = null

                            val orderItemsList = cartItems.map {
                                mapOf(
                                    "productId" to it.productId,
                                    "name" to it.productName,
                                    "price" to it.productPrice,
                                    "quantity" to it.quantity,
                                    "imageUrl" to it.productImageUrl
                                )
                            }

                            val newOrder = Order(
                                userId = userProfile?.userId ?: "",
                                customerName = customerName.trim(),
                                customerEmail = userProfile?.email ?: "",
                                customerPhone = customerPhone.trim(),
                                shippingAddress = shippingAddress.trim(),
                                city = city.trim(),
                                postalCode = postalCode.trim(),
                                paymentMethod = selectedPaymentMethod,
                                paymentStatus = if (selectedPaymentMethod == "COD") "PENDING" else "VERIFIED",
                                upiTransactionId = if (selectedPaymentMethod == "UPI") upiRefId.trim() else null,
                                orderStatus = "Order Placed",
                                items = orderItemsList,
                                totalAmount = totalAmount,
                                itemsCount = cartItems.sumOf { it.quantity },
                                orderNotes = orderNotes.trim()
                            )

                            onPlaceOrder(newOrder)
                        },
                        enabled = !isPlacingOrder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_order_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(
                                text = "Confirm & Place Order (₹${totalAmount.toInt()})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

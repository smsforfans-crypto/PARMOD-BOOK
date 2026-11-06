package com.example.pramodstore.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pramodstore.data.model.UserProfile
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.NavyPrimary

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    isAdmin: Boolean,
    isGuest: Boolean = false,
    storeEmail: String = "rohanguptamorwabazar@gmail.com",
    storePhone: String = "",
    onSaveProfile: (UserProfile) -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToContact: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onSignOut: () -> Unit,
    onSignInRequest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(userProfile?.name ?: "") }
    var editPhone by remember { mutableStateOf(userProfile?.phone ?: "") }
    var editAddress by remember { mutableStateOf(userProfile?.address ?: "") }
    var editCity by remember { mutableStateOf(userProfile?.city ?: "") }
    var editPincode by remember { mutableStateOf(userProfile?.postalCode ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (isGuest) {
            // Guest User Card
            Card(
                modifier = Modifier.fillMaxWidth().testTag("guest_profile_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Guest Visitor",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Browsing Mode Active",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You are browsing the bookstore catalog. Sign in to place orders, save delivery addresses, and track shipments.",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onSignInRequest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("guest_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In with Google / Email", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            // Logged-in User Profile Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (userProfile?.name?.take(1)?.uppercase() ?: userProfile?.email?.take(1)?.uppercase() ?: "P"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile?.name?.ifBlank { "Bookstore Customer" } ?: "Bookstore Customer",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AmberTertiary, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ADMIN", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = userProfile?.email ?: "",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        if (!userProfile?.phone.isNullOrBlank()) {
                            Text(
                                text = "📞 ${userProfile?.phone}",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            editName = userProfile?.name ?: ""
                            editPhone = userProfile?.phone ?: ""
                            editAddress = userProfile?.address ?: ""
                            editCity = userProfile?.city ?: ""
                            editPincode = userProfile?.postalCode ?: ""
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.testTag("edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = NavyPrimary
                        )
                    }
                }
            }
        }

        // Admin Access Tile (if admin)
        if (isAdmin && !isGuest) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdmin() }
                    .testTag("admin_dashboard_tile"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AmberTertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Store Dashboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = "Add products, edit contact info, manage stock & orders",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AmberTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Account & Shopping Shortcuts Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                ProfileOptionRow(
                    icon = Icons.Default.ReceiptLong,
                    title = "My Orders & Shipment Tracking",
                    subtitle = "View order status, invoices & cancel orders",
                    onClick = {
                        if (isGuest) onSignInRequest() else onNavigateToOrders()
                    }
                )
                Divider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(
                    icon = Icons.Default.Favorite,
                    title = "My Saved Wishlist",
                    subtitle = "Books & sporting goods saved for later",
                    onClick = {
                        if (isGuest) onSignInRequest() else onNavigateToWishlist()
                    }
                )
            }
        }

        // Saved Delivery Address Card (if logged in)
        if (!isGuest) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Default Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (userProfile?.address.isNullOrBlank()) {
                        Text(
                            text = "No address saved yet. Tap 'Edit Profile' to add your street address, city and PIN code for 1-click checkout.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    } else {
                        Text(
                            text = "${userProfile?.address}\n${userProfile?.city} - ${userProfile?.postalCode}",
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Store Legal & Customer Support Links Card (Requirements 2, 3, 4, 5, 6)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                Text(
                    text = "Store & Customer Service",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp)
                )

                ProfileOptionRow(
                    icon = Icons.Default.Storefront,
                    title = "About PRAMOD BOOK STORE",
                    subtitle = "Our Morwa Bazar heritage, quality books & sports gear",
                    onClick = onNavigateToAbout
                )
                Divider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(
                    icon = Icons.Default.Email,
                    title = "Contact Us & Online Enquiry",
                    subtitle = "Support: $storeEmail",
                    onClick = onNavigateToContact
                )
                Divider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy (गोपनीयता नीति)",
                    subtitle = "Data collection, Firebase security & account deletion",
                    onClick = onNavigateToPrivacy
                )
                Divider(color = Color(0xFFF1F5F9))
                ProfileOptionRow(
                    icon = Icons.Default.Description,
                    title = "Terms & Conditions (नियम और शर्तें)",
                    subtitle = "Orders, COD, UPI payment, delivery & 7-day returns",
                    onClick = onNavigateToTerms
                )
            }
        }

        // Future Phone Number Upgrade Architecture (Section 3 of prompt)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Future Phone Login Upgrade Ready",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = NavyPrimary
                    )
                    Text(
                        text = "The authentication system is modular. In v2.0 update, Phone Number OTP Login will link directly without duplicate accounts.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E3A8A),
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Sign In or Sign Out Button
        if (isGuest) {
            Button(
                onClick = onSignInRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("profile_login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In with Google or Email", fontWeight = FontWeight.SemiBold)
            }
        } else {
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Customer Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Contact Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("Delivery Address") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editCity,
                            onValueChange = { editCity = it },
                            label = { Text("City") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editPincode,
                            onValueChange = { editPincode = it },
                            label = { Text("PIN Code") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = (userProfile ?: UserProfile(userId = "")).copy(
                            name = editName.trim(),
                            phone = editPhone.trim(),
                            address = editAddress.trim(),
                            city = editCity.trim(),
                            postalCode = editPincode.trim()
                        )
                        onSaveProfile(updated)
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1E293B))
            Text(text = subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}

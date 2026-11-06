package com.example.pramodstore.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyPrimary

@Composable
fun TermsAndConditionsScreen(
    supportEmail: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var selectedLanguageTab by remember { mutableIntStateOf(0) } // 0: English, 1: Hindi

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyPrimary)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("terms_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedLanguageTab == 0) "Terms and Conditions" else "नियम और शर्तें",
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
        ) {
            TabRow(
                selectedTabIndex = selectedLanguageTab,
                containerColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedLanguageTab == 0,
                    onClick = { selectedLanguageTab = 0 },
                    text = { Text("English", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedLanguageTab == 1,
                    onClick = { selectedLanguageTab = 1 },
                    text = { Text("हिंदी (Hindi)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (selectedLanguageTab == 0) {
                    // ENGLISH TERMS
                    TermsSectionCard(
                        icon = Icons.Default.Gavel,
                        title = "1. Agreement to Terms",
                        content = "By placing an order or using the PRAMOD BOOK STORE mobile app, you agree to be bound by these Terms and Conditions. Please review them carefully before completing any transaction."
                    )

                    TermsSectionCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "2. Product Availability & Pricing",
                        content = "• All prices are quoted in Indian Rupees (INR ₹) and are inclusive of all applicable taxes.\n• We endeavor to maintain accurate inventory levels. In case an ordered book or sports item is unexpectedly out of stock, we will notify you promptly and provide a full refund if payment was processed."
                    )

                    TermsSectionCard(
                        icon = Icons.Default.Payment,
                        title = "3. Payment Modes & Verification",
                        content = "• Cash on Delivery (COD): Pay upon delivery of goods.\n• UPI Payment: Payments made via Google Pay, PhonePe, Paytm, or BHIM must include a valid 12-digit transaction UTR number. Online payments are verified with our banking ledger prior to dispatch."
                    )

                    TermsSectionCard(
                        icon = Icons.Default.LocalShipping,
                        title = "4. Shipping & Delivery Policy",
                        content = "• Standard delivery time is 2 to 5 business days across local and regional postal zones.\n• Orders above ₹499 qualify for FREE shipping. For smaller orders, a standard delivery fee of ₹40 applies."
                    )

                    TermsSectionCard(
                        icon = Icons.Default.Cancel,
                        title = "5. Order Cancellation",
                        content = "Customers may cancel an order free of charge directly within the app as long as the status is 'Order Placed'. Once an order transitions to 'Shipped', it cannot be cancelled in transit."
                    )

                    TermsSectionCard(
                        icon = Icons.Default.AssignmentReturn,
                        title = "6. Returns, Replacements & Refunds",
                        content = "• We offer a 7-day replacement guarantee for items received in damaged condition, defective binding, missing book pages, or manufacturing defects in sporting equipment (e.g., cricket bats, carrom boards).\n• To request an exchange or refund, contact us at $supportEmail with photo proof of the package within 7 days of delivery."
                    )
                } else {
                    // HINDI TERMS
                    TermsSectionCard(
                        icon = Icons.Default.Gavel,
                        title = "1. नियमों की स्वीकृति (Agreement to Terms)",
                        content = "प्रमोद बुक स्टोर ऐप का उपयोग करने या ऑर्डर देने पर आप हमारे नियमों और शर्तों से सहमत होते हैं।"
                    )

                    TermsSectionCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "2. मूल्य और उत्पाद उपलब्धता (Pricing & Availability)",
                        content = "• सभी कीमतें भारतीय रुपये (INR ₹) में हैं और सभी करों सहित हैं।\n• स्टॉक समाप्त होने की स्थिति में ग्राहक को तुरंत सूचित किया जाएगा और पूर्व-भुगतान होने पर पूरी राशि वापस की जाएगी।"
                    )

                    TermsSectionCard(
                        icon = Icons.Default.Payment,
                        title = "3. भुगतान के तरीके (Payment Methods)",
                        content = "• कैश ऑन डिलीवरी (COD): डिलीवरी के समय नकद भुगतान करें।\n• यूपीआई (UPI): GPay, PhonePe, Paytm द्वारा भुगतान करने पर मान्य 12-अंकीय लेन-देन संख्या (UTR) दर्ज करें। सत्यापन के बाद ही ऑर्डर भेजा जाता है।"
                    )

                    TermsSectionCard(
                        icon = Icons.Default.LocalShipping,
                        title = "4. डिलीवरी नीति (Shipping & Delivery)",
                        content = "• सामान्य डिलीवरी समय 2 से 5 कार्य दिवस है।\n• ₹499 से अधिक के ऑर्डर पर मुफ़्त डिलीवरी उपलब्ध है।"
                    )

                    TermsSectionCard(
                        icon = Icons.Default.Cancel,
                        title = "5. ऑर्डर रद्दीकरण (Order Cancellation)",
                        content = "जब तक ऑर्डर 'Order Placed' स्थिति में है, आप ऐप से सीधे बिना किसी शुल्क के ऑर्डर रद्द कर सकते हैं।"
                    )

                    TermsSectionCard(
                        icon = Icons.Default.AssignmentReturn,
                        title = "6. वापसी और रिफंड नीति (Returns & Refunds)",
                        content = "• कटी-फटी किताब या दोषपूर्ण खेल उपकरण मिलने पर 7 दिनों के भीतर निःशुल्क प्रतिस्थापन (रिप्लेसमेंट) दिया जाता है।\n• सहायता हेतु हमें $supportEmail पर संपर्क करें।"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun TermsSectionCard(
    icon: ImageVector,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 13.sp,
                color = Color(0xFF334155),
                lineHeight = 20.sp
            )
        }
    }
}

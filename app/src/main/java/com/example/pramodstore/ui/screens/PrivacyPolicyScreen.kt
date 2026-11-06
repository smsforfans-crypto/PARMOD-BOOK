package com.example.pramodstore.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NavyPrimary

@Composable
fun PrivacyPolicyScreen(
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
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("privacy_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (selectedLanguageTab == 0) "Privacy Policy" else "गोपनीयता नीति",
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
            // Language Toggle Tab
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
                    // ENGLISH PRIVACY POLICY
                    PrivacySectionCard(
                        icon = Icons.Default.PrivacyTip,
                        title = "1. Introduction",
                        content = "Welcome to PRAMOD BOOK STORE. We respect your personal privacy and are committed to protecting the personal information you share with us while browsing our books, stationery, and sports equipment."
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Shield,
                        title = "2. Information We Collect",
                        content = "• Account Information: When you sign in via Google OAuth or Email, we receive your name, verified email address, and unique user identifier.\n• Order & Delivery Details: Recipient full name, mobile contact number, shipping street address, city, and postal PIN code for delivery fulfillment.\n• Shopping Records: Items placed in shopping cart, wishlist, and past order history.\n• Inquiries: Messages, questions, and contact details submitted via our Contact Us form."
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Lock,
                        title = "3. Firebase Authentication & Cloud Storage",
                        content = "PRAMOD BOOK STORE utilizes Google Firebase Authentication and Google Cloud Firestore. Your credentials are securely managed through Google Identity Services and encrypted tokens. We never store raw passwords or sensitive card credentials on our servers."
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Security,
                        title = "4. Data Protection & Sharing",
                        content = "We do NOT sell, rent, or trade your personal data to third-party marketers. Your delivery address and phone number are used strictly by our logistics partners to deliver your ordered books and sports merchandise. All database transactions are guarded by zero-trust security rules."
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.DeleteForever,
                        title = "5. Account & Data Deletion Rights",
                        content = "You have the right to review, update, or permanently delete your account and personal data at any time. To request complete deletion of your account and associated records, please email our support team at $supportEmail with the subject 'Account Deletion Request'. Your data will be deleted within 7 business days."
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Mail,
                        title = "6. Contact Our Privacy Officer",
                        content = "For questions or concerns regarding our privacy practices, please contact us:\n• Support Email: $supportEmail\n• Store: PRAMOD BOOK STORE, Morwa Bazar, Samastipur, Bihar"
                    )
                } else {
                    // HINDI PRIVACY POLICY
                    PrivacySectionCard(
                        icon = Icons.Default.PrivacyTip,
                        title = "1. परिचय (Introduction)",
                        content = "प्रमोद बुक स्टोर (PRAMOD BOOK STORE) में आपका स्वागत है। हम आपकी व्यक्तिगत गोपनीयता का पूरा सम्मान करते हैं और आपकी निजी जानकारी को सुरक्षित रखने के लिए प्रतिबद्ध हैं।"
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Shield,
                        title = "2. हम क्या जानकारी एकत्र करते हैं (Data Collection)",
                        content = "• खाता जानकारी: जब आप Google या ईमेल से लॉगिन करते हैं, तो आपका नाम, ईमेल पता और यूज़र आईडी प्राप्त होती है।\n• डिलीवरी विवरण: पुस्तकों और खेल सामान की डिलीवरी हेतु ग्राहक का पूरा नाम, मोबाइल नंबर, पता, शहर और पिन कोड।\n• खरीदारी विवरण: कार्ट में जोड़े गए उत्पाद, विशलिस्ट और ऑर्डर इतिहास।\n• पूछताछ: संपर्क फ़ॉर्म के माध्यम से भेजे गए आपके संदेश।"
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Lock,
                        title = "3. फायरबेस प्रमाणीकरण और सुरक्षा (Firebase & Security)",
                        content = "प्रमोद बुक स्टोर Google Firebase प्रमाणीकरण और क्लाउड फायरस्टोर का उपयोग करता है। आपका डेटा सुरक्षित और एन्क्रिप्टेड है। हम कोई भी पासवर्ड या बैंकिंग विवरण अनधिकृत रूप से स्टोर नहीं करते हैं।"
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Security,
                        title = "4. डेटा सुरक्षा और गोपनीयता (Data Protection)",
                        content = "हम आपका व्यक्तिगत डेटा किसी भी तीसरे पक्ष या विज्ञापनदाता को नहीं बेचते हैं। आपकी संपर्क जानकारी का उपयोग केवल ऑर्डर डिलीवरी और ग्राहक सहायता के लिए किया जाता है।"
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.DeleteForever,
                        title = "5. खाता हटाने का अधिकार (Account Deletion)",
                        content = "आपको किसी भी समय अपने खाते और व्यक्तिगत डेटा को स्थायी रूप से हटाने का पूरा अधिकार है। खाता हटाने के लिए कृपया हमें $supportEmail पर ईमेल करें। 7 कार्य दिवसों में आपका सारा डेटा हटा दिया जाएगा।"
                    )

                    PrivacySectionCard(
                        icon = Icons.Default.Mail,
                        title = "6. संपर्क करें (Contact)",
                        content = "गोपनीयता नीति से संबंधित किसी भी प्रश्न के लिए संपर्क करें:\n• सहायता ईमेल: $supportEmail\n• पता: प्रमोद बुक स्टोर, मोरवा बाज़ार, समस्तीपुर, बिहार"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PrivacySectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

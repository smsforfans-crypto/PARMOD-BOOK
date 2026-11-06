package com.example.pramodstore.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pramodstore.data.model.Inquiry
import com.example.pramodstore.data.model.Order
import com.example.pramodstore.data.model.Product
import com.example.pramodstore.data.model.StoreCategory
import com.example.pramodstore.data.model.StoreConfig
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    products: List<Product>,
    orders: List<Order>,
    categories: List<StoreCategory>,
    storeConfig: StoreConfig? = null,
    inquiries: List<Inquiry> = emptyList(),
    onBackClick: () -> Unit,
    onAddProduct: (Product) -> Unit,
    onUpdateProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onUpdatePaymentStatus: (String, String) -> Unit,
    onSeedCatalog: () -> Unit,
    onSaveStoreConfig: (StoreConfig) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler { onBackClick() }

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Products, 1: Orders, 2: Categories, 3: Settings & Inquiries
    var showAddProductDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val totalSales = orders.filter { it.orderStatus != "Cancelled" }.sumOf { it.totalAmount }
    val lowStockCount = products.count { it.stockQuantity <= 5 }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("admin_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Admin Store Dashboard",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "PRAMOD BOOK STORE Management",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedAdminTab == 0) {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = NavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_product")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            // KPI Summary Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(
                    title = "Total Sales",
                    value = "₹${totalSales.toInt()}",
                    subtitle = "${orders.size} orders",
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    title = "Catalog Items",
                    value = "${products.size}",
                    subtitle = "$lowStockCount low stock",
                    color = if (lowStockCount > 0) AmberTertiary else NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Scrollable Tabs: Products | Orders | Categories | Store Settings
            ScrollableTabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = Color.White,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = { Text("Products (${products.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = { Text("Orders (${orders.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = { selectedAdminTab = 2 },
                    text = { Text("Categories", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedAdminTab == 3,
                    onClick = { selectedAdminTab = 3 },
                    text = { Text("Settings & Enquiries (${inquiries.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }

            // Tab Content
            when (selectedAdminTab) {
                0 -> AdminProductsTab(
                    products = products,
                    onEdit = { productToEdit = it },
                    onDelete = { productToDelete = it }
                )
                1 -> AdminOrdersTab(
                    orders = orders,
                    onUpdateStatus = onUpdateOrderStatus,
                    onUpdatePaymentStatus = onUpdatePaymentStatus
                )
                2 -> AdminCategoriesTab(
                    categories = categories,
                    onSeedCatalog = onSeedCatalog
                )
                3 -> AdminSettingsTab(
                    storeConfig = storeConfig,
                    inquiries = inquiries,
                    onSaveStoreConfig = onSaveStoreConfig
                )
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        ProductFormDialog(
            title = "Add New Product",
            initialProduct = null,
            categories = categories,
            onDismiss = { showAddProductDialog = false },
            onConfirm = { newProduct ->
                onAddProduct(newProduct)
                showAddProductDialog = false
            }
        )
    }

    // Edit Product Dialog
    if (productToEdit != null) {
        ProductFormDialog(
            title = "Edit Product Details",
            initialProduct = productToEdit,
            categories = categories,
            onDismiss = { productToEdit = null },
            onConfirm = { updated ->
                onUpdateProduct(updated)
                productToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete '${productToDelete?.name}' from the store catalog?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.id?.let { onDeleteProduct(it) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun AdminProductsTab(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    if (products.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products in catalog yet. Tap '+' to add.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products, key = { it.id }) { product ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("admin_product_card_${product.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80" })
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = product.category,
                            fontSize = 11.sp,
                            color = NavyPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${product.price.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stock: ${product.stockQuantity}",
                                fontSize = 11.sp,
                                color = if (product.stockQuantity <= 5) AmberTertiary else Color.Gray,
                                fontWeight = if (product.stockQuantity <= 5) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    IconButton(onClick = { onEdit(product) }, modifier = Modifier.testTag("admin_edit_${product.id}")) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary)
                    }

                    IconButton(onClick = { onDelete(product) }, modifier = Modifier.testTag("admin_delete_${product.id}")) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrdersTab(
    orders: List<Order>,
    onUpdateStatus: (String, String) -> Unit,
    onUpdatePaymentStatus: (String, String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customer orders placed yet.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            var statusMenuExpanded by remember { mutableStateOf(false) }
            val statusOptions = listOf("Order Placed", "Confirmed", "Processing", "Shipped", "Delivered", "Cancelled")

            Card(
                modifier = Modifier.fillMaxWidth().testTag("admin_order_card_${order.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order #${order.id.takeLast(8).uppercase()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "₹${order.totalAmount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = NavyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155)
                    )
                    Text(
                        text = "Address: ${order.shippingAddress}, ${order.city} - ${order.postalCode}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    if (!order.upiTransactionId.isNullOrBlank()) {
                        Text(
                            text = "UPI Ref ID: ${order.upiTransactionId}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AmberTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current status button opening dropdown
                        Box {
                            OutlinedButton(
                                onClick = { statusMenuExpanded = true },
                                modifier = Modifier.height(36.dp).testTag("order_status_dropdown_${order.id}"),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text("Status: ${order.orderStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            DropdownMenu(
                                expanded = statusMenuExpanded,
                                onDismissRequest = { statusMenuExpanded = false }
                            ) {
                                statusOptions.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status) },
                                        onClick = {
                                            onUpdateStatus(order.id, status)
                                            statusMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Payment Status Toggle
                        TextButton(
                            onClick = {
                                val nextPayment = if (order.paymentStatus == "VERIFIED") "PENDING" else "VERIFIED"
                                onUpdatePaymentStatus(order.id, nextPayment)
                            }
                        ) {
                            Text(
                                text = "Payment: ${order.paymentStatus}",
                                color = if (order.paymentStatus == "VERIFIED") GreenSuccess else AmberTertiary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoriesTab(
    categories: List<StoreCategory>,
    onSeedCatalog: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Store Product Categories (${categories.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(10.dp))

                categories.forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = cat.name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Catalog Inventory Management",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Ensure all 8 required categories (School Books, Notebooks, Stationery, Cricket Bats, Cricket Accessories, Sports Items, Carrom Boards, Other Educational Products) are stocked.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSeedCatalog,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-seed Sample Bookstore Catalog")
                }
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    title: String,
    initialProduct: Product?,
    categories: List<StoreCategory>,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: if (categories.isNotEmpty()) categories.first().name else "School Books") }
    var priceStr by remember { mutableStateOf(initialProduct?.price?.toInt()?.toString() ?: "") }
    var originalPriceStr by remember { mutableStateOf(initialProduct?.originalPrice?.toInt()?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(initialProduct?.stockQuantity?.toString() ?: "10") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var imageUrl by remember { mutableStateOf(initialProduct?.imageUrl ?: "") }
    var inStock by remember { mutableStateOf(initialProduct?.inStock ?: true) }
    var featured by remember { mutableStateOf(initialProduct?.featured ?: false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input")
                )

                // Category Selection
                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { categoryDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    category = cat.name
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (₹) *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("product_price_input")
                    )
                    OutlinedTextField(
                        value = originalPriceStr,
                        onValueChange = { originalPriceStr = it },
                        label = { Text("MRP (₹)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text("Stock Quantity *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("product_stock_input")
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Photo Image URL") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = inStock, onCheckedChange = { inStock = it })
                    Text("In Stock Availability", fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = featured, onCheckedChange = { featured = it })
                    Text("Featured on Home Screen", fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && priceStr.isNotBlank()) {
                        val priceVal = priceStr.toDoubleOrNull() ?: 0.0
                        val origPriceVal = originalPriceStr.toDoubleOrNull()
                        val stockVal = stockStr.toIntOrNull() ?: 10
                        val finalProduct = (initialProduct ?: Product(id = "")).copy(
                            name = name.trim(),
                            category = category,
                            price = priceVal,
                            originalPrice = origPriceVal,
                            stockQuantity = stockVal,
                            description = description.trim(),
                            imageUrl = imageUrl.trim(),
                            inStock = inStock && stockVal > 0,
                            featured = featured
                        )
                        onConfirm(finalProduct)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdminSettingsTab(
    storeConfig: StoreConfig?,
    inquiries: List<Inquiry>,
    onSaveStoreConfig: (StoreConfig) -> Unit
) {
    var email by remember(storeConfig) { mutableStateOf(storeConfig?.supportEmail ?: "rohanguptamorwabazar@gmail.com") }
    var phone by remember(storeConfig) { mutableStateOf(storeConfig?.supportPhone ?: "") }
    var address by remember(storeConfig) { mutableStateOf(storeConfig?.storeAddress ?: "PRAMOD BOOK STORE, Main Road, Morwa Bazar, Samastipur, Bihar - 848121") }
    var hours by remember(storeConfig) { mutableStateOf(storeConfig?.storeHours ?: "Monday to Sunday: 9:00 AM - 9:30 PM") }
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Store Contact & Policy Editor (Requirement 7)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Store Contact Details & Public Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Authorized admin settings: Updates reflect immediately across Contact Us, About Us, and Terms pages.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                if (isSaved) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text("✓ Store contact information updated successfully!", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; isSaved = false },
                    label = { Text("Support Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_support_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it; isSaved = false },
                    label = { Text("Support Phone (Leave blank or add number)") },
                    placeholder = { Text("e.g. +91 9876543210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_support_phone_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it; isSaved = false },
                    label = { Text("Physical Store Address") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("admin_store_address_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it; isSaved = false },
                    label = { Text("Store Operating Hours") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val updated = (storeConfig ?: StoreConfig(id = "store_config")).copy(
                            supportEmail = email.trim(),
                            supportPhone = phone.trim(),
                            storeAddress = address.trim(),
                            storeHours = hours.trim()
                        )
                        onSaveStoreConfig(updated)
                        isSaved = true
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_store_settings_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save Store Settings", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Customer Inquiries Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Customer Inquiries (${inquiries.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (inquiries.isEmpty()) {
                    Text(
                        text = "No customer inquiries received yet. Submitted messages from the Contact Us enquiry form will appear here.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )
                } else {
                    inquiries.forEach { inq ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = inq.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        text = inq.createdAt?.toDate()?.let {
                                            SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(it)
                                        } ?: "Recent",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "✉️ ${inq.email}" + if (inq.phone.isNotBlank()) " | 📞 ${inq.phone}" else "",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Subject: ${inq.subject}",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = NavyPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = inq.message,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


package com.example.pramodstore.data.repository

import android.content.Context
import com.example.pramodstore.data.model.Product
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(
                context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            )
        )
    )

    private val collectionPath = "products"
    private val collection = db.collection(collectionPath)

    fun observeProducts(): Flow<List<Product>> = flow {
        collection.snapshots()
            .map { snapshot ->
                if (snapshot.isEmpty) defaultProducts
                else snapshot.toObjects(Product::class.java)
            }
            .catch {
                emit(defaultProducts)
            }
            .collect { emit(it) }
    }

    suspend fun getProductById(id: String): Result<Product?> = try {
        val doc = collection.document(id).get().await()
        Result.success(doc.toObject(Product::class.java))
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.READ, "$collectionPath/$id")
        Result.failure(e)
    }

    suspend fun addProduct(product: Product): Result<String> = try {
        val docRef = if (product.id.isNotBlank()) collection.document(product.id) else collection.document()
        val data = hashMapOf<String, Any?>(
            "name" to product.name,
            "category" to product.category,
            "price" to product.price,
            "originalPrice" to product.originalPrice,
            "description" to product.description,
            "imageUrl" to product.imageUrl,
            "inStock" to product.inStock,
            "stockQuantity" to product.stockQuantity,
            "rating" to product.rating,
            "reviewCount" to product.reviewCount,
            "featured" to product.featured,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        docRef.set(data).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, collectionPath)
        Result.failure(e)
    }

    suspend fun updateProduct(product: Product): Result<Unit> = try {
        require(product.id.isNotBlank()) { "Product ID cannot be blank for update" }
        val docRef = collection.document(product.id)
        val data = hashMapOf<String, Any?>(
            "name" to product.name,
            "category" to product.category,
            "price" to product.price,
            "originalPrice" to product.originalPrice,
            "description" to product.description,
            "imageUrl" to product.imageUrl,
            "inStock" to product.inStock,
            "stockQuantity" to product.stockQuantity,
            "rating" to product.rating,
            "reviewCount" to product.reviewCount,
            "featured" to product.featured,
            "createdAt" to (product.createdAt ?: FieldValue.serverTimestamp()),
            "updatedAt" to FieldValue.serverTimestamp()
        ).filterValues { it != null }

        docRef.set(data).await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "$collectionPath/${product.id}")
        Result.failure(e)
    }

    suspend fun deleteProduct(id: String): Result<Unit> = try {
        collection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, "$collectionPath/$id")
        Result.failure(e)
    }

    val defaultProducts = listOf(
                // School Books
                Product(
                    id = "sb_ncert_math_10",
                    name = "NCERT Mathematics Class 10 (English)",
                    category = "School Books",
                    price = 160.0,
                    originalPrice = 175.0,
                    description = "Official NCERT textbook for CBSE Class 10 Mathematics. Complete syllabus covering Real Numbers, Polynomials, Quadratic Equations, Trigonometry, and Statistics.",
                    imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80",
                    inStock = true,
                    stockQuantity = 45,
                    rating = 4.8,
                    reviewCount = 64,
                    featured = true
                ),
                Product(
                    id = "sb_ncert_science_10",
                    name = "NCERT Science Class 10 (Latest Edition)",
                    category = "School Books",
                    price = 190.0,
                    originalPrice = 210.0,
                    description = "CBSE Board Science textbook for Class 10. Comprehensive concepts in Physics, Chemistry, and Biology with in-chapter questions and laboratory exercises.",
                    imageUrl = "https://images.unsplash.com/photo-1532012164546-f432f2e3777f?w=500&q=80",
                    inStock = true,
                    stockQuantity = 38,
                    rating = 4.7,
                    reviewCount = 52,
                    featured = true
                ),
                Product(
                    id = "sb_rd_sharma_9",
                    name = "R.D. Sharma Mathematics for Class 9",
                    category = "School Books",
                    price = 485.0,
                    originalPrice = 540.0,
                    description = "In-depth reference book for CBSE Class 9 Mathematics by Dr. R.D. Sharma. Includes thousands of solved problems, exercise questions, and MCQs for Olympiad preparation.",
                    imageUrl = "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=500&q=80",
                    inStock = true,
                    stockQuantity = 20,
                    rating = 4.9,
                    reviewCount = 89,
                    featured = false
                ),

                // Notebooks
                Product(
                    id = "nb_classmate_long_172",
                    name = "Classmate Long Notebook 172 Pages (Ruled) - Pack of 6",
                    category = "Notebooks",
                    price = 320.0,
                    originalPrice = 360.0,
                    description = "Eco-friendly, chlorine-free paper notebook with smooth writing feel. 54 GSM bright paper with engaging activity pages on the back cover.",
                    imageUrl = "https://images.unsplash.com/photo-1517842645767-c639042777db?w=500&q=80",
                    inStock = true,
                    stockQuantity = 75,
                    rating = 4.6,
                    reviewCount = 112,
                    featured = true
                ),
                Product(
                    id = "nb_spiral_a4_200",
                    name = "A4 Premium Spiral Bound Ruled Notebook 200 Pages",
                    category = "Notebooks",
                    price = 145.0,
                    originalPrice = 175.0,
                    description = "Heavy duty polypropylene frosted cover, micro-perforated sheets, sturdy dual-wire binding. Ideal for college students and coaching notes.",
                    imageUrl = "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=500&q=80",
                    inStock = true,
                    stockQuantity = 50,
                    rating = 4.5,
                    reviewCount = 33,
                    featured = false
                ),
                Product(
                    id = "nb_drawing_book_40",
                    name = "Classmate Drawing Book 40 Pages (Cartridge Paper)",
                    category = "Notebooks",
                    price = 70.0,
                    originalPrice = 85.0,
                    description = "Heavy GSM acid-free drawing sheets suitable for pencil sketching, crayons, water color, and poster color paints without bleed-through.",
                    imageUrl = "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=500&q=80",
                    inStock = true,
                    stockQuantity = 40,
                    rating = 4.7,
                    reviewCount = 28,
                    featured = false
                ),

                // Stationery
                Product(
                    id = "st_doms_geometry_box",
                    name = "DOMS Mathematical Geometry Instrument Box",
                    category = "Stationery",
                    price = 120.0,
                    originalPrice = 140.0,
                    description = "High precision compass, divider, 15cm ruler, 45 & 60 degree set squares, protractor, eraser, sharpener, and mechanical pencil in a rust-proof tin case.",
                    imageUrl = "https://images.unsplash.com/photo-1585776245991-cf89dd7fc73a?w=500&q=80",
                    inStock = true,
                    stockQuantity = 60,
                    rating = 4.6,
                    reviewCount = 47,
                    featured = true
                ),
                Product(
                    id = "st_parker_vector_pen",
                    name = "Parker Vector Matte Black Rollerball Pen with Chrome Trim",
                    category = "Stationery",
                    price = 375.0,
                    originalPrice = 450.0,
                    description = "Iconic arrow clip design, ultra-smooth FreeInk rollerball technology for effortless handwriting. Comes in an elegant gift box with genuine Parker ink refill.",
                    imageUrl = "https://images.unsplash.com/photo-1583485088034-697b5bc54ccd?w=500&q=80",
                    inStock = true,
                    stockQuantity = 25,
                    rating = 4.8,
                    reviewCount = 95,
                    featured = true
                ),
                Product(
                    id = "st_faber_oil_pastels",
                    name = "Faber-Castell 25 Shades Triangular Oil Pastels Set",
                    category = "Stationery",
                    price = 150.0,
                    originalPrice = 175.0,
                    description = "Vibrant, rich, and creamy pastels with ergonomic triangular grip. Includes scraping tool and blending white shade for creative shading.",
                    imageUrl = "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=500&q=80",
                    inStock = true,
                    stockQuantity = 30,
                    rating = 4.7,
                    reviewCount = 41,
                    featured = false
                ),

                // Cricket Bats
                Product(
                    id = "cb_mrf_genius_bat",
                    name = "MRF Genius Grand Edition English Willow Cricket Bat",
                    category = "Cricket Bats",
                    price = 4499.0,
                    originalPrice = 5299.0,
                    description = "Crafted from selected Grade English Willow. Features massive edges, curved blade, lightweight pickup, and treble spring cane handle for superior shock absorption.",
                    imageUrl = "https://images.unsplash.com/photo-1531415074868-83633dd5e45c?w=500&q=80",
                    inStock = true,
                    stockQuantity = 8,
                    rating = 4.9,
                    reviewCount = 73,
                    featured = true
                ),
                Product(
                    id = "cb_ss_ton_kashmir",
                    name = "SS Ton Master Kashmir Willow Full Size Cricket Bat",
                    category = "Cricket Bats",
                    price = 1850.0,
                    originalPrice = 2200.0,
                    description = "Air-dried premium Kashmir Willow with thick middle and toe protection. Ideal for leather ball club matches, tournament play, and serious practice sessions.",
                    imageUrl = "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?w=500&q=80",
                    inStock = true,
                    stockQuantity = 15,
                    rating = 4.7,
                    reviewCount = 58,
                    featured = true
                ),

                // Cricket Accessories
                Product(
                    id = "ca_sg_club_ball",
                    name = "SG Club Red Leather Cricket Ball (Pack of 2)",
                    category = "Cricket Accessories",
                    price = 780.0,
                    originalPrice = 900.0,
                    description = "Four-piece leather ball made from top-grade alum tanned leather. High seam prominence, cork rubber nucleus, water-proofed finish suitable for 50-over matches.",
                    imageUrl = "https://images.unsplash.com/photo-1624526267942-ab0ff8a3e972?w=500&q=80",
                    inStock = true,
                    stockQuantity = 40,
                    rating = 4.8,
                    reviewCount = 82,
                    featured = false
                ),
                Product(
                    id = "ca_ss_batting_gloves",
                    name = "SS Platino Mens Cricket Batting Gloves",
                    category = "Cricket Accessories",
                    price = 899.0,
                    originalPrice = 1100.0,
                    description = "Sheep leather palm for non-slip grip, multi-section high-density foam finger rolls with fiber inserts on leading fingers, elasticated wristband with velcro fastener.",
                    imageUrl = "https://images.unsplash.com/photo-1593341646782-e0b495cff86d?w=500&q=80",
                    inStock = true,
                    stockQuantity = 18,
                    rating = 4.6,
                    reviewCount = 36,
                    featured = false
                ),

                // Sports Items
                Product(
                    id = "sp_nivia_storm_football",
                    name = "Nivia Storm Size 5 Rubber Molded Football",
                    category = "Sports Items",
                    price = 599.0,
                    originalPrice = 720.0,
                    description = "Official size 5 all-weather rubber molded football. 32 panel construction with butyl bladder for optimal shape retention on hard grounds and turf.",
                    imageUrl = "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=500&q=80",
                    inStock = true,
                    stockQuantity = 22,
                    rating = 4.5,
                    reviewCount = 67,
                    featured = true
                ),
                Product(
                    id = "sp_yonex_badminton_racket",
                    name = "Yonex Nanoray Full Graphite Badminton Racket with Cover",
                    category = "Sports Items",
                    price = 1499.0,
                    originalPrice = 1890.0,
                    description = "Lightweight head-light balance racket with isometric head shape for enlarged sweet spot. Pre-strung with Yonex BG65 string at 24 lbs tension.",
                    imageUrl = "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=500&q=80",
                    inStock = true,
                    stockQuantity = 14,
                    rating = 4.7,
                    reviewCount = 54,
                    featured = true
                ),

                // Carrom Boards
                Product(
                    id = "cb_siscaa_champion_32",
                    name = "Siscaa Champion 32x32 Inch Carrom Board (Waterproof)",
                    category = "Carrom Boards",
                    price = 2899.0,
                    originalPrice = 3500.0,
                    description = "Professional 32 inch carrom board with 3-inch black wooden border, smooth English birch ply playing surface, and woollen pockets. Free coins, striker and disco powder included.",
                    imageUrl = "https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=500&q=80",
                    inStock = true,
                    stockQuantity = 9,
                    rating = 4.8,
                    reviewCount = 44,
                    featured = true
                ),
                Product(
                    id = "cb_carrom_coins_striker",
                    name = "Synco Deluxe Wooden Carrom Coins & Striker Set",
                    category = "Carrom Boards",
                    price = 249.0,
                    originalPrice = 300.0,
                    description = "Standard tournament size wooden coins (11 white, 11 black, 2 red queens) and weighted engraved tournament striker in a transparent storage box.",
                    imageUrl = "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=500&q=80",
                    inStock = true,
                    stockQuantity = 35,
                    rating = 4.6,
                    reviewCount = 39,
                    featured = false
                ),

                // Other Educational Products
                Product(
                    id = "ed_world_globe_8inch",
                    name = "National Geographic 8-Inch Illuminated World Globe",
                    category = "Other Educational Products",
                    price = 1199.0,
                    originalPrice = 1499.0,
                    description = "Detailed physical & political 3D globe with calibrated meridian ring and sturdy metallic brass finish stand. Clear country boundaries, capitals, and time zones.",
                    imageUrl = "https://images.unsplash.com/photo-1521295121783-8a321d551ad2?w=500&q=80",
                    inStock = true,
                    stockQuantity = 16,
                    rating = 4.9,
                    reviewCount = 61,
                    featured = true
                ),
                Product(
                    id = "ed_casio_fx991cw",
                    name = "Casio FX-991CW ClassWiz Non-Programmable Scientific Calculator",
                    category = "Other Educational Products",
                    price = 1595.0,
                    originalPrice = 1695.0,
                    description = "540+ functions high-resolution 4-gradation natural textbook display. Matrix, vector, statistics, calculus, and equation solving for engineering and CBSE students.",
                    imageUrl = "https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?w=500&q=80",
                    inStock = true,
                    stockQuantity = 28,
                    rating = 4.9,
                    reviewCount = 120,
                    featured = true
                )
    )

    suspend fun seedInitialCatalogIfEmpty(): Result<Int> = try {
        if (Firebase.auth.currentUser == null) {
            Result.success(0)
        } else {
            val existing = collection.limit(1).get().await()
            if (existing.isEmpty) {
                for (p in defaultProducts) {
                    addProduct(p)
                }
                Result.success(defaultProducts.size)
            } else {
                Result.success(0)
            }
        }
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, collectionPath)
        Result.failure(e)
    }
}

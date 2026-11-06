const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Public: Anyone can read products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertSucceeds(unauthDb.collection("products").get());
});

test("Unauthenticated: Cannot create product", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("products").add({
    name: "Sample Book",
    category: "School Books",
    price: 299,
    description: "Sample",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Unauthenticated: Cannot read or create orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").get());
  await assertFails(unauthDb.collection("orders").add({
    userId: ALICE_UID,
    customerName: "Alice",
    customerEmail: "alice@example.com",
    shippingAddress: "123 Main St",
    paymentMethod: "COD",
    paymentStatus: "PENDING",
    orderStatus: "Order Placed",
    items: [{ productId: "p1", name: "Book", price: 100, quantity: 1 }],
    totalAmount: 100,
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated user: Can add item to own cart", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("cart").doc("item1").set({
    userId: ALICE_UID,
    productId: "prod_1",
    productName: "Maths Class 10",
    productPrice: 250,
    quantity: 1,
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Cross-user: Bob cannot read Alice's cart", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("cart").doc("item1").set({
      userId: ALICE_UID,
      productId: "prod_1",
      productName: "Maths Class 10",
      productPrice: 250,
      quantity: 1,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("cart").doc("item1").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("cart").get());
});

test("Authenticated user: Can create own order and query own orders", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID, { email: "alice@example.com" }).firestore();
  await assertSucceeds(aliceDb.collection("orders").doc("order_1").set({
    userId: ALICE_UID,
    customerName: "Alice Sharma",
    customerEmail: "alice@example.com",
    shippingAddress: "Station Road, Shop 4",
    paymentMethod: "UPI",
    paymentStatus: "PENDING",
    orderStatus: "Order Placed",
    items: [{ productId: "p1", name: "Notebook", price: 60, quantity: 2 }],
    totalAmount: 120,
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  // Query own orders with filter
  await assertSucceeds(aliceDb.collection("orders").where("userId", "==", ALICE_UID).get());
});

test("Cross-user: Bob cannot read Alice's order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("order_alice").set({
      userId: ALICE_UID,
      customerName: "Alice Sharma",
      customerEmail: "alice@example.com",
      shippingAddress: "Station Road",
      paymentMethod: "COD",
      paymentStatus: "PENDING",
      orderStatus: "Order Placed",
      items: [{ productId: "p1", name: "Notebook", price: 60, quantity: 2 }],
      totalAmount: 120,
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("orders").doc("order_alice").get());
});

test("Admin: Can create product and view all orders", async () => {
  // Add admin record
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("admins").doc(ADMIN_UID).set({
      userId: ADMIN_UID,
      email: "admin@pramod.com",
      role: "admin",
      createdAt: new Date()
    });
  });

  const adminDb = testEnv.authenticatedContext(ADMIN_UID).firestore();
  await assertSucceeds(adminDb.collection("products").doc("bat_1").set({
    name: "MRF English Willow Cricket Bat",
    category: "Cricket Bats",
    price: 4999,
    description: "Grade 1 English Willow",
    inStock: true,
    stockQuantity: 5,
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(adminDb.collection("orders").get());
});

package com.example.mpps;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mpps.Model.Order;
import com.example.mpps.Model.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MPPS.db";
    public static final String USER_TABLE = "users";
    public static final String STORE_TABLE = "stores";
    public static final String PRODUCT_TABLE = "products";
    public static final String ADDRESS_TABLE = "addresses";
    public static final String ORDERS_TABLE = "orders";
    public static final String ORDER_ITEMS_TABLE = "order_items";


    // Columns for ORDERS_TABLE
    public static final String ORDER_COL_1 = "ID";
    public static final String ORDER_COL_2 = "USER_ID";
    public static final String ORDER_COL_3 = "TOTAL_AMOUNT";
    public static final String ORDER_COL_4 = "DATE";
    public static final String ORDER_COL_5 = "DELIVERY_TYPE";
    public static final String ORDER_COL_6 = "ADDRESS";
    public static final String ORDER_COL_7 = "STORE_NAME";
    public static final String ORDER_COL_8 = "STATUS";

    // Columns for ORDER_ITEMS_TABLE
    public static final String ORDER_ITEM_COL_1 = "ID";
    public static final String ORDER_ITEM_COL_2 = "ORDER_ID";
    public static final String ORDER_ITEM_COL_3 = "PRODUCT_NAME";
    public static final String ORDER_ITEM_COL_4 = "QUANTITY";
    public static final String ORDER_ITEM_COL_5 = "PRICE";

    // Vartotojų lentelės stulpeliai
    public static final String USER_COL_1 = "ID";
    public static final String USER_COL_2 = "EMAIL";
    public static final String USER_COL_3 = "PASSWORD";
    public static final String USER_COL_4 = "FIRST_NAME";
    public static final String USER_COL_5 = "LAST_NAME";
    public static final String USER_COL_6 = "PHONE_NUMBER";

    // Parduotuvės lentelės stulpeliai
    public static final String STORE_COL_1 = "ID";
    public static final String STORE_COL_2 = "NAME";
    public static final String STORE_COL_3 = "LOGO";

    // Produktų lentelės stulpeliai
    public static final String PRODUCT_COL_1 = "ID";
    public static final String PRODUCT_COL_2 = "NAME";
    public static final String PRODUCT_COL_3 = "PRICE";
    public static final String PRODUCT_COL_4 = "STORE_ID";
    public static final String PRODUCT_COL_5 = "INGREDIENTS";
    public static final String PRODUCT_COL_6 = "NUTRITION";
    public static final String PRODUCT_COL_7 = "CATEGORY";

    public static final String CART_TABLE = "cart";
    public static final String CART_COL_1 = "ID";
    public static final String CART_COL_2 = "PRODUCT_ID";
    public static final String CART_COL_3 = "QUANTITY";

    public static final String ADDRESS_COL_1 = "ID";
    public static final String ADDRESS_COL_2 = "USER_ID";
    public static final String ADDRESS_COL_3 = "CITY";
    public static final String ADDRESS_COL_4 = "ADDRESS";
    public static final String ADDRESS_COL_5 = "ADDRESS_NAME";
    public static final String ADDRESS_COL_6 = "PHONE";
    public static final String ADDRESS_COL_7 = "APARTMENT_NUMBER";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + ORDERS_TABLE + " (" +
                ORDER_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_COL_2 + " INTEGER, " +
                ORDER_COL_3 + " REAL, " +
                ORDER_COL_4 + " TEXT, " +
                ORDER_COL_5 + " TEXT, " +
                ORDER_COL_6 + " TEXT, " +
                ORDER_COL_7 + " TEXT, " +
                ORDER_COL_8 + " TEXT, " +
                "FOREIGN KEY(" + ORDER_COL_2 + ") REFERENCES " + USER_TABLE + "(ID))");

        db.execSQL("CREATE TABLE " + ORDER_ITEMS_TABLE + " (" +
                ORDER_ITEM_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_ITEM_COL_2 + " INTEGER, " +
                ORDER_ITEM_COL_3 + " TEXT, " +
                ORDER_ITEM_COL_4 + " INTEGER, " +
                ORDER_ITEM_COL_5 + " REAL, " +
                "FOREIGN KEY(" + ORDER_ITEM_COL_2 + ") REFERENCES " + ORDERS_TABLE + "(ID))");

        // Vartotojų lentelė
        db.execSQL("CREATE TABLE " + USER_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, " +
                "PASSWORD TEXT, " +
                "FIRST_NAME TEXT, " +
                "LAST_NAME TEXT, " +
                "PHONE_NUMBER TEXT)");

        // Krepšelio lentelė
        db.execSQL("CREATE TABLE " + CART_TABLE + " (" +
                CART_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID INTEGER, " + // Pridedame USER_ID stulpelį
                CART_COL_2 + " INTEGER, " +
                CART_COL_3 + " INTEGER, " +
                "FOREIGN KEY(" + CART_COL_2 + ") REFERENCES " + PRODUCT_TABLE + "(ID))");

        // Parduotuvės lentelė
        db.execSQL("CREATE TABLE " + STORE_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, LOGO TEXT)");

        // Produktų lentelė
        db.execSQL("CREATE TABLE " + PRODUCT_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, " +
                "PRICE REAL, " +
                "STORE_ID INTEGER, " +
                "INGREDIENTS TEXT, " +
                "NUTRITION TEXT, " +
                "CATEGORY TEXT, " +  // Add category column
                "FOREIGN KEY (STORE_ID) REFERENCES " + STORE_TABLE + "(ID))");

        // Adresų lentelė
        db.execSQL("CREATE TABLE " + ADDRESS_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID INTEGER, " +
                "CITY TEXT, " +
                "ADDRESS TEXT, " +
                "ADDRESS_NAME TEXT, " +
                "PHONE TEXT, " +
                "APARTMENT_NUMBER TEXT, " +
                "FOREIGN KEY(USER_ID) REFERENCES " + USER_TABLE + "(ID))");

        // Pridėkime pradines parduotuves
        ContentValues storeValues = new ContentValues();
        storeValues.put(STORE_COL_2, "Maxima");
        storeValues.put(STORE_COL_3, "logo_maxima");
        long maximaId = db.insert(STORE_TABLE, null, storeValues);

        storeValues.put(STORE_COL_2, "IKI");
        storeValues.put(STORE_COL_3, "logo_iki");
        long ikiId = db.insert(STORE_TABLE, null, storeValues);

        storeValues.put(STORE_COL_2, "Rimi");
        storeValues.put(STORE_COL_3, "logo_rimi");
        long rimiId = db.insert(STORE_TABLE, null, storeValues);

        // Pridėkime pradinius produktus Maxima parduotuvei
        ContentValues productValues = new ContentValues();
        productValues.put(PRODUCT_COL_2, "Duona");
        productValues.put(PRODUCT_COL_3, 1.29);
        productValues.put(PRODUCT_COL_4, maximaId);
        productValues.put(PRODUCT_COL_5, "Kvietiniai miltai, vanduo, mielės");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 250kcal/100g, Baltymai: 9g/100g");
        productValues.put(PRODUCT_COL_7, "Pieno gaminiai ir kiaušiniai");  // Category example
        db.insert(PRODUCT_TABLE, null, productValues);

        productValues.put(PRODUCT_COL_2, "Pienas");
        productValues.put(PRODUCT_COL_3, 0.99);
        productValues.put(PRODUCT_COL_4, maximaId);
        productValues.put(PRODUCT_COL_5, "Pienas, vitaminai");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 42kcal/100ml, Baltymai: 3.4g/100ml");
        productValues.put(PRODUCT_COL_7, "Pieno gaminiai ir kiaušiniai"); // Naujas kategorijos laukas
        db.insert(PRODUCT_TABLE, null, productValues);

// Pridėkime pradinius produktus IKI parduotuvei
        productValues.put(PRODUCT_COL_2, "Suris");
        productValues.put(PRODUCT_COL_3, 2.49);
        productValues.put(PRODUCT_COL_4, ikiId);
        productValues.put(PRODUCT_COL_5, "Pienas, druska, fermentai");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 402kcal/100g, Baltymai: 25g/100g");
        productValues.put(PRODUCT_COL_7, "Pieno gaminiai ir kiaušiniai"); // Naujas kategorijos laukas
        db.insert(PRODUCT_TABLE, null, productValues);

        productValues.put(PRODUCT_COL_2, "Kiausiniai");
        productValues.put(PRODUCT_COL_3, 1.99);
        productValues.put(PRODUCT_COL_4, ikiId);
        productValues.put(PRODUCT_COL_5, "Kiaušiniai");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 155kcal/100g, Baltymai: 13g/100g");
        productValues.put(PRODUCT_COL_7, "Pieno gaminiai ir kiaušiniai"); // Naujas kategorijos laukas
        db.insert(PRODUCT_TABLE, null, productValues);

// Pridėkime pradinius produktus Rimi parduotuvei
        productValues.put(PRODUCT_COL_2, "Obuoliai");
        productValues.put(PRODUCT_COL_3, 1.79);
        productValues.put(PRODUCT_COL_4, rimiId);
        productValues.put(PRODUCT_COL_5, "Obuoliai");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 52kcal/100g, Baltymai: 0.3g/100g");
        productValues.put(PRODUCT_COL_7, "Daržovės ir vaisiai"); // Naujas kategorijos laukas
        db.insert(PRODUCT_TABLE, null, productValues);

        productValues.put(PRODUCT_COL_2, "Sviestas");
        productValues.put(PRODUCT_COL_3, 1.49);
        productValues.put(PRODUCT_COL_4, rimiId);
        productValues.put(PRODUCT_COL_5, "Pienas, druska");
        productValues.put(PRODUCT_COL_6, "Kalorijos: 717kcal/100g, Baltymai: 0.9g/100g");
        productValues.put(PRODUCT_COL_7, "Bakaleja"); // Naujas kategorijos laukas
        db.insert(PRODUCT_TABLE, null, productValues);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + STORE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ADDRESS_TABLE);
        onCreate(db);
    }

    // Metodas, skirtas pridėti vartotoją
    public boolean insertUser(String email, String password, String firstName, String lastName, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COL_2, email);
        contentValues.put(USER_COL_3, password);
        contentValues.put(USER_COL_4, firstName);
        contentValues.put(USER_COL_5, lastName);
        contentValues.put(USER_COL_6, phoneNumber);
        long result = db.insert(USER_TABLE, null, contentValues);
        return result != -1;
    }

    // Address-related methods
    public boolean insertAddress(int userId, String city, String address, String addressName, String phone, String apartmentNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADDRESS_COL_2, userId);
        contentValues.put(ADDRESS_COL_3, city);
        contentValues.put(ADDRESS_COL_4, address);
        contentValues.put(ADDRESS_COL_5, addressName);
        contentValues.put(ADDRESS_COL_6, phone);
        contentValues.put(ADDRESS_COL_7, apartmentNumber);
        long result = db.insert(ADDRESS_TABLE, null, contentValues);
        return result != -1;
    }

    public String getAddressByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CITY || ', ' || ADDRESS || ', ' || ADDRESS_NAME || ', ' || PHONE || ', ' || APARTMENT_NUMBER AS ADDRESS FROM " + ADDRESS_TABLE + " WHERE USER_ID=?", new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            String address = cursor.getString(0);
            cursor.close();
            return address;
        }
        return "Adresas nerastas";
    }

    public boolean updateAddress(int userId, String city, String address, String addressName, String phone, String apartmentNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ADDRESS_COL_3, city);
        contentValues.put(ADDRESS_COL_4, address);
        contentValues.put(ADDRESS_COL_5, addressName);
        contentValues.put(ADDRESS_COL_6, phone);
        contentValues.put(ADDRESS_COL_7, apartmentNumber);
        return db.update(ADDRESS_TABLE, contentValues, "USER_ID=?", new String[]{String.valueOf(userId)}) > 0;
    }



    // Metodas, skirtas patikrinti vartotojo prisijungimo duomenis
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true; // Vartotojas rastas
        }
        return false; // Vartotojas nerastas
    }

    // Metodas, skirtas pridėti parduotuvę
    public boolean insertStore(String name, String logo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STORE_COL_2, name);
        contentValues.put(STORE_COL_3, logo);
        long result = db.insert(STORE_TABLE, null, contentValues);
        return result != -1;
    }

    // Metodas, skirtas pridėti produktą
    public boolean insertProduct(String name, double price, int storeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_COL_2, name);
        contentValues.put(PRODUCT_COL_3, price);
        contentValues.put(PRODUCT_COL_4, storeId);
        long result = db.insert(PRODUCT_TABLE, null, contentValues);
        return result != -1;
    }

    // Metodas, skirtas gauti parduotuves
    public Cursor getStores() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + STORE_TABLE, null);
    }

    // Metodas, skirtas gauti produktus pagal parduotuvę
    public Cursor getProductsByStore(int storeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCT_TABLE + " WHERE STORE_ID=?", new String[]{String.valueOf(storeId)});
    }
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCT_TABLE, null);
    }
    public Cursor getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM products WHERE ID = ?", new String[]{String.valueOf(productId)});
    }
    public Cursor getProductsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + PRODUCT_TABLE + " WHERE CATEGORY=?", new String[]{category});
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE EMAIL=?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            // Ensure these match the column names in your database schema
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_COL_1));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_3));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_4));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_5));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_6));

            cursor.close();

            return new User(id, email, password, firstName, lastName, phoneNumber);
        }

        return null; // User not found
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0; // Check if any rows are returned
        cursor.close();
        return exists;
    }

    public boolean addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Patikriname, ar produktas jau yra krepšelyje
        Cursor cursor = db.rawQuery("SELECT QUANTITY FROM " + CART_TABLE + " WHERE USER_ID = ? AND PRODUCT_ID = ?",
                new String[]{String.valueOf(userId), String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            // Jei produktas jau yra krepšelyje, padidiname kiekį
            @SuppressLint("Range") int currentQuantity = cursor.getInt(cursor.getColumnIndex("QUANTITY"));
            int newQuantity = currentQuantity + quantity;

            ContentValues values = new ContentValues();
            values.put("QUANTITY", newQuantity);

            db.update(CART_TABLE, values, "USER_ID = ? AND PRODUCT_ID = ?", new String[]{String.valueOf(userId), String.valueOf(productId)});

            cursor.close();
            return true;
        } else {
            // Jei produkto nėra krepšelyje, pridedame jį kaip naują įrašą
            ContentValues values = new ContentValues();
            values.put("USER_ID", userId);
            values.put("PRODUCT_ID", productId);
            values.put("QUANTITY", quantity);

            long result = db.insert(CART_TABLE, null, values);

            if (cursor != null) {
                cursor.close();
            }

            return result != -1;
        }
    }


    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + CART_TABLE + " WHERE USER_ID=?", new String[]{String.valueOf(userId)});
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM " + USER_TABLE + " WHERE EMAIL=? AND PASSWORD=?", new String[]{email, password});
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        if (cursor != null) cursor.close();
        return -1; // Grąžina -1, jei vartotojas nerastas
    }
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE ID=?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            // Sukuriame User objektą pagal duomenis iš užklausos
            String email = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_2));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_3));
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_4));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_5));
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(USER_COL_6));

            cursor.close();

            // Grąžiname naujai sukurtą User objektą
            return new User(userId, email, password, firstName, lastName, phoneNumber);
        }

        if (cursor != null) {
            cursor.close();
        }

        return null; // Grąžiname null, jei vartotojas nerastas
    }
    public void updateCartItemQuantity(int userId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CART_COL_3, quantity);
        db.update(CART_TABLE, values, "USER_ID=? AND PRODUCT_ID=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
    }

    public void removeCartItem(int userId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CART_TABLE, "USER_ID=? AND PRODUCT_ID=?", new String[]{String.valueOf(userId), String.valueOf(productId)});
    }

    public Cursor getAllAddressesByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM addresses WHERE USER_ID = ?", new String[]{String.valueOf(userId)});
    }

    public boolean deleteAddressById(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ADDRESS_TABLE, "ID=?", new String[]{String.valueOf(addressId)}) > 0;
    }

    public long insertOrder(int userId, double totalAmount, String date, String deliveryType, String address, String storeName, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORDER_COL_2, userId);
        values.put(ORDER_COL_3, totalAmount);
        values.put(ORDER_COL_4, date);
        values.put(ORDER_COL_5, deliveryType);
        values.put(ORDER_COL_6, address);
        values.put(ORDER_COL_7, storeName);
        values.put(ORDER_COL_8, status);
        return db.insert(ORDERS_TABLE, null, values);
    }

    public void insertOrderItem(long orderId, String productName, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORDER_ITEM_COL_2, orderId);
        values.put(ORDER_ITEM_COL_3, productName);
        values.put(ORDER_ITEM_COL_4, quantity);
        values.put(ORDER_ITEM_COL_5, price);
        db.insert(ORDER_ITEMS_TABLE, null, values);
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CART_TABLE, "USER_ID=?", new String[]{String.valueOf(userId)});
    }

    public ArrayList<Order> getOrdersByUserId(int userId) {
        ArrayList<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ORDERS_TABLE + " WHERE USER_ID=?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int orderId = cursor.getInt(cursor.getColumnIndex(ORDER_COL_1));
                @SuppressLint("Range") double totalAmount = cursor.getDouble(cursor.getColumnIndex(ORDER_COL_3));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(ORDER_COL_4));
                @SuppressLint("Range") String deliveryType = cursor.getString(cursor.getColumnIndex(ORDER_COL_5));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(ORDER_COL_6));
                @SuppressLint("Range") String storeName = cursor.getString(cursor.getColumnIndex(ORDER_COL_7));
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(ORDER_COL_8));

                Order order = new Order(orderId, userId, totalAmount, date, deliveryType, address, storeName, status);
                orders.add(order);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return orders;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ORDERS_TABLE + " WHERE ID=?", new String[]{String.valueOf(orderId)});
        Order order = null;

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(ORDER_COL_2));
            @SuppressLint("Range") double totalAmount = cursor.getDouble(cursor.getColumnIndex(ORDER_COL_3));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(ORDER_COL_4));
            @SuppressLint("Range") String deliveryType = cursor.getString(cursor.getColumnIndex(ORDER_COL_5));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(ORDER_COL_6));
            @SuppressLint("Range") String storeName = cursor.getString(cursor.getColumnIndex(ORDER_COL_7));
            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex(ORDER_COL_8));

            order = new Order(orderId, userId, totalAmount, date, deliveryType, address, storeName, status);

            // Retrieve the order items associated with this order
            Cursor itemCursor = db.rawQuery("SELECT * FROM " + ORDER_ITEMS_TABLE + " WHERE ORDER_ID=?", new String[]{String.valueOf(orderId)});
            if (itemCursor != null && itemCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String productName = itemCursor.getString(itemCursor.getColumnIndex(ORDER_ITEM_COL_3));
                    @SuppressLint("Range") int quantity = itemCursor.getInt(itemCursor.getColumnIndex(ORDER_ITEM_COL_4));
                    @SuppressLint("Range") double price = itemCursor.getDouble(itemCursor.getColumnIndex(ORDER_ITEM_COL_5));
                    order.addItem(new Order.OrderItem(productName, quantity, price));
                } while (itemCursor.moveToNext());
            }
            if (itemCursor != null) {
                itemCursor.close();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return order;
    }

    public boolean cancelOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORDER_COL_8, "Atšauktas"); // Set status to "Canceled" or "Atšauktas" in Lithuanian
        int rowsAffected = db.update(ORDERS_TABLE, values, ORDER_COL_1 + "=?", new String[]{String.valueOf(orderId)});
        return rowsAffected > 0; // Return true if at least one row was updated
    }

}


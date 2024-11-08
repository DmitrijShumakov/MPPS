package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.ProductDetailActivity;
import com.example.mpps.Activity.CartActivity;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddToCartTest {

    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int testUserId = 1;
    private int testProductId = 101;

    @Rule
    public ActivityScenarioRule<ProductDetailActivity> activityScenarioRule =
            new ActivityScenarioRule<>(ProductDetailActivity.class);

    @Before
    public void setup() {
        // Initialize the database helper
        db = new DatabaseHelper(ApplicationProvider.getApplicationContext());

        // Create a test user in the database
        ContentValues userValues = new ContentValues();
        userValues.put("ID", testUserId);
        userValues.put("EMAIL", "testuser@example.com");
        userValues.put("PASSWORD", "password123");
        db.getWritableDatabase().insert("users", null, userValues);

        // Create a test product in the database
        ContentValues productValues = new ContentValues();
        productValues.put("ID", testProductId);
        productValues.put("NAME", "Test Product");
        productValues.put("PRICE", 9.99);
        db.getWritableDatabase().insert("products", null, productValues);

        // Set SharedPreferences for the test user
        sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("UserPrefs", ApplicationProvider.getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("user_id", testUserId);
        editor.apply();
    }

    @After
    public void tearDown() {
        // Clear test data
        db.getWritableDatabase().delete("users", "ID = ?", new String[]{String.valueOf(testUserId)});
        db.getWritableDatabase().delete("products", "ID = ?", new String[]{String.valueOf(testProductId)});
        db.getWritableDatabase().delete("cart", "USER_ID = ?", new String[]{String.valueOf(testUserId)});

        // Clear SharedPreferences
        editor.clear();
        editor.apply();
    }

    @Test
    public void testAddProductToCart() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProductDetailActivity.class);
        intent.putExtra("product_id", testProductId);
        ActivityScenario<ProductDetailActivity> scenario = ActivityScenario.launch(intent);

        // Tikriname, ar produktas rodomas
        onView(withId(R.id.productName)).check(matches(isDisplayed()));

        // Spaudžiame mygtuką „Pridėti į krepšelį“
        onView(withId(R.id.addToCartButton)).perform(click());

        // Tikriname, ar prekė pridėta į krepšelį
        Cursor cursor = db.getReadableDatabase().query("cart",
                null, "USER_ID = ? AND PRODUCT_ID = ?",
                new String[]{String.valueOf(testUserId), String.valueOf(testProductId)},
                null, null, null);

        boolean productAdded = cursor.moveToFirst();
        cursor.close();

        assert productAdded : "The product was not successfully added to the cart.";
    }

    @Test
    public void testRemoveProductFromCart() {
        // Pridėti produktą į krepšelį, kad būtų galima jį pašalinti
        db.addToCart(testUserId, testProductId, 1);

        // Paleisti CartActivity
        ActivityScenario<CartActivity> cartScenario = ActivityScenario.launch(CartActivity.class);

        // Tikrinti, ar produktas rodomas krepšelyje
        onView(withId(R.id.cartRecyclerView)).check(matches(isDisplayed()));

        // Spaudžiame mygtuką „Pašalinti prekę“ (pakeiskite ID pagal jūsų implementaciją)
        onView(withId(R.id.removeProduct)).perform(click());

        // Patikrinti, ar produktas buvo pašalintas iš duomenų bazės
        Cursor cursor = db.getReadableDatabase().query("cart",
                null, "USER_ID = ? AND PRODUCT_ID = ?",
                new String[]{String.valueOf(testUserId), String.valueOf(testProductId)},
                null, null, null);

        boolean productRemoved = !cursor.moveToFirst();
        cursor.close();

        assert productRemoved : "The product was not successfully removed from the cart.";
    }
}

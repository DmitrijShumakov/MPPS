package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.ContentValues;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.LoginActivity;
import com.example.mpps.Activity.AppMainActivity;
import com.example.mpps.DatabaseHelper;
import com.example.mpps.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityInstrumentedTest {

    private DatabaseHelper db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Initialize intents
        init();

        // Initialize the database helper
        db = new DatabaseHelper(ApplicationProvider.getApplicationContext());

        // Create a test user in the database
        ContentValues values = new ContentValues();
        values.put("ID", 1);
        values.put("FIRST_NAME", "TestUser");
        values.put("LAST_NAME", "LastName");
        values.put("PHONE_NUMBER", "123456789");
        values.put("EMAIL", "valid.email@example.com");
        values.put("PASSWORD", "CorrectPassword");
        db.getWritableDatabase().insert("users", null, values);

        // Set the SharedPreferences to mock a logged-in user before running the test
        sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("UserPrefs", ApplicationProvider.getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("user_id", 1);
        editor.putString("user_email", "valid.email@example.com");
        editor.apply();
    }

    @After
    public void tearDown() {
        // Release intents after the test
        release();

        // Clear the test user from the database after the test
        db.getWritableDatabase().delete("users", "ID = ?", new String[]{"1"});
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Enter valid credentials
        onView(withId(R.id.emailInput)).perform(replaceText("valid.email@example.com"));
        onView(withId(R.id.passwordInput)).perform(replaceText("CorrectPassword"));

        // Click the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Check that AppMainActivity is launched
        intended(hasComponent(AppMainActivity.class.getName()));
    }

    @Test
    public void testLoginWithInvalidCredentials() throws InterruptedException {
        // Enter invalid credentials
        onView(withId(R.id.emailInput)).perform(replaceText("invalid.email@example.com"));
        onView(withId(R.id.passwordInput)).perform(replaceText("WrongPassword"));

        // Click the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Wait for the Snackbar to appear
        Thread.sleep(1500);  // Temporary delay to wait for Snackbar

        // Check for error message in Snackbar
        onView(withText("Prisijungimo duomenys neteisingi"))
                .check(matches(isDisplayed()));
    }
}

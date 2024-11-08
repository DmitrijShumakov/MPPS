package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.content.ContentValues;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.AccountActivity;
import com.example.mpps.DatabaseHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.core.app.ActivityScenario;

@RunWith(AndroidJUnit4.class)
public class AccountUpdateTest {

    @Rule
    public ActivityScenarioRule<AccountActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AccountActivity.class);

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DatabaseHelper db;

    @Before
    public void setup() {
        // Disable animations programmatically
        System.setProperty("debug.test.disable_window_animation_scale", "1");
        System.setProperty("debug.test.disable_transition_animation_scale", "1");
        System.setProperty("debug.test.disable_animator_duration_scale", "1");

        // Initialize the database helper
        db = new DatabaseHelper(ApplicationProvider.getApplicationContext());

        // Create a test user in the database
        ContentValues values = new ContentValues();
        values.put("ID", 1);
        values.put("FIRST_NAME", "TestFirstName");
        values.put("LAST_NAME", "TestLastName");
        values.put("PHONE_NUMBER", "123456789");
        values.put("EMAIL", "testuser@example.com");
        values.put("PASSWORD", "password123");
        db.getWritableDatabase().insert("users", null, values);

        // Set the SharedPreferences to mock a logged-in user before running the test
        sharedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("UserPrefs", ApplicationProvider.getApplicationContext().MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("user_id", 1); // Set test user ID
        editor.putString("user_email", "testuser@example.com"); // Mock user email
        editor.apply();

        // Launch the AccountActivity explicitly
        ActivityScenario.launch(AccountActivity.class);
    }

    @Test
    public void testUpdateUserProfile() throws InterruptedException {
        // Wait until the first name field is displayed
        onView(withId(R.id.firstNameEditText)).check(matches(isDisplayed()));

        // Replace existing user data with new information
        onView(withId(R.id.firstNameEditText)).perform(replaceText("NewFirstName"));
        onView(withId(R.id.lastNameEditText)).perform(replaceText("NewLastName"));
        onView(withId(R.id.phoneEditText)).perform(replaceText("123456789"));
        onView(withId(R.id.emailEditText)).perform(replaceText("newuser@example.com"));
        onView(withId(R.id.passwordEditText)).perform(replaceText("NewPassword123"));

        // Scroll to the save button and click it
        onView(withId(R.id.saveButton)).perform(scrollTo(), click());

        // Wait for Snackbar to be displayed (using IdlingResource or a fixed delay)
        onView(withText("Pakeitimai išsaugoti!")).check(matches(isDisplayed()));

        // Wait for SharedPreferences to update
        Thread.sleep(1500);  // Wait for 1.5 seconds

        // Verify the updated email in SharedPreferences
        SharedPreferences updatedPreferences = ApplicationProvider.getApplicationContext()
                .getSharedPreferences("UserPrefs", ApplicationProvider.getApplicationContext().MODE_PRIVATE);
        String updatedEmail = updatedPreferences.getString("user_email", null);

        // Check if the email has been updated
        assert updatedEmail.equals("newuser@example.com");
    }

    @After
    public void tearDown() {
        // Clear test user from the database
        db.getWritableDatabase().delete("users", "ID = ?", new String[]{"1"});

        // Clear SharedPreferences
        editor.clear();
        editor.apply();
    }
}

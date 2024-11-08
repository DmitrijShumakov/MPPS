package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Handler;
import android.os.Looper;

import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.RegisterActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegisterActivityInstrumentedTest {
    CountingIdlingResource idlingResource = new CountingIdlingResource("SnackbarIdle");

    private DatabaseHelper mockDb;

    @Rule
    public ActivityScenarioRule<RegisterActivity> activityScenarioRule =
            new ActivityScenarioRule<>(RegisterActivity.class);

    @Before
    public void setup() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            DisableAnimations.disableSystemAnimations(activity.getApplicationContext());

            mockDb = mock(DatabaseHelper.class);
            activity.db = mockDb;
        });
    }

    @After
    public void enableAnimations() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            DisableAnimations.enableSystemAnimations(activity.getApplicationContext());
        });
    }

    @Test
    public void TC01_RegistrationWithWeakPassword() {

        when(mockDb.checkEmailExists("john.doe@example.com")).thenReturn(false);

        onView(withId(R.id.firstNameInput)).perform(scrollTo(), replaceText("John"));
        onView(withId(R.id.lastNameInput)).perform(scrollTo(), replaceText("Doe"));
        onView(withId(R.id.emailInput)).perform(scrollTo(), replaceText("john.doe@example.com"));
        onView(withId(R.id.phoneInput)).perform(scrollTo(), replaceText("123456789"));
        onView(withId(R.id.passwordInput)).perform(scrollTo(), replaceText("12345")); // Weak password
        onView(withId(R.id.confirmPasswordInput)).perform(scrollTo(), replaceText("12345"));

        onView(withId(R.id.registerButton)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Slaptažodis turi būti bent 8 simbolių, turėti didžiąją raidę, skaičių ir specialų simbolį")));
    }

    @Test
    public void TC02_RegistrationWithMissingFields() {
        // Fill in fields but leave email empty
        onView(withId(R.id.firstNameInput)).perform(scrollTo(), replaceText("John"));
        onView(withId(R.id.lastNameInput)).perform(scrollTo(), replaceText("Doe"));
        onView(withId(R.id.emailInput)).perform(scrollTo(), clearText());
        onView(withId(R.id.phoneInput)).perform(scrollTo(), replaceText("123456789"));
        onView(withId(R.id.passwordInput)).perform(scrollTo(), replaceText("Password1!"));
        onView(withId(R.id.confirmPasswordInput)).perform(scrollTo(), replaceText("Password1!"));

        onView(withId(R.id.registerButton)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Užpildykite visus laukus")));
    }

    @Test
    public void TC03_RegistrationWithExistingEmail() {
        // Mock the email already existing
        when(mockDb.checkEmailExists("existing.email@example.com")).thenReturn(true);

        onView(withId(R.id.firstNameInput)).perform(scrollTo(), replaceText("John"));
        onView(withId(R.id.lastNameInput)).perform(scrollTo(), replaceText("Doe"));
        onView(withId(R.id.emailInput)).perform(scrollTo(), replaceText("existing.email@example.com"));
        onView(withId(R.id.phoneInput)).perform(scrollTo(), replaceText("123456789"));
        onView(withId(R.id.passwordInput)).perform(scrollTo(), replaceText("Password1!"));
        onView(withId(R.id.confirmPasswordInput)).perform(scrollTo(), replaceText("Password1!"));

        onView(withId(R.id.registerButton)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("El. paštas jau užregistruotas")));
    }

    @Test
    public void TC04_SuccessfulRegistration() {
        when(mockDb.checkEmailExists("john.doe@example.com")).thenReturn(false);
        when(mockDb.insertUser("john.doe@example.com", "Password1!", "John", "Doe", "123456789")).thenReturn(true);

        onView(withId(R.id.firstNameInput)).perform(scrollTo(), replaceText("John"));
        onView(withId(R.id.lastNameInput)).perform(scrollTo(), replaceText("Doe"));
        onView(withId(R.id.emailInput)).perform(scrollTo(), replaceText("john.doe@example.com"));
        onView(withId(R.id.phoneInput)).perform(scrollTo(), replaceText("123456789"));
        onView(withId(R.id.passwordInput)).perform(scrollTo(), replaceText("Password1!"));
        onView(withId(R.id.confirmPasswordInput)).perform(scrollTo(), replaceText("Password1!"));

        onView(withId(R.id.registerButton)).perform(scrollTo(), click());

        idlingResource.increment();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            idlingResource.decrement();
        }, 2000);

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Registracija sėkminga")));
    }
}

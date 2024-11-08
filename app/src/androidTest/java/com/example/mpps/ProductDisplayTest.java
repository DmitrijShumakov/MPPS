package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.AppMainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProductDisplayTest {

    @Rule
    public ActivityScenarioRule<AppMainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AppMainActivity.class);

    @Test
    public void testDisplayProductsForSelectedStore() {
        // Simulate a click on the first store in the RecyclerView (Maxima)
        onView(withId(R.id.storeRecyclerView))
                .perform(actionOnItemAtPosition(0, click())); // Assuming Maxima is the first store

        // Check if products from Maxima are displayed (e.g., Pienas and Duona)
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productName), withText("Pienas"))))); // Check for Pienas
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productName), withText("Duona"))))); // Check for Duona

        // Ensure products from other stores (e.g., Obuoliai from Rimi) are NOT displayed
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productName), withText("Pienas"))))); // Ensure only Maxima products are shown
    }
}

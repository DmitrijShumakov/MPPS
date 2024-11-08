package com.example.mpps;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mpps.Activity.AppMainActivity;
import com.example.mpps.Activity.ProductDetailActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProductCatalogTest {

    @Rule
    public ActivityScenarioRule<AppMainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AppMainActivity.class);

    @Before
    public void setup() {
        // Initialize intents for checking activity transitions
        init();
    }

    @After
    public void tearDown() {
        // Release intents after the test
        release();
    }

    @Test
    public void testProductDisplayAndDetails() {
        // Check if a product's name and price are displayed in the RecyclerView
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productName), withText("Pienas"))))); // Change to match actual product name
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productPrice), withText("€0.99"))))); // Change to match actual price format

        // Simulate a click on the first product to navigate to the detail page
        onView(withId(R.id.productRecyclerView))
                .perform(actionOnItemAtPosition(0, click())); // Click on the first item

        // Check if ProductDetailActivity is launched
        intended(hasComponent(ProductDetailActivity.class.getName()));
    }

    @Test
    public void testSortProductsByPriceAscending() {
        // Click on the spinner to open the dropdown menu
        onView(withId(R.id.priceFilterSpinner)).perform(click());

        // Select "Pigiausios pirmos" from the dropdown
        onView(withText("Pigiausios pirmos")).perform(click());

        // Check if the first product has the lowest price
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productPrice), withText("€0.99"))))); // Assuming €0.99 is the lowest price
    }

    @Test
    public void testSearchFunctionality() {
        // Enter a search query in the search bar
        onView(withId(R.id.searchBar)).perform(replaceText("Pienas")); // Replace "Pienas" with the product name you are searching for

        // Check if the product matching the search query is displayed in the RecyclerView
        onView(withId(R.id.productRecyclerView))
                .check(matches(hasDescendant(allOf(withId(R.id.productName), withText("Pienas"))))); // Adjust the product name as per actual data
    }
}

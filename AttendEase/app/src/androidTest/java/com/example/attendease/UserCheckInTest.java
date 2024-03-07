package com.example.attendease;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserCheckInTest {

    @Rule
    public ActivityScenarioRule<UserCheckIn> activityRule = new ActivityScenarioRule<>(UserCheckIn.class);

    @Before
    public void setUp() {
        // Initialize Espresso Intents before each test, needed only if you're using intents
        Intents.init();
    }

    @After
    public void tearDown() {
        // Clean up after each test by releasing Espresso Intents, needed only if you're using intents
        Intents.release();
    }

    @Test
    public void userTypesNameAndClicksSubmit() {
        // Assume R.id.editText_name is your EditText and R.id.button_submit is your submit button.
        onView(withId(R.id.editText_name))
                .perform(typeText("John Doe"), closeSoftKeyboard());

        onView(withId(R.id.button_submit)).perform(click());
    }
}

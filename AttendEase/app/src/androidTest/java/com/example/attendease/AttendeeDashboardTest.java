package com.example.attendease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeDashboardTest {

    @Rule
    public ActivityScenarioRule<AttendeeDashboardActivity> scenario = new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), AttendeeDashboardActivity.class).putExtra("attendee", new Attendee("testAttendeeId", "Test Attendee")));

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testSeeAllEventsNavigation() {
        onView(withId(R.id.see_all2)).perform(click());
        intended(hasComponent(BrowseAllEvents.class.getName()));
    }

    @Test
    public void testProfileButtonNavigation() {
        onView(withId(R.id.nav_profile)).perform(click());
        intended(hasComponent(EditProfileActivity.class.getName()));
    }

    @org.junit.After
    public void tearDown() {
        // Clean up after each test execution
        Intents.release();
    }
}

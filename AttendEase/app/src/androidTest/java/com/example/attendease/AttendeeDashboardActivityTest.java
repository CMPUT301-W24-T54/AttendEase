package com.example.attendease;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeDashboardActivityTest {

    @Rule
    public ActivityScenarioRule<AttendeeDashboardActivity> activityRule =
            new ActivityScenarioRule<>(AttendeeDashboardActivity.class);

    @Before
    public void setUp() {
        // Launch the AttendeeDashboardActivity with necessary extras
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), AttendeeDashboardActivity.class);
        startIntent.putExtra("deviceID", "mockDeviceID");
        ActivityScenario.launch(startIntent);

        // Initialize Espresso Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Clean up after each test by releasing Espresso Intents
        Intents.release();
    }

    @Test
    public void checkInButtonNavigatesToQRScannerActivity() {
        onView(withId(R.id.scan_new_qr)).perform(click());

        // Verify that QRScannerActivity is started
        intended(hasComponent(QRScannerActivity.class.getName()));
    }

    @Test
    public void seeAllEventsButtonNavigatesToBrowseAllEvents() {
        onView(withId(R.id.see_all)).perform(click());

        // Verify that BrowseAllEvents activity is started
        intended(hasComponent(BrowseAllEvents.class.getName()));
    }

    @Test
    public void bottomNavigationToProfileOpensEditProfileActivity() {
        onView(withId(R.id.nav_profile)).perform(click());

        // Verify that EditProfileActivity is started
        intended(hasComponent(EditProfileActivity.class.getName()));
    }
}

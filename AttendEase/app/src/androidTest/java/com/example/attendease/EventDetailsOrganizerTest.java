package com.example.attendease;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailsOrganizerTest {

    @Rule
    public IntentsTestRule<EventDetailsOrganizer> intentsTestRule =
            new IntentsTestRule<>(EventDetailsOrganizer.class, true, false);

    @Test
    public void testUIComponentsAreInitialized() {
        // Launch Activity with a mocked intent if needed
        Intent intent = new Intent();
        intent.putExtra("eventDocumentId", "mockEventDocId"); // Mocked event document ID
        intentsTestRule.launchActivity(intent);

        // Verify that UI components are displayed
        onView(withId(R.id.eventName)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsAboutContent)).check(matches(isDisplayed()));
        onView(withId(R.id.location)).check(matches(isDisplayed()));
        onView(withId(R.id.dateandtime)).check(matches(isDisplayed()));
        onView(withId(R.id.QRCodeImage)).check(matches(isDisplayed()));

    }
}

package com.example.attendease;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;



@RunWith(AndroidJUnit4.class)
public class EventDetailsOrganizerTest {

    @Rule
    public IntentsTestRule<EventDetailsOrganizer> intentsTestRule = new IntentsTestRule<>(EventDetailsOrganizer.class, true, false);
    public ActivityScenarioRule<EventDetailsOrganizer> scenario1 =
            new ActivityScenarioRule<EventDetailsOrganizer>(new Intent(ApplicationProvider.getApplicationContext(), EventDetailsOrganizer.class)
                    .putExtra("eventDocumentId", "testEvent"));


    @Test
    public void testActivityDisplaysCorrectly() {
        // Mock the intent used to start the activity
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailsOrganizer.class);
        startIntent.putExtra("eventDocumentId", "mockEventDocId");

        // Launch the activity with the mocked intent
        intentsTestRule.launchActivity(startIntent);

        // Verify UI components are displayed
        onView(withId(R.id.eventName)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsAboutContent)).check(matches(isDisplayed()));
        onView(withId(R.id.location)).check(matches(isDisplayed()));
        onView(withId(R.id.dateandtime)).check(matches(isDisplayed()));
        onView(withId(R.id.QRCodeImage)).check(matches(isDisplayed()));
        onView(withId(R.id.detailsAboutContent)).check(matches(isDisplayed()));
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.signupscount)).check(doesNotExist());
        onView(withId(R.id.back_button)).perform(click());
    }
}
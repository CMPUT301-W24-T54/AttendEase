package com.example.attendease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class EventDetailsSwitchTest {

    @Rule
    public ActivityScenarioRule<EventDetailsOrganizer> scenario = new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), EventDetailsOrganizer.class)
            .putExtra("eventDocumentId", "testEvent"));

    @Test
    public void testSignUpList() {
        Intents.init();
        onView(withId(R.id.signUpsSeeAllButton)).perform(ViewActions.click());
        intended(hasComponent(SignupsListActivity.class.getName()));
        Intents.release();
    }
    @Test
    public void testAttendanceList() {
        Intents.init();
        onView(withId(R.id.scrollViewEventDetails)).perform(ViewActions.swipeUp());
        onView(withId(R.id.attendanceSeeAllButton)).perform(ViewActions.click());
        intended(hasComponent(AttendanceListActivity.class.getName()));
        Intents.release();
    }
}

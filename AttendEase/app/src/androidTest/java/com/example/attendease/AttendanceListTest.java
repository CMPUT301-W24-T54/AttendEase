package com.example.attendease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.google.firebase.Timestamp;
import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;

public class AttendanceListTest {
    String eventId = "abc";
    String title = "Switch Test";
    String description = "This test just switches to the two lists";
    String organizerId = "xyz";
    Timestamp timestamp = Timestamp.now();
    String location = "University of Alberta";
    String promoqr = "";
    String checkinqr = "";
    String posterUrl = "poster";
    Boolean geo = false;
    Integer max = 10;
    Event newEvent = new Event(eventId, title, description, organizerId, timestamp, location, promoqr, checkinqr, posterUrl, geo, max);

    @Rule
    public ActivityScenarioRule<EventDetailsOrganizer> scenario = new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), EventDetailsOrganizer.class)
            .putExtra("event", newEvent));
    @Test
    public void testMapActivityIntent() {
        ActivityScenario.launch(AttendanceListActivity.class);

        // Verify that map view is displayed
        onView(withId(R.id.mapView)).check(matches(isDisplayed()));

        // Perform click on map view
        onView(withId(R.id.mapView)).perform(click());

        // Verify that intent to MapActivity is sent
        intended(hasComponent(MapActivity.class.getName()));
    }

    @Test
    public void testBackButton() {
        ActivityScenario.launch(AttendanceListActivity.class);

        // Perform click on back button
        onView(withId(R.id.back_button)).perform(click());

        // Verify that the activity is finished and closed
        //onView(withId(R.id.attendance_list_activity)).check(doesNotExist());
    }

    @Test
    public void testEventNameDisplayed() {
        ActivityScenario.launch(AttendanceListActivity.class);

        // Verify that event name TextView is displayed
        onView(withId(R.id.event_textview)).check(matches(isDisplayed()));

        // Verify the text of event name TextView
        onView(withId(R.id.event_textview)).check(matches(withText("Event Name"))); // Replace "Event Name" with your expected event name
    }

    @Test
    public void testAttendanceListDisplayed() {
        ActivityScenario.launch(AttendanceListActivity.class);

        // Verify that attendance list ListView is displayed
        onView(withId(R.id.attendancelist)).check(matches(isDisplayed()));
    }

}

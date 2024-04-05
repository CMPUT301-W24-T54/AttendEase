package com.example.attendease;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ActivityScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.Arrays;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MsgAddTest {

    private Intent startIntent;

    @Before
    public void setup() {
        Intents.init();
        startIntent = new Intent(ApplicationProvider.getApplicationContext(), MsgAdd.class);
        ArrayList<String> eventIDs = new ArrayList<>(Arrays.asList("event1", "event2"));
        ArrayList<String> eventsList = new ArrayList<>(Arrays.asList("Event 1", "Event 2"));
        startIntent.putStringArrayListExtra("eventIDs", eventIDs);
        startIntent.putStringArrayListExtra("eventslist", eventsList);
    }


    @Test
    public void testPostMessage() {

        // Launch the activity
        try (ActivityScenario<MsgAdd> scenario = ActivityScenario.launch(startIntent)) {
            onView(withId(R.id.Title)).perform(typeText("Test Title"));
            Espresso.closeSoftKeyboard(); // Close the keyboard after typing text
            onView(withId(R.id.body_text)).perform(typeText("Test Body"));
            Espresso.closeSoftKeyboard(); // Close the keyboard again after typing in the second field

            // Click the post button
            onView(withId(R.id.post_button)).perform(click());

        }
    }

    @Test
    public void testSpinnerSelection() {

        try (ActivityScenario<MsgAdd> scenario = ActivityScenario.launch(startIntent)) {

            onView(withId(R.id.Event)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("Event 2"))).perform(click());
            onView(withId(R.id.Event)).check(matches(withSpinnerText(containsString("Event 2"))));
        }
    }

    @After
    public void tearDown() {
        Intents.release();
    }
}

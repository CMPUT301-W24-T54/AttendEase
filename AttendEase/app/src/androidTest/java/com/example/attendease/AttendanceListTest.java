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
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)

public class AttendanceListTest {
    Event newEvent = new Event("abc", "Attendance List Test",
            "This is a test", "xyz", Timestamp.now(),
            "University of Alberta", "", "",
            "poster", true, 5);

    @Rule
    public IntentsTestRule<AttendanceListActivity> intentsTestRule =
            new IntentsTestRule<>(AttendanceListActivity.class, true, false);

    @Test
    public void eventNameMatch() {
        Intents.init();
        onView(withId(R.id.event_textview))
                .check(matches(isDisplayed()))
                .check(matches(withText("Attendance List Test")));

        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.event_textview)).check(doesNotExist()); // Check that the event name TextView is no longer visible, indicating activity is finished

        onView(withId(R.id.attendancecount))
                .check(matches(isDisplayed()))
                .check(matches(withText("Total: 0"))); // Assuming initial attendance count is 0

        onView(withId(R.id.attendancelist)).check(matches(isDisplayed()));

        onView(withId(R.id.attendancelist)).perform(ViewActions.swipeUp()); // Example scroll action, adjust as needed

        //onView(withId(R.id.attendancelist)).check(matches(atPosition(0, hasDescendant(withText("Attendee Name 1"))))); // Assuming attendee name at position 0 is "Attendee Name 1"

        //onView(withId(R.id.attendancelist)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        //intended(hasComponent(AttendeeDetailActivity.class.getName())); // Assuming clicking on an item opens AttendeeDetailActivity

        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.event_textview)).check(doesNotExist());

        Intents.release();
    }

    @Test
    public void testBackButtonIntent() {
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), AttendanceListActivity.class)
                .putExtra("event", newEvent);

        intentsTestRule.launchActivity(startIntent);
    }
}

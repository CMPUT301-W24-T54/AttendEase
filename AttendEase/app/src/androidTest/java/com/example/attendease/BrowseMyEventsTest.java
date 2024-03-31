package com.example.attendease;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;
import android.content.Intent;


import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BrowseMyEventsTest {

    @Rule
    public ActivityTestRule<BrowseMyEvent> activityTestRule =
            new ActivityTestRule<>(BrowseMyEvent.class, true, false);

    private final CountingIdlingResource idlingResource = new CountingIdlingResource("Firestore");

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String addedEventId;
    private String addedAttendeeId;

    @Before
    public void setUp() throws Exception {
        Intents.init(); // Moved inside setUp

        setupFirestoreData();
        IdlingRegistry.getInstance().register(idlingResource);

        // Launch activity with the added attendee ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BrowseMyEvent.class);
        intent.putExtra("attendee", new Attendee(addedAttendeeId, "Test Attendee"));
        activityTestRule.launchActivity(intent);
    }

    @After
    public void tearDown() throws Exception {
        cleanupFirestoreData();
        IdlingRegistry.getInstance().unregister(idlingResource);
        Intents.release(); // Ensure this is called here to match with Intents.init()
    }

    @Test
    public void testNavigateToPage() {
        onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
    }

    private void setupFirestoreData() throws Exception {
        // Add test data to Firestore

        Map<String, Object> attendee = new HashMap<>();
        attendee.put("name", "Test Attendee");
        attendee.put("email", "test@example.com");

        // Add attendee record and get its ID
        addedAttendeeId = Tasks.await(db.collection("attendees").add(attendee)).getId();

        Map<String, Object> event = new HashMap<>();
        event.put("title", "Test Event");
        event.put("description", "This is a test event.");
        event.put("attendeeId", addedAttendeeId);  // Linking event to attendee

        // Add event record and get its ID
        addedEventId = Tasks.await(db.collection("events").add(event)).getId();
    }

    private void cleanupFirestoreData() throws Exception {
        // Delete added event and attendee records from Firestore
        if (addedEventId != null) {
            Tasks.await(db.collection("events").document(addedEventId).delete());
        }
        if (addedAttendeeId != null) {
            Tasks.await(db.collection("attendees").document(addedAttendeeId).delete());
        }
    }
}
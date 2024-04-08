package com.example.attendease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapActivityTest {

    Event testEvent = new Event(
            "bd7145c0-2fdb-4796-8098-49d41feb9ed2_1712533095391",
            "Test Event Title",
            "Test Event Description",
            "testOrganizerId",
            new Timestamp(60, 60),
            "Test Event Location",
            "Test Promo QR",
            "Test Check-In QR",
            "Test Poster URL",
            true,  // Set isGeoTrackingEnabled to true or false as needed
            100    // Set maxAttendees to an appropriate value
    );
    @Rule
    public ActivityScenarioRule<MapActivity> activityScenarioRule = new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class).putExtra("event", testEvent));

    @Mock
    private CollectionReference mockCheckInsRef;

    @Mock
    private CollectionReference mockAttendeesRef;

    @Before
    public void setUp() {
        // Initialize Mockito annotations
        Intents.init();
        MockitoAnnotations.initMocks(this);

        // Launch the activity
        ActivityScenario<MapActivity> activityScenario = activityScenarioRule.getScenario();

        // Provide the mocked dependencies to the MapActivity instance
        activityScenario.onActivity(activity -> {
            activity.checkInsRef = mockCheckInsRef;
            activity.attendeesRef = mockAttendeesRef;
        });
    }

    @Test
    public void testMapActivity() {

        // Simulate Firestore query response
        Task<QuerySnapshot> mockTask = Mockito.mock(Task.class);
        Mockito.when(mockTask.isSuccessful()).thenReturn(true);

        QuerySnapshot mockSnapshot = Mockito.mock(QuerySnapshot.class);
        Mockito.when(mockTask.getResult()).thenReturn(mockSnapshot);

        // Perform Espresso actions and assertions based on your test scenario
        // For example, verify that certain UI elements are displayed or perform actions on them
        onView(withId(R.id.map)).perform(ViewActions.swipeLeft());
        onView(withId(R.id.map)).perform(ViewActions.swipeRight());
    }

    @After
    public void teardown() {
        Intents.release();
    }
}

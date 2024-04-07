package com.example.attendease;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AttendanceListTest {
    //initialise firestore
    private FirebaseFirestore firestore;

    @Before
    public void setup() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseApp.initializeApp(appContext);
        firestore = FirebaseFirestore.getInstance();

    }

    @Before
    public void setupEspressoIntents() {
        Intents.init();
    }

    @After
    public void releaseEspressoIntents() {
        Intents.release();
    }

    @Test
    public void attendeesInListView_MatchDatabaseCount() throws InterruptedException {
        AtomicInteger expectedCount = new AtomicInteger();

        // Assuming "eventDocID" is a static known document ID for testing purposes
        String eventDocID = "yourTestEventDocumentID";

        // Count attendees from Firestore for a specific event
        firestore.collection("checkIns").whereEqualTo("eventID", eventDocID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        expectedCount.set(task.getResult().size());
                    } else {
                        Log.e("Test", "Failed to fetch attendees from Firestore", task.getException());
                    }
                });

        // Adjust sleep time as needed, or consider using Espresso Idling Resources for better practice
        Thread.sleep(5000);

        // Launch the activity with a test intent containing the event ID
        Intent intent = new Intent();
        intent.putExtra("eventDocID", eventDocID);
        ActivityScenario.launch(AttendanceListActivity.class);

        // Assuming your Activity updates a TextView with the attendee count
        onView(withId(R.id.attendancecount)).check(matches(withText(containsString(String.valueOf(expectedCount.get())))));
    }

    @Test
    public void calculateCheckInCount_correctlyCountsAttendeeCheckIns() throws Throwable {
        // Mocking DocumentSnapshot objects
        DocumentSnapshot doc1 = mock(DocumentSnapshot.class);
        when(doc1.getString("attendeeID")).thenReturn("123");
        DocumentSnapshot doc2 = mock(DocumentSnapshot.class);
        when(doc2.getString("attendeeID")).thenReturn("456");
        DocumentSnapshot doc3 = mock(DocumentSnapshot.class);
        when(doc3.getString("attendeeID")).thenReturn("123");

        // Creating a list of mocked DocumentSnapshots
        List<DocumentSnapshot> documents = Arrays.asList(doc1, doc2, doc3);

        // Mocking QuerySnapshot to return our list of mocked DocumentSnapshots
        QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
        when(querySnapshot.getDocuments()).thenReturn(documents);

        // Launch Activity within a scenario
        try (ActivityScenario<AttendanceListActivity> scenario = ActivityScenario.launch(AttendanceListActivity.class)) {
            scenario.onActivity(new ActivityScenario.ActivityAction<AttendanceListActivity>() {
                @Override
                public void perform(AttendanceListActivity activity) {
                    try {
                        // Reflection to access the private method
                        Method method = AttendanceListActivity.class.getDeclaredMethod("calculateCheckInCount", QuerySnapshot.class, String.class);
                        method.setAccessible(true);

                        // Invoke the private method with mocked data
                        int count = (Integer) method.invoke(activity, querySnapshot, "123");

                        assertEquals(2, count);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}

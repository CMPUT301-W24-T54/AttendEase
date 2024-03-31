package com.example.attendease;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.core.AllOf.allOf;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BrowseMyEventsTest {
    Attendee attendee = new Attendee("sample_attendee", "name", "phone", "email", "image", false);
    //@Rule
    private ActivityScenario<BrowseMyEvent> scenario;
    private static final String IDLING_RESOURCE_NAME = "FirebaseLoading";
    private CountingIdlingResource countingIdlingResource;
    String txt = "Sample text";
    //@Before
    public void setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
            uiAutomation.executeShellCommand("settings put global transition_animation_scale 0");
            uiAutomation.executeShellCommand("settings put global window_animation_scale 0");
            uiAutomation.executeShellCommand("settings put global animator_duration_scale 0");
        }
        CountDownLatch latch = new CountDownLatch(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add data to Firestore

        Timestamp currentTimestamp = Timestamp.now();
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("attendeeID", "sample_attendee");
        eventData.put("eventID", "sample_test");
        eventData.put("timeStamp", "2024-03-07 15:35:34");



        Event newEvent = new Event(txt, txt, txt, txt, currentTimestamp, txt, null, txt, txt, false, 0);
        db.collection("signIns").add(eventData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                latch.countDown();

            }
        });
        db.collection("events").document("sample_test")
                .set(newEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        latch.countDown();
                        // Data added successfully, now we can proceed with the test




                    }
                });
        try {
            // Wait for all cleanup tasks to complete
            latch.await();
        } catch (InterruptedException e) {
            // Handle interruption
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        countingIdlingResource = new CountingIdlingResource(IDLING_RESOURCE_NAME);
        IdlingRegistry.getInstance().register(countingIdlingResource);
        scenario = ActivityScenario.launch(new Intent(ApplicationProvider.getApplicationContext(), BrowseMyEvent.class).putExtra("attendee", attendee));

        /*db.collection("events").add(newEvent);
        countingIdlingResource = new CountingIdlingResource(IDLING_RESOURCE_NAME);
        IdlingRegistry.getInstance().register(countingIdlingResource);*/
    }

    //@After
    public void cleanup() {


        IdlingRegistry.getInstance().unregister(countingIdlingResource);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(2);
        db.collection("events").whereEqualTo("title", txt)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("events").document(document.getId()).delete();
                            latch.countDown();
                        }

                    }
                });
        db.collection("signIns").whereEqualTo("attendeeID", "sample_attendee")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("signIns").document(document.getId()).delete();
                            latch.countDown();
                        }
                    }
                });
        try {
            // Wait for all cleanup tasks to complete
            latch.await();
        } catch (InterruptedException e) {
            // Handle interruption
        }
        scenario.close();

    }

    @Test
    public void testNavigationToDifferentPage() {
        setup();
        onIdle();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Intents.init();
        //onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.Event_list))
                .atPosition(0) // Check the presence of an item at position 0
                .perform(click());
        intended(hasComponent(EventDetailsAttendee.class.getName()));
        Intents.release();
        cleanup();
    }

    @Test
    public void testeventdetails() {
        setup();
        onIdle();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Intents.init();

        //onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
        boolean textFound=false;
        onData(anything())
                .inAdapterView(withId(R.id.Event_list))
                .atPosition(0) // Check the presence of an item at position 0
                .perform(click());

        onView(withId(R.id.Location)).check(matches(withText("Sample text")));
        onView(withId(R.id.description)).check(matches(withText("Sample text")));
        //onView(withId(R.id.description)).check(matches(withText(txt)));
        //Intents.release();
        cleanup();
    }

}

package com.example.attendease;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerNotificationTest {
    Attendee attendee = new Attendee("sample_test_attendees", "name", "phone", "email", "image", false);
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
        IdlingRegistry.getInstance().register(FirebaseLoadingTestHelper.getIdlingResource());
        CountDownLatch latch = new CountDownLatch(2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add data to Firestore

        Timestamp currentTimestamp = Timestamp.now();

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("event", "sample_test");
        notificationData.put("event_name", txt);
        notificationData.put("timeStamp", "something");
        notificationData.put("message", txt);
        notificationData.put("sentBy", "sample_test_organizer");
        notificationData.put("title", txt);







        db.collection("notifications").document("notification_test").set(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
            }
        });
        db.collection("attendees").document("sample_test_attendees")
                .set(attendee)
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




        //FirebaseLoadingTestHelper.increment();
        scenario = ActivityScenario.launch(new Intent(ApplicationProvider.getApplicationContext(), OrganizerNotifications.class).putExtra("deviceId", "sample_test_organizer"));

        /*db.collection("events").add(newEvent);
        countingIdlingResource = new CountingIdlingResource(IDLING_RESOURCE_NAME);
        IdlingRegistry.getInstance().register(countingIdlingResource);*/
    }

    //@After
    public void cleanup() {


        //IdlingRegistry.getInstance().unregister(countingIdlingResource);
        IdlingRegistry.getInstance().unregister(FirebaseLoadingTestHelper.getIdlingResource());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CountDownLatch latch = new CountDownLatch(2);

        db.collection("notifications").document("notification_test").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
            }
        });
        db.collection("attendees").document("sample_test_attendees").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                latch.countDown();
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
    public void testNavigationToMsgView() {
        setup();
        Intents.init();
        //onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.Msg_list))
                .atPosition(0) // Check the presence of an item at position 0
                .perform(click());
        intended(hasComponent(ViewMsgOrganizer.class.getName()));
        Intents.release();
        cleanup();
    }
    @Test
    public void CheckCorrectNotification() {
        setup();
        //onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
        onData(anything())
                .inAdapterView(withId(R.id.Msg_list))
                .atPosition(0) // Check the presence of an item at position 0
                .perform(click());
        onView(withId(R.id.event_name)).check(matches(withText(txt)));
        onView(withId(R.id.body)).check(matches(withText(txt)));
        onView(withId(R.id.Title)).check(matches(withText(txt)));
        cleanup();
    }
}

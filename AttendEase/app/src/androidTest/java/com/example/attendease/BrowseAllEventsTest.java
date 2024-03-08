package com.example.attendease;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;

import static androidx.test.espresso.action.ViewActions.scrollTo;
import static
        androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.AllOf.allOf;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class BrowseAllEventsTest {
    @Rule
    public ActivityScenarioRule<BrowseAllEvents> scenario = new ActivityScenarioRule<BrowseAllEvents>(new Intent(ApplicationProvider.getApplicationContext(), BrowseAllEvents.class).putExtra("deviceID", "testDevice"));
    private static final String IDLING_RESOURCE_NAME = "FirebaseLoading";
    private CountingIdlingResource countingIdlingResource;
    String txt = "Sample text";
    @Before
    public void setup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
            uiAutomation.executeShellCommand("settings put global transition_animation_scale 0");
            uiAutomation.executeShellCommand("settings put global window_animation_scale 0");
            uiAutomation.executeShellCommand("settings put global animator_duration_scale 0");
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add data to Firestore

        Timestamp currentTimestamp = Timestamp.now();
        Event newEvent = new Event(txt, txt, txt, txt, currentTimestamp, txt, null, txt, txt, false, 0);
        db.collection("events")
                .add(newEvent)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Data added successfully, now we can proceed with the test
                        countingIdlingResource = new CountingIdlingResource(IDLING_RESOURCE_NAME);
                        IdlingRegistry.getInstance().register(countingIdlingResource);
                    }
                });

        /*db.collection("events").add(newEvent);
        countingIdlingResource = new CountingIdlingResource(IDLING_RESOURCE_NAME);
        IdlingRegistry.getInstance().register(countingIdlingResource);*/
    }

    @After
    public void cleanup() {

        IdlingRegistry.getInstance().unregister(countingIdlingResource);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").whereEqualTo("title", txt)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("events").document(document.getId()).delete();
                        }
                    }
                });
    }

    @Test
    public void testNavigationToDifferentPage() {
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
        intended(hasComponent(EventDetails.class.getName()));
        Intents.release();
    }


    @Test
    public void testeventdetails() {
        onIdle();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Intents.init();

        //onView(withId(R.id.Event_list)).check(matches(isDisplayed()));
        boolean textFound=false;
        while (true) {
            if (textFound) {
                break; // Exit the loop if the text is found
            }

            try {
                onView(allOf(withText(txt), isDisplayed())).perform(click());
                textFound = true; // Set flag to true if text is found
            } catch (Exception e) {
                // Swiping up
                onView(withId(R.id.Event_list)).perform(ViewActions.swipeUp());
            }
        }
        onView(withId(R.id.EventTitle)).check(matches(withText(txt)));
        onView(withId(R.id.description)).check(matches(withText(txt)));
        //Intents.release();
    }


}

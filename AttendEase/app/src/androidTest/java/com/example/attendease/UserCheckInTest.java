package com.example.attendease;

import android.app.Activity;
import android.provider.Settings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static org.junit.Assert.assertEquals;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserCheckInTest {

    @Rule
    public ActivityScenarioRule<UserCheckIn> scenario = new ActivityScenarioRule<>(UserCheckIn.class);

    @Test
    public void testCheckInAttendee() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendeesRef = db.collection("attendees");

        String deviceID = Settings.Secure.getString(ApplicationProvider.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        onView(withId(R.id.editText_name)).perform(ViewActions.replaceText("Test Attendee"));
        onView(withId(R.id.button_submit)).perform(ViewActions.click());


        final CountDownLatch latch = new CountDownLatch(1);


        Task<DocumentSnapshot> task = attendeesRef.document(deviceID).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");

                assertEquals("Test Attendee", name);
                latch.countDown();
            }
        });

        latch.await(); // Make sure to await the latch countdown
    }

    @Test
    public void testNavigationToAttendeeDashboard() {
        onView(withId(R.id.editText_name)).perform(ViewActions.replaceText("Test Attendee"));
        onView(withId(R.id.button_submit)).perform(ViewActions.click());

        // Check that we have navigated to AttendeeDashboardActivity
        intended(hasComponent(AttendeeDashboardActivity.class.getName()));
    }

    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button)).perform(ViewActions.click());

        // Check that we have navigated back to MainActivity
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testCheckInWithoutName() {
        onView(withId(R.id.button_submit)).perform(ViewActions.click());

        // Get the current activity
        final Activity[] currentActivity = new Activity[1];
        scenario.getScenario().onActivity(new ActivityScenario.ActivityAction<UserCheckIn>() {
            @Override
            public void perform(UserCheckIn activity) {
                currentActivity[0] = activity;
            }
        });

        // Check that a toast with the correct message is displayed
        onView(withText("Name cannot be empty."))
                .inRoot(withDecorView(not(is(currentActivity[0].getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}

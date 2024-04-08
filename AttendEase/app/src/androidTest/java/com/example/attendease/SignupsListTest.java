package com.example.attendease;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.attendease.Event;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SignupsListTest {

    @Rule
    public ActivityTestRule<SignupsListActivity> activityRule =
            new ActivityTestRule<>(SignupsListActivity.class, true, false);

    private Event mockEvent;
    private Intent intent;

    @Before
    public void setUp() {
        mockEvent = new Event("123", "Test Event", "Test Description", "1", new Timestamp(new Date()), "Test Location", "Test PromoQR", "Test CheckInQR", "Test PosterUrl", false, 100);
        intent = new Intent(ApplicationProvider.getApplicationContext(), SignupsListActivity.class);
        intent.putExtra("event", mockEvent);
        activityRule.launchActivity(intent);
    }

    @Test
    public void setUpEventName() {
        onView(withId(R.id.event_textview)).check(matches(withText("Test Event")));
    }

    @Test
    public void backButton() {
        onView(withId(R.id.back_button)).perform(click());
        assert(activityRule.getActivity().isFinishing());
    }

    @Test
    public void signUpsCount_isVisible() {
        onView(withId(R.id.signupscount)).check(matches(isDisplayed()));
    }

    @Test
    public void setUpSignUpsListView_setsCorrectSignUpsCount() throws Exception {

        Method method = SignupsListActivity.class.getDeclaredMethod("setUpSignUpsListView", String.class);
        method.setAccessible(true);
        method.invoke(activityRule.getActivity(), "123");


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference signInsRef = db.collection("signIns");
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger(0);

        signInsRef.whereEqualTo("eventID", "123").get().addOnSuccessListener(queryDocumentSnapshots -> {
            count.set(queryDocumentSnapshots.size());
            latch.countDown();
        });

        latch.await();

        onView(withId(R.id.signupscount)).check(matches(withText("Total Attendees: " + count.get())));
    }
}
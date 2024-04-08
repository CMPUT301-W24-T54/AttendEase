package com.example.attendease;

import android.content.Intent;
import android.util.Log;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class EventDetailsAdminTest {

    @Rule
    public IntentsTestRule<EventDetailsAdmin> intentsTestRule =
            new IntentsTestRule<>(EventDetailsAdmin.class, true, false);

    private String testDocumentId;
    private final String testCollectionName = "events";

    @Before
    public void setUp() throws Exception {
        testDocumentId = "testEvent_" + UUID.randomUUID().toString();
        prepareFirestoreTestData(testDocumentId);
    }

    private void prepareFirestoreTestData(String documentId) throws InterruptedException {
        Map<String, Object> testData = new HashMap<>();
        testData.put("title", "Test Event Title");
        testData.put("description", "Test Event Description");

        CountDownLatch latch = new CountDownLatch(1);

        FirebaseFirestore.getInstance().collection(testCollectionName).document(documentId).set(testData)
                .addOnCompleteListener(task -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testActivity() {
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailsAdmin.class)
                .putExtra("event", testDocumentId);

        intentsTestRule.launchActivity(startIntent);

        onView(withId(R.id.event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.about_content)).check(matches(isDisplayed()));
        onView(withId(R.id.location)).check(matches(isDisplayed()));
        onView(withId(R.id.date_time)).check(matches(isDisplayed()));
//        onView(withId(R.id.qr_code_image)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_left)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        FirebaseFirestore.getInstance().collection(testCollectionName).document(testDocumentId).delete()
                .addOnCompleteListener(task -> latch.countDown());

        boolean awaitSuccess = latch.await(5, TimeUnit.SECONDS);
        if (!awaitSuccess) {
            Log.e("EventDetailsAdminTest", "Timeout waiting for test data cleanup.");
        }
    }
}

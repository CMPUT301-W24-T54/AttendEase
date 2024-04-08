package com.example.attendease;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
public class OrganizerMyEventsTest {
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
    public void eventsInRecyclerView_ShouldMatchDatabaseCount() throws InterruptedException {
        AtomicInteger expectedCount = new AtomicInteger();

        String androidId = Settings.Secure.getString(InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        CountDownLatch latch = new CountDownLatch(1);

        // Count events from Firestore
        firestore.collection("events").whereEqualTo("organizerId", androidId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        expectedCount.set(task.getResult().size());
                    } else {
                        Log.e("Test", "Failed to fetch events from Firestore", task.getException());
                    }
                    latch.countDown();
                });
        latch.await();
//        Thread.sleep(5000);

        // Launch the activity
        ActivityScenario.launch(OrganizerMyEventsActivity.class);

        // Check if the RecyclerView item count matches the expected count
        onView(withId(R.id.rvMyEvents)).check(matches(CustomMatchers.withItemCount(expectedCount.get())));
    }

    @Test
    public void clickingOnHomeMenuItem_ShouldStartOrganizerDashboardActivity() {
        ActivityScenario.launch(OrganizerMyEventsActivity.class);
        onView(withId(R.id.nav_home)).perform(click());
        // Verifies that the expected Activity is launched
        intended(hasComponent(OrganizerDashboardActivity.class.getName()));
    }
}

class CustomMatchers {
    public static TypeSafeMatcher<View> withItemCount(final int expectedCount) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                if (!(item instanceof RecyclerView)) return false;
                RecyclerView recyclerView = (RecyclerView) item;
                return recyclerView.getAdapter().getItemCount() == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have " + expectedCount + " items");
            }
        };
    }
}
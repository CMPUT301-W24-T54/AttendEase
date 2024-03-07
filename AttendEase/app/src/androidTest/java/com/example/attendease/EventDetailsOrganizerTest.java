package com.example.attendease;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;

import android.app.ListActivity;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventDetailsOrganizerTest {

    @Rule
    public ActivityScenarioRule<EventDetailsOrganizer> scenario1 =
            new ActivityScenarioRule<EventDetailsOrganizer>(new Intent(ApplicationProvider.getApplicationContext(), EventDetailsOrganizer.class)
            .putExtra("eventDocumentId", "testEvent"));
    @Test
    public void testActivitySwitch(){
        onView(withId(R.id.detailsAboutContent)).check(matches(isDisplayed()));
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.signUpsSeeAllButton)).perform(click());
        onView(withId(R.id.signupslist)).check(matches(isDisplayed()));
        onView(withId(R.id.signupscount)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
        onView(withId(R.id.detailsAboutContent)).check(matches(isDisplayed()));
        onView(withId(R.id.signupscount)).check(doesNotExist());
    }
}

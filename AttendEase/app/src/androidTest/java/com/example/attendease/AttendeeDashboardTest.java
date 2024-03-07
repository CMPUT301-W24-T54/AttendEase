package com.example.attendease;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import static
        androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.action.ViewActions;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeDashboardTest {
    @Rule
    //public ActivityScenarioRule<AttendeeDashboardActivity> scenario = new ActivityScenarioRule<AttendeeDashboardActivity>(AttendeeDashboardActivity.class);
    public ActivityScenarioRule<BrowseAllEvents> scenario = new ActivityScenarioRule<BrowseAllEvents>(new Intent(ApplicationProvider.getApplicationContext(), AttendeeDashboardActivity.class).putExtra("deviceID", "testDevice"));
    @Test
    public void testSeeAll(){
        // Click on see all button
        Intents.init();
        onView(withId(R.id.see_all2)).perform(click());
        intended(hasComponent(BrowseAllEvents.class.getName()));
        Intents.release();


    }
    @Test
    public void testprofilebutton(){
        // Click on see all button
        Intents.init();
        onView(withId(R.id.nav_profile)).perform(click());
        intended(hasComponent(EditProfileActivity.class.getName()));
        Intents.release();



    }

}

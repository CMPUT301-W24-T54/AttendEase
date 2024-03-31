package com.example.attendease;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ApplicationProvider;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;


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
    Attendee attendee = new Attendee("testDevice", "name", "phone", "email", "image", false);
    @Rule
    //public ActivityScenarioRule<AttendeeDashboardActivity> scenario = new ActivityScenarioRule<AttendeeDashboardActivity>(AttendeeDashboardActivity.class);

    public ActivityScenarioRule<BrowseAllEvents> scenario = new ActivityScenarioRule<BrowseAllEvents>(new Intent(ApplicationProvider.getApplicationContext(), AttendeeDashboardActivity.class).putExtra("attendee", attendee));

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

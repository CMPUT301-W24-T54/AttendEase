package com.example.attendease;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.attendease.Event;
import com.google.firebase.Timestamp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

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

    @Test
    public void setUpEventName() {
        // Arrange
        Event mockEvent = new Event("123", "Test Event", "Test Description", "1", new Timestamp(new Date()), "Test Location", "Test PromoQR", "Test CheckInQR", "Test PosterUrl", false, 100);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SignupsListActivity.class);
        intent.putExtra("event", mockEvent);

        activityRule.launchActivity(intent);

        // Assert
        onView(withId(R.id.event_textview)).check(matches(withText("Test Event")));
    }

    @Test
    public void backButton() {
        // Arrange
        Event mockEvent = new Event("123", "Test Event", "Test Description", "1", new Timestamp(new Date()), "Test Location", "Test PromoQR", "Test CheckInQR", "Test PosterUrl", false, 100);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SignupsListActivity.class);
        intent.putExtra("event", mockEvent);

        activityRule.launchActivity(intent);

        // Act
        onView(withId(R.id.back_button)).perform(click());

        // Assert
        assert(activityRule.getActivity().isFinishing());
    }

    @Test
    public void signUpsCount_isVisible() {
        // Arrange
        Event mockEvent = new Event("123", "Test Event", "Test Description", "1", new Timestamp(new Date()), "Test Location", "Test PromoQR", "Test CheckInQR", "Test PosterUrl", false, 100);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SignupsListActivity.class);
        intent.putExtra("event", mockEvent);

        activityRule.launchActivity(intent);

        // Assert
        onView(withId(R.id.signupscount)).check(matches(isDisplayed()));
    }
}
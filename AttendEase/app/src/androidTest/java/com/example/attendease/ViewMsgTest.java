package com.example.attendease;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewMsgTest {

    @Rule
    public ActivityScenarioRule<ViewMsg> activityRule
            = new ActivityScenarioRule<>(ViewMsg.class);

    @Test
    public void testDisplayMessage() {
        // Create a bundle with the extras
        Bundle bundle = new Bundle();
        bundle.putString("Title", "Test Title");
        bundle.putString("Message", "Test Message");
        bundle.putString("sentBy", "Test Sender");

        // Create an intent with the extras
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ViewMsg.class);
        intent.putExtras(bundle);

        // Launch the activity with the intent
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));

        // Check that the text views display the correct text
        onView(withId(R.id.Title)).check(matches(withText("Test Title")));
        onView(withId(R.id.body)).check(matches(withText("Test Message")));
        onView(withId(R.id.textView9)).check(matches(withText("Test Sender")));
    }
}

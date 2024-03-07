package com.example.attendease;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BrowseAllEventsTest {

    @Test
    public void eventListIsDisplayed() {

        ActivityScenario.launch(BrowseAllEvents.class);
        onView(ViewMatchers.withId(R.id.Event_list))
                .check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void eventAdapterIsUpdated() {
        // Launch the activity
        ActivityScenario<BrowseAllEvents> scenario = ActivityScenario.launch(BrowseAllEvents.class);

        scenario.onActivity(activity -> {
            Event fakeEvent = new Event("1", "Test Event", "Test Description", "1", new Timestamp(new Date()), "Test Location", null, "Test QR", "Test URL", false, 0);
            ArrayList<Event> dataList = activity.getDataList();
            dataList.add(fakeEvent);
            activity.setDataList(dataList);
            activity.updateDataList();
        });

        // Check that the EventAdapter is updated
        onView(withId(R.id.Event_list)).check(matches(hasDescendant(withText("Test Event"))));
    }
}

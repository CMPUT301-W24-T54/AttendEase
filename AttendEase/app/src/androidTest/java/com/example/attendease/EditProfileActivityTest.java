package com.example.attendease;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileActivityTest {
    Attendee attendee = new Attendee("testDevice", "name", "phone", "email", "image", false);
    @Rule
    public ActivityScenarioRule<EditProfileActivity> scenario = new ActivityScenarioRule<>(new Intent(ApplicationProvider.getApplicationContext(), EditProfileActivity.class)
            .putExtra("attendee", attendee));


    @Test
    public void testProfileLoads() throws InterruptedException {
        // OpenAI, 2024, ChatGPT, Wait for firebase query before performing check
        final CountDownLatch latch = new CountDownLatch(1); // Create a latch with count 1
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendeesRef = db.collection("attendees");

        final String[] name = new String[1];
        final String[] phone = new String[1];
        final String[] email = new String[1];

        String deviceID = "testDevice";

        attendeesRef.document(deviceID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Document exists, retrieve attributes
                    name[0] = documentSnapshot.getString("name");
                    phone[0] = documentSnapshot.getString("phone");
                    email[0] = documentSnapshot.getString("email");
                    latch.countDown(); // Signal that the query has completed
                }
            }
        });

        // Wait for the latch countdown to complete (i.e., for the query to finish)
        latch.await();

        // Now perform assertions
        onView(withId(R.id.edit_profile_name)).check(matches(withText(name[0])));
        onView(withId(R.id.edit_profile_phone)).check(matches(withText(phone[0])));
        onView(withId(R.id.edit_profile_email)).check(matches(withText(email[0])));
    }

    @Test
    public void testProfileChanges() throws InterruptedException {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference attendeesRef = db.collection("attendees");

        String deviceID = "testDevice";

        onView(withId(R.id.edit_profile_name)).perform(ViewActions.replaceText("Tested"));
        onView(withId(R.id.edit_profile_phone)).perform(ViewActions.replaceText("1111111111"));
        onView(withId(R.id.edit_profile_email)).perform(ViewActions.replaceText("tested@email.com"));
        onView(withId(R.id.edit_profile_save_changes)).perform(ViewActions.click());

        // Wait for Firestore query to complete
        final CountDownLatch latch = new CountDownLatch(1);

        // Retrieve updated data from Firestore
        Task<DocumentSnapshot> task = attendeesRef.document(deviceID).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Get updated data from Firestore
                String name = documentSnapshot.getString("name");
                String phone = documentSnapshot.getString("phone");
                String email = documentSnapshot.getString("email");

                // Perform assertions on the retrieved data
                assertEquals("Tested", name);
                assertEquals("1111111111", phone);
                assertEquals("tested@email.com", email);

                // Signal that the query has completed
                latch.countDown();
            }
        });

        latch.await(); // Make sure to await the latch countdown
    }
    @After
    public void tearDown() throws Exception {
        // Code to delete test data from Firestore
    }

}

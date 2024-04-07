package com.example.attendease;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Before
    public void setup() {
        Intents.init();
    }
    @Test
    public void testDatabase() {
        // Context of the app under test.
        Database dbInstance = Database.getInstance();

        // Verify the singleton instance is created
        assertNotNull(dbInstance);

        // Verify Firestore and Storage instances are not null
        assertNotNull(dbInstance.getDb());
        assertNotNull(dbInstance.getStorage());

        // Verify Firestore collection references are not null
        assertNotNull(dbInstance.getAttendeesRef());
        assertNotNull(dbInstance.getOrganizersRef());
        assertNotNull(dbInstance.getAdminRef());
        assertNotNull(dbInstance.getCheckInsRef());
        assertNotNull(dbInstance.getSignInsRef());
        assertNotNull(dbInstance.getEventsRef());
        assertNotNull(dbInstance.getImagesRef());
        assertNotNull(dbInstance.getNotificationsRef());

        // Additional test to verify singleton property
        Database anotherDbInstance = Database.getInstance();
        assertTrue(dbInstance == anotherDbInstance);
    }

    @After
    public void teardown() {
        Intents.release();
    }
}

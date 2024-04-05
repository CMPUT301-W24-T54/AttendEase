package com.example.attendease;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AdminUnitTest {
    @Test
    public void testDeviceID() {
        // Initialize a new Admin object with a device ID
        Admin admin = new Admin("1234");

        // Test the getDeviceID method
        assertEquals("1234", admin.getDeviceID());

        // Test the setDeviceID method
        admin.setDeviceID("5678");
        assertEquals("5678", admin.getDeviceID());
    }
}

package com.example.attendease;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventUnitTest {
    private Event event;

    @Before
    public void setUp() {
        event = new Event("1", "title", "description", "organizerId", Timestamp.now(), "location", "promoQR", "checkInQR", "posterUrl", true, 100);
    }

    @Test
    public void testGetEventId() {
        assertEquals("1", event.getEventId());
    }

    @Test
    public void testGetTitle() {
        assertEquals("title", event.getTitle());
    }

    @Test
    public void testGetDescription() {
        assertEquals("description", event.getDescription());
    }

    @Test
    public void testGetOrganizerId() {
        assertEquals("organizerId", event.getOrganizerId());
    }

    @Test
    public void testGetLocation() {
        assertEquals("location", event.getLocation());
    }

    @Test
    public void testGetPromoQR() {
        assertEquals("promoQR", event.getPromoQR());
    }

    @Test
    public void testGetCheckInQR() {
        assertEquals("checkInQR", event.getCheckInQR());
    }

    @Test
    public void testGetPosterUrl() {
        assertEquals("posterUrl", event.getPosterUrl());
    }

    @Test
    public void testIsGeoTrackingEnabled() {
        assertTrue(event.getisGeoTrackingEnabled());
    }

    @Test
    public void testGetMaxAttendees() {
        assertEquals(100, event.getMaxAttendees());
    }
}

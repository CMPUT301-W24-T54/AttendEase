package com.example.attendease;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class OrganizerUnitTest {
    private Organizer organizer;

    @Before
    public void setUp() {
        organizer = new Organizer("testDevice", "test@organizer.com", "999-999-9999");
    }

    @Test
    public void getIdTest() {
        assertEquals(organizer.getId(), "testDevice");
    }

    @Test
    public void getPhoneNumberTest() {
        assertEquals(organizer.getContactNumber(), "999-999-9999");
    }

    @Test
    public void getEmailTest() {
        assertEquals(organizer.getEmail(), "test@organizer.com");
    }

    @Test
    public void setIdTest() {
        assertEquals(organizer.getId(), "testDevice");
    }

    @Test
    public void setPhoneNumberTest() {
        assertEquals(organizer.getContactNumber(), "999-999-9999");
    }

    @Test
    public void setEmailTest() {
        assertEquals(organizer.getEmail(), "test@organizer.com");
    }

    @Test
    public void setPhoneTest() {
        organizer.setContactNumber("111-111-1111");
        assertEquals(organizer.getContactNumber(), "111-111-1111");
    }
}

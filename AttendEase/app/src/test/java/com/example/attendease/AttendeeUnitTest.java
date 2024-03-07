package com.example.attendease;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AttendeeUnitTest {

    private Attendee attendee;

    @Before
    public void setUp() {
        attendee = new Attendee("testDevice", "test", "999-999-9999", "test@email.com");
    }

    @Test
    public void getDeviceIDTest() {
        assertEquals(attendee.getDeviceID(), "testDevice");
    }

    @Test
    public void getNameTest() {
        assertEquals(attendee.getName(), "test");
    }

    @Test
    public void getPhoneTest() {
        assertEquals(attendee.getPhone(), "999-999-9999");
    }

    @Test
    public void getEmailTest() {
        assertEquals(attendee.getEmail(), "test@email.com");
    }

    @Test
    public void setNameTest() {
        attendee.setName("TEST");
        assertEquals(attendee.getName(), "TEST");
    }

    @Test
    public void setPhoneTest() {
        attendee.setPhone("111-111-1111");
        assertEquals(attendee.getPhone(), "111-111-1111");
    }

    @Test
    public void setEmailTest() {
        attendee.setEmail("TEST@email.com");
        assertEquals(attendee.getEmail(), "TEST@email.com");
    }
}

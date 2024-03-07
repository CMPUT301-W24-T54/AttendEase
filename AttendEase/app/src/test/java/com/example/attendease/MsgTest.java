package com.example.attendease;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MsgTest {
    private Msg msg;

    @Before
    public void setUp() {
        msg = new Msg("title", "message", "sender");
    }

    @Test
    public void testGetTitle() {
        assertEquals("title", msg.getTitle());
    }

    @Test
    public void testGetMessage() {
        assertEquals("message", msg.getMessage());
    }

    @Test
    public void testGetSentBy() {
        assertEquals("sender", msg.getSent_By());
    }

    @Test
    public void testUniqueId() {
        assertNotNull(msg.getUnique_id());
        String id = msg.getUnique_id();
        msg.setUnique_id("newId");
        assertNotEquals(id, msg.getUnique_id());
        assertEquals("newId", msg.getUnique_id());
    }
}

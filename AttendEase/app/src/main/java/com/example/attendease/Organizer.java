package com.example.attendease;

import java.util.ArrayList;

public class Organizer {
    // Attributes
    private String id; // Unique identifier from Firebase or device ID
    private String email;
    private String contactNumber;
    private ArrayList<Event> eventsOrganized;

    // Constructor
    public Organizer(String id, String email, String contactNumber) {
        this.id = id;
        this.email = email;
        this.contactNumber = contactNumber;
        this.eventsOrganized = new ArrayList<>();
    }

    // Methods for organizer's actions
    public void createEvent(String eventName, String location, String dateTime) {
        Event newEvent = new Event(eventName, location, dateTime);
        this.eventsOrganized.add(newEvent);
    }

    public void sendNotification(String message) {
        for (Event event : eventsOrganized) {
            event.notifyAttendees(message); // notifyAttendees will be part of the Event Class
        }
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public ArrayList<Event> getEventsOrganized() {
        return eventsOrganized;
    }

    public void setEventsOrganized(ArrayList<Event> eventsOrganized) {
        this.eventsOrganized = eventsOrganized;
    }
}

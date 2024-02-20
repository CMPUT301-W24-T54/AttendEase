package com.example.attendease;

import java.util.ArrayList;

/**
 * Represents an event organizer with the capability to manage events.
 */
public class Organizer {
    // Attributes
    private String id; // Unique identifier from Firebase or device ID
    private String email; // email address for the organizer
    private String contactNumber; // contact number for the organizer
    private ArrayList<Event> eventsOrganized; // Number of events organized by the organizer

    /**
     * Constructs a new Organizer with the specified details.
     * 
     * @param id Unique identifier for the organizer, typically from Firebase Authentication.
     * @param email The email address of the organizer.
     * @param contactNumber The contact phone number of the organizer.
     */
    public Organizer(String id, String email, String contactNumber) {
        this.id = id;
        this.email = email;
        this.contactNumber = contactNumber;
        this.eventsOrganized = new ArrayList<>();
    }

    /**
     * Creates a new event and adds it to the organizer's list of managed events.
     * 
     * @param eventName The name of the event.
     * @param location The location where the event will take place.
     * @param dateTime The date and time of the event.
     */
    public void createEvent(String eventName, String location, String dateTime) {
        Event newEvent = new Event(eventName, location, dateTime);
        this.eventsOrganized.add(newEvent);
    }

    /**
     * Sends a notification message to all attendees of all events organized by this organizer.
     * 
     * @param message The message to be sent as a notification.
     */
    public void sendNotification(String message) {
        for (Event event : eventsOrganized) {
            event.notifyAttendees(message); // notifyAttendees will be part of the Event Class
        }
    }

    // Getters and Setters
    
    /** Returns the unique identifier of the organizer. */
    public String getId() {
        return id;
    }

     /**
     * Sets the unique identifier of the organizer.
     * 
     * @param id The new unique identifier for the organizer.
     */
    public void setId(String id) {
        this.id = id;
    }

    /** Returns the email address of the organizer. */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the organizer.
     * 
     * @param email The new email address for the organizer.
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /** Returns the contact phone number of the organizer. */
    public String getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the contact phone number of the organizer.
     * 
     * @param contactNumber The new contact phone number for the organizer.
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /** Returns the list of events organized by this organizer. */
    public ArrayList<Event> getEventsOrganized() {
        return eventsOrganized;
    }

     /**
     * Sets the list of events organized by this organizer.
     * 
     * @param eventsOrganized The new list of events managed by the organizer.
     */
    public void setEventsOrganized(ArrayList<Event> eventsOrganized) {
        this.eventsOrganized = eventsOrganized;
    }
}

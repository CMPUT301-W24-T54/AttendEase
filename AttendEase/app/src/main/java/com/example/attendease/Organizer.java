package com.example.attendease;

public class Organizer {
    // Attributes
    private String username;
    private String password;
    private String email;
    private String contactNumber;
    private ArrayList<Event> eventsOrganized;

    // Constructor
    public Organizer(String username, String password, String email, String contactNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.contactNumber = contactNumber;
        this.eventsOrganized = new ArrayList<>();
    }

    // Methods for organizer's actions
    // Generates a new Event object and adds it to the eventsOrganized list
    // You need to implement the Event class separately

    public void createEvent(String eventName, String location, String dateTime) {
        Event newEvent = new Event(eventName, location, dateTime);
        this.eventsOrganized.add(newEvent);
    }

    // Loop through each event and its attendees, sending a notification
    // This method assumes you have a way to send notifications implemented

    public void sendNotification(String message) {
        for (Event event : eventsOrganized) {
            event.notifyAttendees(message); //notifyAttendees will be part of the Event Class
        }
    }

    // Login method
    public static boolean login(String username, String password, ArrayList<Organizer> organizers) {
        for (Organizer organizer : organizers) {
            if (organizer.getUsername().equals(username) && organizer.getPassword().equals(password)) {
                return true; // Login successful
            }
        }
        return false; // Login failed
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // Normally you would not provide a setter for the password without some form of authentication
    public void setPassword(String password) {
        this.password = password;
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

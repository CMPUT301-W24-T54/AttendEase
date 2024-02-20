package com.example.attendease;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

/**
 * Represents an event within the AttendEase application.
 * This class encapsulates all the information about an event including its location,
 * QR codes for promotion and check-in, settings/info, and attendee management functionalities.
 */
public class Event {
    private String eventId;
    private String title;
    private String description;
    private String organizerId;
    private Timestamp dateTime;
    private GeoPoint location; // For geolocation verification after halfway point
    private String promoQR;
    private String checkInQR;
    private String posterUrl; // URL to the event poster image
    private int maxAttendees; // Optional limit on attendees



    // Constructor //
    /**
     * Constructs a new Event with specified details.
     *
     * @param eventId      Unique identifier for the event.
     * @param title        Title of the event.
     * @param description  Description of the event.
     * @param organizerId  ID of the user organizing the event.
     * @param dateTime     Timestamp of when the event is scheduled.
     * @param location     GeoPoint location for the event, used for geolocation verification.
     * @param promoQR      QR code URL for event promotion.
     * @param checkInQR    QR code URL for event check-in.
     * @param posterUrl    URL of the event poster image.
     * @param maxAttendees Maximum number of attendees allowed (optional).
     */
    public Event(String eventId, String title, String description, String organizerId, Timestamp dateTime, GeoPoint location, String promoQR, String checkInQR, String posterUrl, int maxAttendees) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.organizerId = organizerId;
        this.dateTime = dateTime;
        this.location = location;
        this.promoQR = promoQR;
        this.checkInQR = checkInQR;
        this.posterUrl = posterUrl;
        this.maxAttendees = maxAttendees;
    }



    // Getters/Setters //
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getPromoQR() {
        return promoQR;
    }

    public void setPromoQR(String promoQR) {
        this.promoQR = promoQR;
    }

    public String getCheckInQR() {
        return checkInQR;
    }

    public void setCheckInQR(String checkInQR) {
        this.checkInQR = checkInQR;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }



    // QR Code Methods //
    /**
     * Placeholder method for generating and updating the check-in QR code.
     * Actual implementation should create a QR code that, when scanned, marks an attendee as checked in.
     */
    public void generateCheckInQrCode() {
        // Placeholder for generating and updating the check-in QR code
    }
    /**
     * Placeholder method for generating and updating the promotion QR code.
     * Actual implementation should create a QR code that, when scanned, routes to the event page in the app.
     */
    public void generatePromotionQrCode() {
        // Placeholder for generating and updating the promotion QR code
    }



    // Notification Methods //
    /**
     * Placeholder method for creating and sending notifications to attendees.
     * @param message The message to be sent to all attendees.
     */
    public void sendNotificationToAttendees(String message) {
        // Placeholder for creating and sending notifications to attendees
    }



    // Signups/Checkins and other miscellaneous methods interacting with firestore //
    /**
     * Placeholder methods for interacting with Firestore to manage event details and attendee lists.
     * These methods include fetching and updating attendee sign-up and check-in lists, among other functionalities.
     */
    public void fetchSignUpList() {
        // Placeholder for fetching sign-up list from Firestore
    }

    public void fetchCheckInList() {
        // Placeholder for fetching check-in list from Firestore
    }

    public void addAttendeeToSignUpList(String userId) {
        // Placeholder for adding an attendee to the sign-up list in Firestore
    }

    public void addAttendeeToCheckInList(String userId) {
        // Placeholder for adding an attendee to the check-in list in Firestore
    }
    /**
     * Updates the event details with new information provided.
     * This method allows for updating the event's title, description, date/time, location, and poster URL.
     * It's designed to be used when event details change and need to be reflected in the application and database.
     *
     * @param title        The new title for the event.
     * @param description  The new description for the event.
     * @param dateTime     The new date and time for the event.
     * @param location     The new location for the event represented as a GeoPoint.
     * @param posterUrl    The new URL for the event's poster image.
     */
    public void updateEventDetails(String title, String description, Timestamp dateTime, GeoPoint location, String posterUrl) {
        // Placeholder for logic to update event details
    }
}


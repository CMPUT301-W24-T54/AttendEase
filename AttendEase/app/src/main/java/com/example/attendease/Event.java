package com.example.attendease;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

/**
 * Represents an event within the AttendEase application.
 * This class encapsulates all the information about an event including its location,
 * QR codes for promotion and check-in, settings/info, and attendee management functionalities.
 */
public class Event implements Parcelable {
    private String eventId;
    private String title;
    private String description;
    private String organizerId;
    private Timestamp dateTime;
    private String location;
    private String promoQR;
    private String checkInQR;
    private String posterUrl; // URL to the event poster image
    private boolean isGeoTrackingEnabled;
    private int maxAttendees; // Optional limit on attendees


    // Constructor //

    /**
     * Constructs a new Event with specified details.
     *
     * @param eventId              Unique identifier for the event.
     * @param title                Title of the event.
     * @param description          Description of the event.
     * @param organizerId          ID of the user organizing the event.
     * @param dateTime             Timestamp of when the event is scheduled.
     * @param location             GeoPoint location for the event, used for geolocation verification.
     * @param promoQR              QR code URL for event promotion.
     * @param checkInQR            QR code URL for event check-in.
     * @param posterUrl            URL of the event poster image.
     * @param isGeoTrackingEnabled Identifies whether or not the organizer has enabled geo-tracking
     * @param maxAttendees         Maximum number of attendees allowed (optional).
     */
    public Event(String eventId, String title, String description, String organizerId, Timestamp dateTime, String location, String promoQR, String checkInQR, String posterUrl, boolean isGeoTrackingEnabled, int maxAttendees) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.organizerId = organizerId;
        this.dateTime = dateTime;
        this.location = location;
        this.promoQR = promoQR;
        this.checkInQR = checkInQR;
        this.posterUrl = posterUrl;
        this.isGeoTrackingEnabled = isGeoTrackingEnabled;
        this.maxAttendees = maxAttendees;
    }

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    // GETTERS AND SETTERS
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
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

    public boolean getisGeoTrackingEnabled() {
        return isGeoTrackingEnabled;
    }

    public void setisGeoTrackingEnabled(boolean isGeoTrackingEnabled) {
        this.isGeoTrackingEnabled = isGeoTrackingEnabled;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    // Notification Methods //
    /**
     * Placeholder method for creating and sending notifications to attendees.
     *
     * @param message The message to be sent to all attendees.
     */
    public void sendNotificationToAttendees(String message) {
        // Placeholder for creating and sending notifications to attendees
    }

    // Parcelable implementation
    protected Event(Parcel in) {
        eventId = in.readString();
        title = in.readString();
        description = in.readString();
        organizerId = in.readString();
        // Reads Timestamp as longs (seconds and nanoseconds)
        long seconds = in.readLong();
        int nanoseconds = in.readInt();
        dateTime = new Timestamp(seconds, nanoseconds);
        location = in.readString();
        promoQR = in.readString();
        checkInQR = in.readString();
        posterUrl = in.readString();
        isGeoTrackingEnabled = in.readByte() != 0; // true if byte is non-zero
        maxAttendees = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventId);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(organizerId);
        parcel.writeLong(dateTime.getSeconds());
        parcel.writeInt(dateTime.getNanoseconds());
        parcel.writeString(location);
        parcel.writeString(promoQR);
        parcel.writeString(checkInQR);
        parcel.writeString(posterUrl);
        parcel.writeByte((byte) (isGeoTrackingEnabled ? 1 : 0)); // true if isGeoTrackingEnabled is true, false otherwise
        parcel.writeInt(maxAttendees);
    }
}
package com.example.attendease;

import java.io.Serializable;


/**
 * This class represents an Attendee user
 */

public class Attendee implements Serializable{
    private String deviceID;
    private String name;
    private String email;
    private String phone;
    private String image;
    private boolean geoTrackingEnabled;  // TODO : use this when checking in for maps
    private int checkInCount; // New field for check-in count
    private String url;


    /**
     * Constructs an Attendee
     * @param deviceID Android device ID
     * @param name Name
     * @param phone Phone
     * @param email Email
     * @param image Profile Picture
     */
    public Attendee(String deviceID, String name, String phone, String email, String image, boolean geoTrackingEnabled) {
        // Use this constructor if the Attendee already exists
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.geoTrackingEnabled = geoTrackingEnabled;
    }

    /**
     * Constructs an Attendee
     * @param deviceID Android device ID
     * @param name Name
     * @param phone Phone
     * @param email Email
     */
    public Attendee(String deviceID, String name, String phone, String email) {
        // Use this constructor if the Attendee already exists
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = null;
    }

    public Attendee(String name, int checkInCount, String url) {
        this.name = name;
        this.checkInCount = checkInCount;
        this.url = url;
    }
    public Attendee(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void signUp() {}  // TODO Implement signUp functionality on database side
    public void checkIn() {} // TODO Implement checkIn functionality on database side

    /**
     * Gets the device ID associated with this user.
     *
     * @return The device ID.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the device ID associated with this user.
     *
     * @param deviceID The new device ID to set.
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Gets the name of this user.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this user.
     *
     * @param name The new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of this user.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this user.
     *
     * @param email The new email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of this user.
     *
     * @return The user's phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of this user.
     *
     * @param phone The new phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the image URL associated with this user.
     *
     * @return The URL of the user's image.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image URL associated with this user.
     *
     * @param image The new image URL to set.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Checks if geo tracking is enabled for this attendee.
     * @return true if geo tracking is enabled, false otherwise.
     */
    public boolean isGeoTrackingEnabled() {
        return geoTrackingEnabled;
    }

    /**
     * Sets whether geo tracking is enabled for this attendee.
     * @param geoTrackingEnabled true to enable geo tracking, false to disable.
     */
    public void setGeoTrackingEnabled(boolean geoTrackingEnabled) {
        this.geoTrackingEnabled = geoTrackingEnabled;
    }

    /**
     * Gets the check-in count for this attendee.
     * @return The check-in count.
     */
    public int getCheckInCount() {
        return checkInCount;
    }

    /**
     * Sets the check-in count for this attendee.
     * @param checkInCount The new check-in count to set.
     */
    public void setCheckInCount(int checkInCount) {
        this.checkInCount = checkInCount;
    }

    /**
     * Gets the URL associated with this user.
     * @return The URL.
     */
    public String getUrl() {
        return url;
    }
}

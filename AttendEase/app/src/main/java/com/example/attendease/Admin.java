package com.example.attendease;

/**
 * This class represents an Admin
 */

public class Admin {
    private String deviceID;

    /**
     * Constructs an Admin
     * @param deviceID Android device ID
     */
    public Admin(String deviceID){
        this.deviceID = deviceID;
    }

    /**
     * Gets the device ID associated with admin.
     * @return The device ID.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the device ID associated with admin.
     * @param deviceID The new device ID to set.
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
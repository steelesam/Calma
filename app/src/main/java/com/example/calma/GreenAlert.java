package com.example.calma;

public class GreenAlert {

    private double longitude;
    private double latitude;
    private long time;
    private String date;
    private String recipient;

    /**
     * Constructor with arguments
     * @param longitude
     * @param latitude
     */
    public GreenAlert(double longitude, double latitude, long time, String date, String recipient) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.date = date;
        this.recipient = recipient;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


}

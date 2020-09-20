package com.example.calma;

public abstract class Alert {

    private String longitude;
    private String latitude;
    private String time;
    private String date;
    private String recipient;

    /**
     * Constructor with arguments
     *
     * @param longitude
     * @param latitude
     */
    public Alert(String longitude, String latitude, String time, String date, String recipient) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.time = time;
        this.date = date;
        this.recipient = recipient;
    }

    public Alert() {
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}

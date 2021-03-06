package com.example.calma;

public class AmberAlert extends Alert {


    /**
     * Constructor with arguments
     *
     * @param longitude
     * @param latitude
     */
    public AmberAlert(String longitude, String latitude, String time, String date, String recipient) {
        super(longitude, latitude, time, date, recipient);
    }

    public AmberAlert() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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

    @Override
    public String toString() {
        return "Date sent: " + date + "\n" + "Sent to: " + recipient + "\n" + "Longitude: " + longitude + ", Latitude: " + latitude + "\n"
                + "Time sent: "+ time + "\n";

    }
}

package com.example.weuller.peladaesporteclube.Models;

/**
 * Created by ismael on 19/09/17.
 */

public class FootballMatch {

    private String date;
    private String footballFieldId;
    private double footballFieldLatitude;
    private double footballFieldLongitude;
    private String footballFieldName;
    private String hour;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFootballFieldId() {
        return footballFieldId;
    }

    public void setFootballFieldId(String footballFieldId) {
        this.footballFieldId = footballFieldId;
    }

    public double getFootballFieldLatitude() {
        return footballFieldLatitude;
    }

    public void setFootballFieldLatitude(double footballFieldLatitude) {
        this.footballFieldLatitude = footballFieldLatitude;
    }

    public double getFootballFieldLongitude() {
        return footballFieldLongitude;
    }

    public void setFootballFieldLongitude(double footballFieldLongitude) {
        this.footballFieldLongitude = footballFieldLongitude;
    }

    public String getFootballFieldName() {
        return footballFieldName;
    }

    public void setFootballFieldName(String footballFieldName) {
        this.footballFieldName = footballFieldName;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}

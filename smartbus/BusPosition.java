package com.example.user.smartbus;

import java.io.Serializable;

public class BusPosition implements Serializable {
    public String latitude;
    public String longitude;

    public BusPosition(){}
    public BusPosition(String lat, String longitude)
    {
        latitude=lat;
        this.longitude=longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String combine()
    {
        return latitude+","+longitude;
    }
}

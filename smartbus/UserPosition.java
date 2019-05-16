package com.example.user.smartbus;

import android.os.Parcelable;

import java.io.Serializable;

public class UserPosition implements Serializable {
    public String latitude;
    public String longitude;

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

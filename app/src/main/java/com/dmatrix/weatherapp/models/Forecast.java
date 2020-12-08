package com.dmatrix.weatherapp.models;

import android.os.Parcel;
import android.os.Parcelable;


public class Forecast implements Parcelable {

    private Integer day;
    private Double minTemp;
    private Double maxTemp;
    private String description;
    private String icon;

    public Forecast(){

    }

    protected Forecast(Parcel in) {
        if (in.readByte() == 0) {
            day = null;
        } else {
            day = in.readInt();
        }
        if (in.readByte() == 0) {
            minTemp = null;
        } else {
            minTemp = in.readDouble();
        }
        if (in.readByte() == 0) {
            maxTemp = null;
        } else {
            maxTemp = in.readDouble();
        }

        description = in.readString();
        icon = in.readString();
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Double minTemp) {
        this.minTemp = minTemp;
    }

    public Double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Double maxTemp) {
        this.maxTemp = maxTemp;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (day == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(day);
        }
        if (minTemp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(minTemp);
        }
        if (maxTemp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(maxTemp);
        }
        parcel.writeString(description);
        parcel.writeString(icon);
    }
}

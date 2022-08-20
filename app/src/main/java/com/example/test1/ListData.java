package com.example.test1;

import com.google.firebase.firestore.GeoPoint;

public class ListData {
    String title, type, date, registUid, message, faciName = null;
    GeoPoint geoPoint;

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
    public void setTitle(String str) {
        title = str;
    }

    public void setType(String str) {
        type = str;
    }

    public void setDate(String str) {
        date = str;
    }

    public void setRegistUid(String str) {
        registUid = str;
    }
    public void setMessage(String str) {
        message = str;
    }

    public void setFaciName(String str) {
        faciName = str;
    }
//    public void setcurHeadcount(String str) {
//        curHeadcount = str;
//    }
//
//    public void setTotalHeadcount(String str) {
//        totalHeadcount = str;
//    }


    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return date;
    }

    public String getRegistUid() {return registUid;}

    public String getMessage() {return message;}

    public String getFaciName() {return faciName;}

//    public String getTotalHeadcount() {
//        return totalHeadcount;
//    }
//
//    public String getCurHeadcount() {
//        return curHeadcount;
//    }
}

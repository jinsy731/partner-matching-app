package com.example.test1;

import java.io.Serializable;

public class RegisterData implements Serializable {
    private String date, time, title, type, fName, uid;
    private int count, cur_count;

    //getter를 public으로 하는 것 매우 중요 (firebase에 데이터로 인스턴스를 사용할 때)
    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }

    public String getType() {return type;}

    public String getfName() {return fName;}

    public String getUid() {return uid;}

    public int getCur_count() {return cur_count;}

    void setDate(String date) {this.date = date;}
    void setTime(String time) {this.time = time;}
    void setTitle(String title) {this.title = title;}
    void setCount(int count) {this.count = count;}
    void setType(String type) { this.type = type;}
    void setfName(String fName) {this.fName = fName;}
    void setUid(String uid) {this.uid = uid;}
    void setCur_count(int count) {this.cur_count = count; }
}

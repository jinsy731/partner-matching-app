package com.example.test1;

import com.google.firebase.firestore.GeoPoint;

public class FacilityData {
    private String fName; // 시설 이름
    private String fTypeCd; // 시설 유형 코드
    private String fRoadAddr; //도로명 주소
    private String fCodNm; // 업종명
    private GeoPoint fgeoPoint; // 위치 정보
    private int nInboundPeople;


    //getter 메소드를 넣어줘야 인스턴스를 db의 add() 메소드의 인스턴스로서 사용할 수 있음.
    public String getfName() {
        return fName;
    }

    public String getfTypeCd() {
        return fTypeCd;
    }
    public int getnInboundPeople() {
        return nInboundPeople;
    }

    public String getfRoadAddr() {
        return fRoadAddr;
    }

    public String getfCodNm() {
        return fCodNm;
    }

    public GeoPoint getFgeoPoint() {
        return fgeoPoint;
    }

    public void setfName(String name) {
        fName = name;
    }

    public void setfTypeCd(String code) {
        fTypeCd = code;
    }

    public void setfRoadAddr(String addr) {
        fRoadAddr = addr;
    }

    public void setfCodNm(String name) {
        fCodNm = name;
    }

    public void setFgeoPoint(GeoPoint geoPoint) {
        fgeoPoint = geoPoint;
    }

    public void setnInboundPeople(int count) { nInboundPeople = count;}

}

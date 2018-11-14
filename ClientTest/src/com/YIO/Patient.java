package com.YIO;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.json.JSONObject;


public class Patient {
    private int id ;
    private int HR;
    private int pSYS;
    private int pDIA;
    private int YY ;
    private int MM;
    private int dd;
    private int HH;
    private int mm;
    //  private LocalDateTime dateTime;

    public Patient(int id) {
        this.id = id;
    }
    public Patient(JSONObject jsonObject){
        id = Integer.parseInt(jsonObject.get("id").toString());
        HR =Integer.parseInt( jsonObject.get("HR").toString());
        pSYS  =Integer.parseInt( jsonObject.get("pSYS").toString());
        pDIA = Integer.parseInt( jsonObject.get("pDIA").toString());
        YY = Integer.parseInt(jsonObject.get("YY").toString());
        MM = Integer.parseInt(jsonObject.get("MM").toString());
        dd = Integer.parseInt(jsonObject.get("dd").toString());
        HH = Integer.parseInt(jsonObject.get("HH").toString());
        mm = Integer.parseInt(jsonObject.get("mm").toString());
    }
    public Patient(int id, int HR, int pSYS, int pDIA, int YY, int MM, int dd, int HH, int mm) {
        this.id = id;
        this.HR = HR;
        this.pSYS = pSYS;
        this.pDIA = pDIA;
        this.YY = YY;
        this.MM = MM;
        this.dd = dd;
        this.HH = HH;
        this.mm = mm;
    }

    public void setDd(int dd) {
        this.dd = dd;
    }

    public void setHH(int HH) {
        this.HH = HH;
        this.HH =this.HH%24;
        this.dd+= (int)(this.HH/24);
    }

    public void setMinutes(int mm) {

        this.mm = mm%60;
        this.HH += (int)(mm/60);
        this.HH =this.HH%24;
        this.dd+= (int)(this.HH/24);
    }

    public void  makeMeasure(int HR, int pDIA, int pSYS){
        this.HR = HR;
        this.pSYS = pSYS;
        this.pDIA = pDIA;
        LocalDateTime dateTime = LocalDateTime.now();
        this.YY = dateTime.getYear();
        this.MM = dateTime.getMonthValue();
        this.dd = LocalDateTime.now().getDayOfMonth();

        this.HH = dateTime.getHour();
        this.mm = dateTime.getMinute();
    }
    public void  makeMeasure(int HR, int pDIA, int pSYS,int YY ,int MM,int dd,int HH,int mm){
        this.HR = HR;
        this.pSYS = pSYS;
        this.pDIA = pDIA;
        LocalDateTime dateTime = LocalDateTime.of(YY,MM,dd,HH,mm);

        this.YY = dateTime.getYear();
        this.MM = dateTime.getMonthValue();
        this.dd = dateTime.getDayOfMonth();
        this.HH = dateTime.getHour();
        this.mm = dateTime.getMinute();

    }
    public  LocalDateTime getDateAndTime(){
        return LocalDateTime.of(YY,MM,dd,HH,mm);
    }

    public int getId() {
        return id;
    }

    public int getHR() {
        return HR;
    }

    public int getpSYS() {
        return pSYS;
    }

    public int getpDIA() {
        return pDIA;
    }

    public String getDateTime() {
        return LocalDateTime.of(YY,MM,dd,HH,mm).toString();
    }

}

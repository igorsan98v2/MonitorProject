package sample;

import org.json.JSONObject;

import java.time.LocalDateTime;


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

    public void  makeMeasure(int HR, int pDIA, int pSYS){
        this.HR = HR;
        this.pSYS = pSYS;
        this.pDIA = pDIA;
        LocalDateTime dateTime = LocalDateTime.now();
        this.YY = dateTime.getYear();
        this.MM = dateTime.getMonthValue();
        this.dd = dateTime.getDayOfMonth();
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
    public void  makeMeasure(int HR, int pDIA, int pSYS,  LocalDateTime dateTime ){
        this.HR = HR;
        this.pSYS = pSYS;
        this.pDIA = pDIA;

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

package sample;

import java.util.Arrays;

public class ClassDetector {
    private int []SDH;
    public ClassDetector(int filtredSDH[]){
        SDH = filtredSDH;
    }
    public String[] detectClass(){
        String []Anserws = new String[2];
        String imageURL,result;
        Arrays.sort(SDH);
        int median =0;
        if(SDH.length%2==0){
            median = (SDH[SDH.length/2]+SDH[(SDH.length/2)+1])/2;
        }
        else median = SDH[Math.round(SDH.length/2.0f)];
        if(median<=-18){
            result ="здоровая";
            imageURL="/sample/fine.png";
        }
        else if(median<-10){
            result ="артериальная гипертензия";
            imageURL="/sample/ok.png";
        }
        else if(median<-3){
            result= "cочетаемые с другими   заболеваниями гестозы";
            imageURL="/sample/ok.png";
        }
        else if (median<3){
            result = "гестоз средней тяжести ";
            imageURL="/sample/hard.png";
        }
        else if(median<9){
            result = "гестоз, плохо поддающиеся лечению";
            imageURL="/sample/danger.png";
        }
        else{
            result = "упорный гестоз";
            imageURL="/sample/danger.png";
        }
        Anserws [0]= result;
        Anserws[1]=imageURL;
        return Anserws;
    }

}

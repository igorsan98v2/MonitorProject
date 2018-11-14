package sample;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MedianBuilder {
    private ArrayList<Patient> logs;
    private int range;
    private ArrayList<Integer>SDH;
    private int[] medianSDH;
    public MedianBuilder(ArrayList<Patient> logs){
            this.logs = logs;
            SDH = new ArrayList<Integer>();
            for (Patient log:logs) {
                SDH.add(log.getpDIA()-log.getHR());
            }

    }
    public int[] calcMedians(int range){
        this.range = range;
        System.out.println("SDH"+SDH.size());
        medianSDH = new int[SDH.size()-(2*range)];
        int m=0;
        for(int i=range;i<SDH.size()-range;i++,m++){
            int[] median = new int[(range*2)+1];
            median[0]=SDH.get(i);
           for(int j=1;j<range;j++){
               median[j]=(SDH.get(i-j));
               median[j+range]=(SDH.get(i+j));
           }

           medianSDH[m]=getMedian(median);

        }

        return medianSDH;
    }

    public ArrayList<Integer> getSDH() {
        return SDH;
    }

    private int getMedian(int[] median){
        Arrays.sort(median);
        int val =-1;
        if(median.length%2==0){
            val = (median[median.length/2]+median[(median.length/2)+1])/2;
        }
        else val = median[Math.round(median.length/2.0f)];
        return val;
    }

}

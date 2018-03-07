package sandoval.cis2237.com.timekepping_bysalvador;

import java.util.StringTokenizer;

/**
 * Created by ssandoval114 on 1/5/2017.
 */
public class TimeKeeping {

    private String date;
    private String inTime;
    private String outTime;
    private int totalHours;
    private int totalMinutes;
    private int id;

    TimeKeeping(int id, String inTime, String outTime, String date,int totalMinutes,int totalHours){

        this.id = id;
        this.inTime = inTime;
        this.outTime = outTime;
        this.date = date;
        this.totalHours = totalHours;
        this.totalMinutes = totalMinutes;

    }
    TimeKeeping(String date,String inTime,String outTime){

        this.date = date;
        this.outTime = outTime;
        this.inTime = inTime;

    }

    private void caculateTotalTime(String inTime,String outTime){

        int inHours;
        int inMin;
        int outHours;
        int outMin;
        int totalHour;
        int totalMin;

        StringTokenizer  stInTime = new StringTokenizer(inTime,":");
        StringTokenizer stOutTime = new StringTokenizer(outTime,":");

        String numHourIn = stInTime.nextToken();
        String numMinIn = stInTime.nextToken();

        String numHourOut = stOutTime.nextToken();
        String numMinOut = stOutTime.nextToken();

        inHours = Integer.parseInt(numHourIn);
        inMin = Integer.parseInt(numMinIn);
        outHours = Integer.parseInt(numHourOut);
        outMin = Integer.parseInt(numMinOut);

        totalHour  =  outHours - inHours;

       if (inHours == outHours){
           totalMin = outMin - inMin;
       }else{
           totalMin = (60 - inMin) + outMin;
       }

        if (totalMin >= 60){
            totalHour =+ 1;
            totalMin =+ totalMin - 60;
        }else if (totalMin == 60){
            totalHour =+ 1;
        }

        this.totalHours = totalHour;
        this.totalMinutes = totalMin;

    }

    public void setTime(String inTime,String outTime){

        this.inTime = inTime;
        this.outTime = outTime;

        caculateTotalTime(this.inTime,this.outTime);

    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public int getTotalHours (){
       if (this.totalMinutes == 0 && this.totalHours == 0){
           caculateTotalTime(this.inTime,this.outTime);
       }
        return totalHours;
    }

    public int getTotalMinutes (){
        if (this.totalMinutes == 0 && this.totalHours == 0){
            caculateTotalTime(this.inTime,this.outTime);
        }
        return totalMinutes;}

    public int getId() {
        return id;
    }
}

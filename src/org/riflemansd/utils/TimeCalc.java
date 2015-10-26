package org.riflemansd.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author rifleman
 */


public class TimeCalc {
    private long start;
    private long end;
    private long time;
    
    public TimeCalc() {}
    
    public void start() { start = System.currentTimeMillis(); }
    public void end() { 
        end = System.currentTimeMillis(); 
        time = calc();
    }
    private long calc() {
        return end - start;
    }
    
    public long getMillis() {
        return time;
    }
    public double getSeconds() {
        return time/1000;
    }
    public double getMinutes() {
        return getSeconds()/60;
    }
    public double getHours() {
        return getMinutes()/60;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date resultdate = new Date(this.getMillis());
        return date_format.format(resultdate);
    }
}

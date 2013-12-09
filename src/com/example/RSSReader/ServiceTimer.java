package com.example.RSSReader;

import android.util.Log;

import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 17.11.13
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTimer extends TimerTask {
    public static final String ServiceTimerTuskLOG="Service_Timer_Tusk_LOG";
    String pubDate="";

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }


    @Override
    public void run() {
        Log.d(ServiceTimerTuskLOG,"INSIDE CLASS ServiceTimer");
        Log.d(ServiceTimerTuskLOG,"INSIDE CLASS ServiceTimer, try print pubDate: "+getPubDate().toString());
    }
}

package com.example.RSSReader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class NotifService extends Service implements InterfaceForNotification {
    public static final String log = "createLog";
    private static final String PREFS_NAME = "MySharedPreference";
    Timer timer = new Timer();
    NotificationManager mNotifyMgr;
    String chekingPubDate = "";
    URL url = null;
    ArrayList<RssTags> arrayList = null;
    private String pubDateFromAsyncTask = "";

    public String getPubDateFromAsyncTask() {
        return pubDateFromAsyncTask;
    }

    public void setPubDateFromAsyncTask(String pubDateFromAsyncTask) {
        this.pubDateFromAsyncTask = pubDateFromAsyncTask;
    }


    public NotificationManager getmNotifyMgr() {
        return mNotifyMgr;
    }

    public void setmNotifyMgr(NotificationManager mNotifyMgr) {
        this.mNotifyMgr = mNotifyMgr;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<RssTags> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<RssTags> arrayList) {
        this.arrayList = arrayList;
    }

    public String getChekingPubDate() {
        return chekingPubDate;
    }

    public void setChekingPubDate(String chekingPubDate) {
        this.chekingPubDate = chekingPubDate;
    }

    public class LocalBinder extends Binder {
        NotifService getService() {
            return NotifService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public void updateNotification(String pubDate) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_av_add_to_queue)
                        .setContentTitle("RSS Reader updated.")
                        .setContentText("Last update date: " + pubDate);

        // Create intent which will start main activity when notification is clicked
        Intent resultIntent = new Intent(this, MyActivity.class);

        // Create back stack for intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MyActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        // Issue a notification in the notification bar
        mNotifyMgr.notify(001, builder.build());

        SharedPreferences updateSP = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = updateSP.edit();
        editor.putString("lastPubDate", pubDate);
        editor.commit();
    }

    public void onCreate() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_av_add_to_queue)
                .setContentTitle("Service RSS Reader")
                .setContentText("is started(works)");

        Intent resultIntent = new Intent(this, MyActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MyActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntentBuilder = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        SharedPreferences checking = getSharedPreferences(PREFS_NAME, 0);
        String checkPubDate = checking.getString("lastPubDate", "Error(Empty String)");
        setChekingPubDate(checkPubDate);
        String url1 = checking.getString("url", "Error(Empty url)");
        setUrl(url1);
        Log.d(log, "INSIDE CLASS NotifService, onCreate(), try to see on received SharedPreferences: " + checkPubDate);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(log, "INSIDE  ServiceTimer, try print pubDate from SP: " + getChekingPubDate()
                        + "\n and url from SP: " + getUrl().toString());

                try {
                    setArrayList(new ParseXml().execute(getUrl()).get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                setPubDateFromAsyncTask(getArrayList().get(0).getPubDate());

                Log.d(log, "INSIDE  ServiceTimer, print PubDateFromAsyncTask " + getPubDateFromAsyncTask());
                if (!getPubDateFromAsyncTask().equals(getChekingPubDate())) {
                    Log.d(log, "INSIDE CLASS ServiceTimer, call updateNotification() RSS IS UPDATED !_!_!_!_!_!_!_!_! ");
                    updateNotification(getPubDateFromAsyncTask());
                } else {
                    Log.d(log, "INSIDE ServiceTimer, Nothing changed \n\n ");
                }
            }
        }, 50000, 1800000);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        mNotifyMgr.cancel(001);
        timer.cancel();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        return Service.START_STICKY;
    }
}

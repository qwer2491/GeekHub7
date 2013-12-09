package com.example.RSSReader;

import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 20.11.13
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class InApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, NotifService.class));
    }
}

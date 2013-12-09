package com.example.RSSReader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 03.12.13
 * Time: 1:19
 * To change this template use File | Settings | File Templates.
 */
public class FavoriteShowElements extends ActionBarActivity {
    public static CharSequence mainTitle = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_elements_display_activity_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra("sendElement");
        TextView textView = (TextView) findViewById(R.id.FEtextView);
        textView.setText(formatInfo(info));
        if (getResources().getBoolean(R.bool.istablet)) {
            textView.setTextSize(30);
        }
        setTitle(mainTitle);
    }
    public String formatInfo(String[] strArray) {
        String result = "";
        for (int i = 0; i < strArray.length - 1; i++) {
            result = result + strArray[i] + "\n";
        }
        mainTitle = strArray[strArray.length - 1];
        return result;
    }

}

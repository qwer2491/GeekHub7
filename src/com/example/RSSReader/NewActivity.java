package com.example.RSSReader;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 10.11.13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class NewActivity extends ActionBarActivity {
    final String log = "createLog";
    public static CharSequence mainTitle = "";
    private MenuItem StatusItem;
    RssTags rssTags;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra("sendElement");
        rssTags = new RssTags(info[3], info[1], info[0], info[2]);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(formatInfo(info));
        if (getResources().getBoolean(R.bool.istablet)) {
            textView.setTextSize(30);
        }
        setTitle(mainTitle);
    }

    void addElement(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnnouncementOfDatabase mDbContract = new AnnouncementOfDatabase();
                AnnouncementOfDatabase.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_TITLE, rssTags.getTitle());
                values.put(AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_DESCRIPTION, rssTags.getDescription());
                values.put(AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_LINK, rssTags.getLink());
                values.put(AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_PUBDATE, rssTags.getPubDate());


                long newRowId;
                newRowId = db.insert(
                        AnnouncementOfDatabase.FeedEntry.TABLE_NAME,
                        null,
                        values);
                Log.d(log, "INSIDE CLASS NewActivity, addElement(), try to see DB result : " + newRowId);
                db.close();
            }
        });

    }

    void deleteElement(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AnnouncementOfDatabase mDbContract = new AnnouncementOfDatabase();
                AnnouncementOfDatabase.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                long newRowId;
                newRowId = db.delete(
                        AnnouncementOfDatabase.FeedEntry.TABLE_NAME,
                        AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_TITLE + " = ?",
                        new String[]{rssTags.getTitle()});
                Log.d(log, "INSIDE CLASS NewActivity, deleteElement(), try to see DB result : " + newRowId);
                db.close();
            }
        });

    }

    boolean checkElement(final Context context, final String checkTitle){
        final boolean[] exists = new boolean[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                   exists[0] =false;
                AnnouncementOfDatabase mDbContract = new AnnouncementOfDatabase();
                AnnouncementOfDatabase.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] projection = {
                        AnnouncementOfDatabase.FeedEntry._ID,
                        AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_TITLE,
                        AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_DESCRIPTION,
                        AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_LINK,
                        AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_PUBDATE
                };
                String selection= AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_TITLE+"=?";
                String selectionArgs[] = {rssTags.getTitle()};
                Log.d(log,"INSIDE DISPLAY ACTIVITY checkElement(), try to see selectionArgs[]  in db = "+selectionArgs[0]);
                Cursor cursor = db.query(
                        AnnouncementOfDatabase.FeedEntry.TABLE_NAME, // The table to query
                        projection, // The columns to return
                        selection, //selection , // The columns for the WHERE clause
                        selectionArgs, //selectionArgs , // The values for the WHERE clause
                        null, // don't group the rows
                        null, // don't filter by row groups
                        null // The sort order
                );
                if (cursor.moveToFirst ()){
                    do{
                        if(checkTitle.equals(cursor.getString(cursor.getColumnIndex(AnnouncementOfDatabase.FeedEntry.COLUMN_NAME_TITLE)))){
                            exists[0] =true;
                        }
                    }   while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
            }
        });
        return exists[0];
    }

    public String formatInfo(String[] strArray) {
        String result = "";
        for (int i = 0; i < strArray.length - 1; i++) {
            result = result + strArray[i] + "\n";
        }
        mainTitle = strArray[strArray.length - 1];
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_activity_menu, menu);
        StatusItem = menu.findItem(R.id.item2);
        if(checkElement(getApplicationContext(), rssTags.getTitle())){
            StatusItem.setIcon(R.drawable.ic_action_rating_bad);
            StatusItem.setTitle(R.string.item22);
        }  else {
            StatusItem.setIcon(R.drawable.ic_action_rating_good);
            StatusItem.setTitle(R.string.item2);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item2:
                if (StatusItem.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_rating_good).getConstantState())) {
                    Toast.makeText(this, "You click LIKE THIS NEWS in ActionBar", Toast.LENGTH_SHORT).show();
                    addElement(getApplicationContext());
                    StatusItem.setIcon(R.drawable.ic_action_rating_bad);
                    StatusItem.setTitle(R.string.item22);
                    break;
                } else {
                    Toast.makeText(this, "You click DISLIKE THIS NEWS in ActionBar", Toast.LENGTH_SHORT).show();
                    deleteElement(getApplicationContext());
                    StatusItem.setIcon(R.drawable.ic_action_rating_good);
                    StatusItem.setTitle(R.string.item2);
                    break;
                }
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }
}

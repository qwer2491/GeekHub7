package com.example.RSSReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 03.12.13
 * Time: 21:03
 * To change this template use File | Settings | File Templates.
 */
public final class AnnouncementOfDatabase {
    public AnnouncementOfDatabase() {
    }

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "liked_entries";
        //public static final String COLUMN_NAME_ENTRY_ID = "entry_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PUBDATE = "pub_date";
        public static final String COLUMN_NAME_LINK = "link";
        public static final String COLUMN_NAME_LIKE = "like";
        //public static final String COLUMN_NAME_UPDATED = "updated";

    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY autoincrement," +
                    //FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LINK + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_PUBDATE + TEXT_TYPE +    COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LIKE + " INTEGER " +

                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }



}

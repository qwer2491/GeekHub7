package com.example.RSSReader;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 29.11.13
 * Time: 19:53
 * To change this template use File | Settings | File Templates.
 */
public class Provider extends ContentProvider {
    static final String log = "createLog";
    //    public static final int DATABASE_VERSION = 1;
//    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String AUTHORITY = "com.example.RSSReader"; //.my_database_provider
    public static final Uri MAIN_URI = Uri.parse("content://" + AUTHORITY + "/" + FeedEntry.TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + FeedEntry.TABLE_NAME;

    // одна строка
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + FeedEntry.TABLE_NAME;

    //// UriMatcher
    // общий Uri
    static final int URI_CONTACTS = 1;

    // Uri с указанным ID
    static final int URI_CONTACTS_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, FeedEntry.TABLE_NAME, URI_CONTACTS);
        uriMatcher.addURI(AUTHORITY, FeedEntry.TABLE_NAME + "/#", URI_CONTACTS_ID);
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
                    FeedEntry.COLUMN_NAME_PUBDATE + TEXT_TYPE + COMMA_SEP +
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


    private FeedReaderDbHelper feedReaderDbHelper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Log.d(log, "class Provider onCreate()");
        feedReaderDbHelper = new FeedReaderDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(log, "class Provider query(), " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                Log.d(log, "class Provider query(), URI_CONTACTS");
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                Log.d(log, " URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = FeedEntry._ID + " = " + id;
                } else {
                    selection = selection + " AND " + FeedEntry._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = feedReaderDbHelper.getWritableDatabase();
        Cursor cursor = db.query(FeedEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), MAIN_URI);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(log, "class Provider getType(), " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                return CONTENT_TYPE;
            case URI_CONTACTS_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(log, "class Provider insert(), " + uri.toString());
        if (uriMatcher.match(uri) != URI_CONTACTS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = feedReaderDbHelper.getWritableDatabase();
        long rowID = db.insert(FeedEntry.TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(MAIN_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(log, "class Provider delete(), " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_CONTACTS:
                Log.d(log, " URI_CONTACTS");
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                Log.d(log, " URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = FeedEntry._ID + " = " + id;
                } else {
                    selection = selection + " AND " + FeedEntry._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = feedReaderDbHelper.getWritableDatabase();
        int cnt = db.delete(FeedEntry.TABLE_NAME,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(log, "class Provider update(), " + uri.toString());
        switch (uriMatcher.match(uri)){
            case URI_CONTACTS:
                Log.d(log, "URI_CONTACTS" );
                break;
            case URI_CONTACTS_ID:
                String id = uri.getLastPathSegment();
                Log.d(log, " URI_CONTACTS_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = FeedEntry._ID + " = " + id;
                } else {
                    selection = selection + " AND " + FeedEntry._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db=feedReaderDbHelper.getWritableDatabase();
        int cnt = db.update(FeedEntry.TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return cnt;
    }
}

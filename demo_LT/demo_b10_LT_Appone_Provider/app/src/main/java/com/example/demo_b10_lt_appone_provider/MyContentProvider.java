package com.example.demo_b10_lt_appone_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {

    //=====================  1
    static final String AUTHOURITY = "com.example.demo_b10_lt_appone_provider";
    static final String CONTENT_PROVIDER = "contentprovider";
    static final String URL = "content://" + AUTHOURITY + "/" + CONTENT_PROVIDER;
    static final Uri CONTENT_URI = Uri.parse(URL);
    static final String PRODUCT_TABLE = "Products";

    private SQLiteDatabase db;

    static final int ONE = 1; // tìm có điều kiện....
    static final int ALL = 2;
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHOURITY, PRODUCT_TABLE, ONE);
        uriMatcher.addURI(AUTHOURITY, PRODUCT_TABLE + "/#", ALL);
    }

    private static HashMap<String, String> PROJECTION_MAP;

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int res =0;
        res = db.delete(PRODUCT_TABLE, selection, null);
        return res;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //=====================  3
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long number_row = db.insert(PRODUCT_TABLE, "", values);
        if (number_row > 0) // > 0  khi truyen vaof thanh cong
        {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, number_row);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    //=====================  2
    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.

        Context context = getContext();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db == null) {
            return false;
        }
        return true;
    }

    //=====================  4
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PRODUCT_TABLE);
        switch (uriMatcher.match(uri)) {
            case ALL: {
                queryBuilder.setProjectionMap(PROJECTION_MAP);
                break;
            }
            case ONE: {
                queryBuilder.appendWhere("id=" + uri.getPathSegments().get(0));
            }
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.

        int a = db.update(PRODUCT_TABLE, values,selection, null );
        if (a>0)
        {
            return  a;
        }
        return 0;
    }
}
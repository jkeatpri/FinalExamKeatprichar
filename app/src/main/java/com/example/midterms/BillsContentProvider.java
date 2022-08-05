package com.example.midterms;

import static com.example.midterms.Bill.KEY_ID;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// TODO implement methods of ContentProvider
//  Tip: Copy and paste the NotesContentProvider here and change "notes" to "bills", etc

public class BillsContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://com.example.billskeatprichar.billsprovider/bills");

    public static final int ALL_ROWS = 1;
    public static final int SINGLE_ROW = 2;

    private BillsOpenHelper helper;
    public static final UriMatcher matcher;

    static{
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI("com.example.billskeatprichar.billsprovider", "bills", ALL_ROWS);
        matcher.addURI("com.example.billskeatprichar.billsprovider", "bills/#", SINGLE_ROW);
    }
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

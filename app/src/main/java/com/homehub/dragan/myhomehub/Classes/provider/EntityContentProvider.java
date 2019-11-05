package com.homehub.dragan.myhomehub.Classes.provider;

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
import android.support.annotation.NonNull;
import android.util.Log;

import com.homehub.dragan.myhomehub.BuildConfig;
import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.Widget;

import java.util.ArrayList;

public class EntityContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.homehub.dragan.myhomehub.Classes.provider.EntityContentProvider";
    private static final String TABLE_NAME = "entities";

    private DatabaseManager mSqliteOpenHelper;
    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "entities", 1);
        sUriMatcher.addURI(AUTHORITY, "dashboard", 2);
    }

    public static Uri getUrl() {
        return Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        //return Uri.parse("content://com.homehub.dragan.myhomehub.Classes.provider.EntityContentProvider/");
    }

    @Override
    public boolean onCreate() {
        mSqliteOpenHelper = DatabaseManager.getInstance(getContext());
        return true;
    }

    @Override
    synchronized public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d("YouQi", "query uri: " + uri.toString());

        SQLiteDatabase db = mSqliteOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        Cursor c = null;
        String offset;

        Log.d("YouQi", "query URI Matcher: " + sUriMatcher.match(uri));

        String query = "SELECT *, -1 AS DISPLAY_ORDER FROM entities";
        c = db.rawQuery(query, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return BuildConfig.APPLICATION_ID + ".item";
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table = "";

        long result = mSqliteOpenHelper.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        if (result == -1) {
            throw new SQLException("insert with conflict!");
        }

        Uri retUri = ContentUris.withAppendedId(uri, result);
        return retUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = mSqliteOpenHelper.getWritableDatabase();
        db.beginTransaction();

        int inserted = 0;

        try {
            db.delete(TABLE_NAME, null, null);

            for (ContentValues value : values) {
                db.insert(TABLE_NAME, null, value);
                ++inserted;
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            //db.close();
        }
        //getContext().getContentResolver().notifyChange(Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME + "/" + "input_slider.harmonyvolume"), null);
        Log.d("YouQi", "Inserted: " + inserted);
        //getContext().getContentResolver().notifyChange(getUrl(), null);

        Uri callbackUri = DummyContentProvider.getUrl("ALL");
        Context context = getContext();
        if (context != null) context.getContentResolver().notifyChange(callbackUri, null);

        return inserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return -1;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        Log.d("YouQi", "update uri: " + uri.toString());
        //Log.d("YouQi", "selection: " + selection);

        SQLiteDatabase db = mSqliteOpenHelper.getWritableDatabase();
        db.update(TABLE_NAME, values, selection, selectionArgs);
        //db.close();

        //Update if there are widgets found.
        final String entityId = values.getAsString("ENTITY_ID");
        Entity entity = mSqliteOpenHelper.getEntityById(entityId);
        ArrayList<Integer> widgetIds = mSqliteOpenHelper.getWidgetIdsByEntityId(entityId);
        for (int widgetId : widgetIds) {
            Log.d("YouQi", "Updating Widget: " + widgetId);
            Widget widget = Widget.getInstance(entity, widgetId);
        }

        Uri newUri = DummyContentProvider.getUrl(entityId);

        getContext().getContentResolver().notifyChange(newUri, null);

        return -1;
    }
}
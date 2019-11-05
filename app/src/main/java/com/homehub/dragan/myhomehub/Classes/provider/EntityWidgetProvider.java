package com.homehub.dragan.myhomehub.Classes.provider;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.homehub.dragan.myhomehub.Activities.AppController;
import com.homehub.dragan.myhomehub.Activities.BackgroundActivity;
import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.HomeHubServer;
import com.homehub.dragan.myhomehub.Classes.model.Widget;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.homehub.dragan.myhomehub.Activities.EditActivity;
//import com.payano.homeassistant.TransparentActivity;
//import com.homehub.dragan.myhomehub.Classes.util.FaultUtil;

public class EntityWidgetProvider extends AppWidgetProvider {

    private static void setTextSizeForWidth(Paint paint, float desiredWidth, String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }

    public static void updateEntityWidget(Context context, Widget widget) {
        Log.d("YouQi", "Widget updateEntityWidget: " + CommonUtil.deflate(widget));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        String iconText = widget.getMdiIcon(); //MDIFont.getIcon("mdi:weather-hail");
        //int iconColor = ResourcesCompat.getColor(context.getResources(), (widget.isToggleable() && !widget.isCurrentStateActive()) ? R.color.md_grey_500 : R.color.xiaomiPrimaryTextSelected, null);

        Bitmap myBitmap = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888);
        myBitmap.eraseColor(Color.TRANSPARENT);

        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setTextSize(160);
        //paint.setStrokeWidth(24); // Text Size
        //setTextSizeForWidth(paint, 48, iconText);
        //paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern

        Canvas canvas = new Canvas(myBitmap);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
        int xPos = (canvas.getWidth() - yPos) / 2;//(canvas.getWidth() / 2);
        canvas.drawText(iconText, 0, yPos, paint);



        //https://stackoverflow.com/questions/21311917/onreceive-will-always-receive-the-last-appwidgetid-even-different-instance-widg
        Intent newIntent = new Intent(context, EntityWidgetProvider.class);
        newIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        newIntent.putExtra("appWidgetId", widget.appWidgetId);
        newIntent.putExtra("widget", CommonUtil.deflate(widget));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widget.appWidgetId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("YouQi", "appWidgetManager updating (" + widget.appWidgetId + "): " + widget.getFriendlyState());
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        //final int count = appWidgetIds.length;
        Log.d("YouQi", "Widget onUpdate");
        final DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        final SharedPreferences mSharedPref = ((AppController) context.getApplicationContext()).getSharedPref();
        final ArrayList<HomeHubServer> mServers = databaseManager.getConnections();
        final HomeHubServer mCurrentServer = mServers.get(mSharedPref.getInt("connectionIndex", 0));

        for (final int appWidgetId : appWidgetIds) {
            Log.d("YouQi", "Widget onUpdate appWidgetId: " + appWidgetId);
            final Widget widget = databaseManager.getWidgetById(appWidgetId);
            if (widget != null) {
                //updateEntityWidget(context, widget);

                if (mCurrentServer != null) {
                    Call<Entity> mCall = ServiceProvider.getApiService(mCurrentServer.getBaseUrl()).getState(mCurrentServer.getBearerHeader(), widget.entityId);
                    mCall.enqueue(new Callback<Entity>() {
                        @Override
                        public void onResponse(@NonNull Call<Entity> call, @NonNull Response<Entity> response) {

                            Entity restResponse = response.body();
                            if (restResponse != null) {
                                context.getContentResolver().update(Uri.parse("content://com.payano.homeassistant.provider.EntityContentProvider/"), restResponse.getContentValues(), "ENTITY_ID='" + restResponse.entityId + "'", null);
                                Widget newWidget = databaseManager.getWidgetById(appWidgetId);
                                updateEntityWidget(context, newWidget);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Entity> call, @NonNull Throwable t) {
                            updateEntityWidget(context, widget);
                        }
                    });
                } else {
                    updateEntityWidget(context, widget);
                }

            } else {
                Log.d("YouQi", "shit happend!");
            }
        }


    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);
        Log.d("YouQi", "Widget onReceive");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("YouQi", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        String rawWidget = intent.getStringExtra("widget");
        if (rawWidget == null) return;
        Log.d("YouQi", "Widget after rawWidget");
        final Widget widget = CommonUtil.inflate(rawWidget, Widget.class);
        final DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        //final SharedPreferences mSharedPref = ((AppController) context.getApplicationContext()).getSharedPref();
        //final ArrayList<HomeAssistantServer> mServers = databaseManager.getConnections();
        //final HomeAssistantServer mCurrentServer = mServers.get(mSharedPref.getInt("connectionIndex", 0));
        //final int appWidgetId =widget.appWidgetId;

        Entity dbEntity = databaseManager.getEntityById(widget.entityId);
        if (dbEntity == null) return;
        Log.d("YouQi", "Widget after dbEntity");

        {
            Intent newIntent = new Intent(context, BackgroundActivity.class);
            newIntent.putExtra("appWidgetId", widget.appWidgetId);
            newIntent.putExtra("entityId", widget.entityId);
            newIntent.putExtra("entity", CommonUtil.deflate(dbEntity));
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(newIntent);
        }

        //updateEntityWidget(context, widget);


//        Widget newWidget = Widget.getInstance(dbEntity, widget.appWidgetId);
//        updateEntityWidget(context, newWidget);

//        if (intent.getStringExtra("state") != null && !dbEntity.state.equals(intent.getStringExtra("state"))) {
//            Intent newIntent = new Intent(context, UpdateWidgetService.class);
//            newIntent.putExtra("appWidgetId", appWidgetId);
//            newIntent.putExtra("entityId", entityId);
//            newIntent.putExtra("state", dbEntity.getFriendlyState());
//            newIntent.putExtra("name", dbEntity.getFriendlyName());
//            context.startService(newIntent);
//        }
    }
}
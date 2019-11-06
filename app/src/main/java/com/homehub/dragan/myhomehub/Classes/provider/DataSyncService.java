package com.homehub.dragan.myhomehub.Classes.provider;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.EventResponse;
import com.homehub.dragan.myhomehub.Classes.model.HomeHubServer;
import com.homehub.dragan.myhomehub.Classes.model.rest.CallServiceRequest;
import com.homehub.dragan.myhomehub.Classes.model.rest.RxPayload;
import com.homehub.dragan.myhomehub.Classes.shared.DataSyncInterface;
import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;

import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class DataSyncService extends Service {
    private static final int NOTIFICATION_ID = 6882;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private int nextNum = 100;
    private Subject<RxPayload> mEventEmitter = PublishSubject.create();
    private WebSocket mWebSocket;
    private static DataSyncService mInstance;
    private String mPassword;
    private String mBaseUrl;
    private HashMap<String, Entity> mEntities = new HashMap<>();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public DataSyncService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataSyncService.this;
        }

        public Subject<RxPayload> getEventSubject() {
            return mEventEmitter;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        output("binding");
        //startWebSocket();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        output("unbinding");
        stopWebSocket();
        return super.onUnbind(intent);
    }

    public boolean isWebSocketRunning() {
        return mWebSocket != null;
    }

    public void startWebSocket(HomeHubServer server) {
        startWebSocket(server, false);
    }

    public void startWebSocket(HomeHubServer server, boolean isSilence) {

        mPassword = server.getPassword();
        mBaseUrl = server.getWebsocketUrl();
        output("mBaseUrl: " + mBaseUrl);
        if (mWebSocket == null) {
            output("startWebSocket: " + mBaseUrl);
            Request request = new Request.Builder().url(mBaseUrl).build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            OkHttpClient client = ServiceProvider.getWebSocketOkHttpClientInstance();
            mWebSocket = client.newWebSocket(request, listener);
        } else {
            if (!isSilence) {
                Toast.makeText(getApplicationContext(), "websocket running", Toast.LENGTH_SHORT).show();
            }
        }
        //client.dispatcher().executorService().shutdown();
    }



    public void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancel(NOTIFICATION_ID);

    }

    public void stopWebSocket() {
        if (mWebSocket != null) mWebSocket.cancel();
        clearNotification();
        mWebSocket = null;
    }

    /**
     * method for clients
     */
    @SuppressLint("StaticFieldLeak")
    public void getRandomNumber(final DataSyncInterface dataSyncInterface) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dataSyncInterface.onResponse(++nextNum);
            }
        }.execute();
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public void onOpen(WebSocket webSocket, Response response) {
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
            processResponse(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
            stopWebSocket();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
            stopWebSocket();
        }
    }

    private void output(String s) {
        Log.d("Websocket", s);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        output("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        output("onDestroy");
        if (mWebSocket != null) mWebSocket.cancel();
        mEventEmitter.onComplete();
    }

    public void sendCommand(String command) {
        Log.d("YouQi", "command: " + command);
        mWebSocket.send(command);
    }

    public void callService(final String domain, final String service, CallServiceRequest serviceRequest) {
        try {
            String command = new JSONObject()
                    .put("type", "call_service")
                    .put("domain", domain)
                    .put("service", service)
                    .put("service_data", new JSONObject(CommonUtil.deflate(serviceRequest)))
                    .put("id", ++nextNum)
                    .toString();
            sendCommand(command);
            Log.d("Yo","sent change");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processResponse(String s) {
        //CommonUtil.logLargeString("YouQi", "RECEIVEDSTRING !" + s);
        try {
            final JSONObject response = new JSONObject(s);
            final String type = response.getString("type");
            //CommonUtil.logLargeString("YouQi", "type: " + type);

            switch (type) {
                case "auth_required":
                    sendCommand(new JSONObject()
                            .put("type", "auth")
                            .put("access_token", mPassword)
                            .toString());
                    break;
                case "auth_ok":
                    sendCommand(new JSONObject()
                            .put("id", HomeHubWebSocket.getNextIdentifier())
                            .put("type", "subscribe_events")
                            .put("event_type", "state_changed")
                            .toString());

                    sendCommand(new JSONObject()
                            .put("id", HomeHubWebSocket.getNextIdentifier())
                            .put("type", "get_states")
                            .toString());
                    break;
                case "event":
                    EventResponse eventResponse = CommonUtil.inflate(s, EventResponse.class);
                    if (eventResponse != null) {

                        final Entity newEntity = eventResponse.event.data.newState;
                        final String entityId = newEntity.entityId;
                        final Entity oldEntity = mEntities.get(entityId);

                        if (oldEntity == null || !oldEntity.lastChanged.equals(newEntity.lastChanged)) {
                            RxPayload payload = RxPayload.getInstance("UPDATE");
                            payload.entity = newEntity;
                            mEventEmitter.onNext(payload);


                            getContentResolver().update(Uri.parse("content://com.homehub.dragan.myhomehub.Classes.provider.EntityContentProvider/"), newEntity.getContentValues(), "ENTITY_ID='" + newEntity.entityId + "'", null);
                            Log.d("Yo","recived change");
                        }
                        mEntities.put(entityId, eventResponse.event.data.newState);
                    }
                    break;
                default:
                    CommonUtil.logLargeString("YouQi", "Not Mapped: " + s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class TerminateConnectionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("YouQi", "Received Cancelled Event");
            if (mInstance != null) mInstance.stopWebSocket();
        }
    }
}
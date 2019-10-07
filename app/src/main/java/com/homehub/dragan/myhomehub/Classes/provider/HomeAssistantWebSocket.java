package com.homehub.dragan.myhomehub.Classes.provider;

import android.util.Log;

import com.homehub.dragan.myhomehub.Classes.util.CommonUtil;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

public class HomeAssistantWebSocket {
    public static int identifier = 1;

    public static int getNextIdentifier() {
        return ++identifier;
    }

    public static Future<WebSocket> getInstance() {
        final String uri = "ws://192.168.2.50:8123/api/websocket";
        return AsyncHttpClient.getDefaultInstance().websocket(uri, null, new AsyncHttpClient.WebSocketConnectCallback() {

            @Override
            public void onCompleted(Exception ex, final WebSocket webSocket) {

                if (ex != null) {
                    Log.d("Yo", ex.toString());
                    ex.printStackTrace();
                    throw new RuntimeException("Conn Failed" + ex.getMessage());
                }

                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        CommonUtil.logLargeString("Yo", "RECEIVED BYTE!");
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }


}
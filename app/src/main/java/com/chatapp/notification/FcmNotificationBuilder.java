package com.chatapp.notification;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FcmNotificationBuilder {

    private static final String SERVER_API_KEY = "AIzaSyDI6rVMtr0YIK-fXaMlt8DIBluyR1ee0BM";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_KEY = "key=" + SERVER_API_KEY;
    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String KEY_TO = "to";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATA = "data";
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");



    private FcmNotificationBuilder() {

    }
    public static FcmNotificationBuilder initialize() {
        return new FcmNotificationBuilder();
    }



    public void send() {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, getValidJsonBody().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(AUTHORIZATION, AUTH_KEY)
                .url(FCM_URL)
                .post(requestBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private JSONObject getValidJsonBody() throws JSONException {
        JSONObject jsonObjectBody = new JSONObject();
        jsonObjectBody.put(KEY_TO, "/topics/chat");    //send to chat topic
        JSONObject jsonObjectData = new JSONObject();
        jsonObjectData.put(KEY_TITLE, "New Message");  //Dummy text for notification
        jsonObjectBody.put(KEY_DATA, jsonObjectData);

        return jsonObjectBody;
    }
}

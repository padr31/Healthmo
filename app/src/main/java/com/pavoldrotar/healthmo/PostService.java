package com.pavoldrotar.healthmo;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class PostService extends IntentService {

    public PostService() {
        super("PostService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (intent.getStringExtra("event")) {
            case "bpm":
                makeBpm(intent);
                break;
            case "CALL_EMERGENCY":
                makeEmergency(intent);
                break;
            default:
                makeEvent(intent);
                break;
        }
    }

    private void makeEmergency(Intent intent) {
        String urlString = "https://stately-turbine-223513.appspot.com/callEmergency";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        try {
            JSONObject params = new JSONObject();
            params.put("from_phone_number", intent.getStringExtra("from_phone_number"));
            params.put("to_phone_number", intent.getStringExtra("to_phone_number"));
            params.put("message", "This is a request on behalf of John. He doesn't feel good and requested your help, likely related to heart issues. He's located at Dipoli, Espoo, at Junction Demo Area. Call back at 3599764.");
            params.put("send_sms", 1);

            StringEntity entity = new StringEntity(params.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post(urlString, client, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeEvent(Intent intent) {
        String urlString = "https://stately-turbine-223513.appspot.com/userEvent";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", 2);
            params.put("event", intent.getStringExtra("event"));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("timestamp", df.format(new Date()));
            StringEntity entity = new StringEntity(params.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post(urlString, client, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeBpm(@NonNull Intent intent) {
        String urlString = "https://stately-turbine-223513.appspot.com/bpm";
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type", "application/json");
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", 2);
            params.put("bpm", intent.getIntExtra("bpm", 0));
            params.put("email", "test@example.com");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params.put("timestamp", df.format(new Date()));
            params.put("aggregated_for", "1 minutes");
            StringEntity entity = new StringEntity(params.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post(urlString, client, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void post(String urlString, AsyncHttpClient client, StringEntity entity) {
        client.post(getApplicationContext(), urlString, entity, "Application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}

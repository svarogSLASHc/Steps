package com.test.pedometer.data.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private final Context context;
    private static final String BASE_URL = "http://10.113.212.22:5000";
    private static final String UPLOAD_URL = "/upload";

    private NetworkManager(Context context) {
        this.context = context;
    }

    public static NetworkManager newInstance(Context context) {
        return new NetworkManager(context);
    }

    public JsonObjectRequest uploadResults(String results, Response.Listener<JSONObject> resultListener, Response.ErrorListener errorListener) {
        Map<String, String> params = new HashMap<>();
        params.put("text", results);

        JSONObject jsonParams = new JSONObject(params);

        return new JsonObjectRequest(Request.Method.POST,
                BASE_URL + UPLOAD_URL, jsonParams,
                resultListener,
                errorListener);
    }
}

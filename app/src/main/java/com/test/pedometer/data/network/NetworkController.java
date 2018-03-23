package com.test.pedometer.data.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import rx.Observable;

public class NetworkController {
    private static NetworkController INSTANCE;
    private RequestQueue requestQueue;
    private final Context context;
    private final NetworkManager networkManager;

    private NetworkController(Context context) {
        this.context = context;
        networkManager = NetworkManager.newInstance(context);
    }

    public static synchronized NetworkController getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NetworkController(context);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public Observable<String> uploadResults(String results) {
        return Observable.create(subscriber ->
                addToRequestQueue(networkManager.uploadResults(results,
                        response -> {
                            subscriber.onNext(response.toString());
                            subscriber.onCompleted();
                        },
                        subscriber::onError))
        );
    }

}

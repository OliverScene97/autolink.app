package com.mecha.app.network;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class ApiService {
    private static ApiService instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ApiService(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public void saveProfile(JSONObject profileData, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = "http://192.168.195.61:3000/api/profile/save";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileData, listener, errorListener);
        getRequestQueue().add(jsonObjectRequest);
    }

    public void getProfile(String userId, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = "http://192.168.195.61:3000/api/profile/" + userId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, listener, errorListener);
        getRequestQueue().add(jsonObjectRequest);
    }
}

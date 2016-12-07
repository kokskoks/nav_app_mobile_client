package pl.lodz.p.navapp.service;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import pl.lodz.p.navapp.NavAppApplication;

/**
 * Created by Łukasz Świtoń on 07.12.2016.
 */

public class RequestManager {

    public static void sendRequest(int method, String URL, Response.Listener responseListener,Response.ErrorListener errorListener){
        StringRequest stringRequest = new StringRequest(method, URL,responseListener,errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                String creds = String.format("%s:%s", "user", "user");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
        NavAppApplication.getInstance().addToRequestQueue(stringRequest);
    }
}

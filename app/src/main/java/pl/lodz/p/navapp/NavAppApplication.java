package pl.lodz.p.navapp;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Kamil on 2016-11-13.
 */
public class NavAppApplication extends Application {
    private static final String TAG = "DEFAULT";
    public static NavAppApplication navAppApplication;
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        navAppApplication = this;

    }
    public static synchronized NavAppApplication getInstance(){
        return navAppApplication;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG: tag);
        getRequestQueue().add(request);
    }
    public <T> void addToRequestQueue(Request <T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }
    public void cancelPendingRequests(Object tag){
        if(requestQueue!=null){
            requestQueue.cancelAll(tag);
        }
    }
}

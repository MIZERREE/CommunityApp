package com.ree.mizer.communityapp.pojos;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyNotifications extends FirebaseMessagingService {

    private RequestQueue mRequestQueue;
    private String URL = "https://fcm.googleapis.com/fcm/send";
    Context context;

    public MyNotifications(){

    }
    
    public MyNotifications(Context context){
        this.context = context;
        mRequestQueue = Volley.newRequestQueue(context);
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        
    }

    public void sendNotification(String notificationTitle, String notificationBody){
        //Json object
        JSONObject mainJsonObject = new JSONObject();
        try {
            mainJsonObject.put("to","/topics/"+"news");
            //notification JSON
            JSONObject notificationJsonObject = new JSONObject();
            notificationJsonObject.put("title",notificationTitle);
            notificationJsonObject.put("body",notificationBody);
            mainJsonObject.put("notification",notificationJsonObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainJsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAA-KBjjHM:APA91bERhUFI_6gD2u-zArYz3VhPEgb4hjzkQoYSe-P9h6nhD9rJVq5H6YCfD6YEP9UMtVqaNO7mKfLorMx9bmvDjCa9mLHfdzW9-20hfc1SHrzrh-h358G0v3gvThtl0M8XwwAnaXXJ");
                    return header;
                }
            };

            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void sendNotificationToReports(String reporter,String title, String message){
        //Json object
        JSONObject mainJsonObject = new JSONObject();
        try {
            mainJsonObject.put("to","/topics/"+"reports");
            //notification JSON
            JSONObject notificationJsonObject = new JSONObject();
            notificationJsonObject.put("title",title);
            notificationJsonObject.put("body",reporter+" "+message);
            mainJsonObject.put("notification",notificationJsonObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainJsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> header = new HashMap<>();
                    header.put("content-type","application/json");
                    header.put("authorization","key=AAAA-KBjjHM:APA91bERhUFI_6gD2u-zArYz3VhPEgb4hjzkQoYSe-P9h6nhD9rJVq5H6YCfD6YEP9UMtVqaNO7mKfLorMx9bmvDjCa9mLHfdzW9-20hfc1SHrzrh-h358G0v3gvThtl0M8XwwAnaXXJ");
                    return header;
                }
            };

            mRequestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

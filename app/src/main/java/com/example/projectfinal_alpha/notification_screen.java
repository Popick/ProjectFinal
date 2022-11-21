package com.example.projectfinal_alpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class notification_screen extends AppCompatActivity {
    EditText title_et, message_et;
    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "AAAAhM6Jw64:APA91bH5HG2SWfJmNjHZ5dComVVAOZf3cG94X6_1C8ON1q7dllYz-8b6JqDrBTPsc4pae0VMKtRYo3GqoR_ZiRCp7h_BogrNjbxro1I1oPepQ7G5ZNXny-r2gVt19RvLTti9NijMByuG";
    private String contentType = "application/json";
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        title_et = (EditText) findViewById(R.id.title_et);
        message_et = (EditText) findViewById(R.id.content_et);

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/teacher");
    }

    public void send_message(View view) {
        if (title_et.getText().toString().isEmpty() || message_et.getText().toString().isEmpty()) {
            Log.i("title_et",title_et.getText().toString());
            Log.i("message_et",message_et.getText().toString());
            Toast.makeText(notification_screen.this, "dudeTF?", Toast.LENGTH_SHORT).show();
        }else{
            String topic = "/topics/teacher"; //topic has to match what the receiver subscribed to

            JSONObject notification = new JSONObject();
            JSONObject notifcationBody = new JSONObject();

            try {
                notifcationBody.put("title", title_et.getText().toString());
                notifcationBody.put("message", message_et.getText().toString()) ;  //Enter your notification message
                notification.put("to", topic);
                notification.put("data", notifcationBody);

            } catch (JSONException e) {
                Log.e("JSON ERROR", "json error" + e);
            }

            sendNotification(notification);
        }
    }

//    private RequestQueue requestQueue: RequestQueue by lazy {
//        Volley.newRequestQueue(this.applicationContext)
//    }
    //    RequestQueue requestQueue = Volley.newRequestQueue(notification_screen.this);


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(notification_screen.this.getApplicationContext());
        }
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private void sendNotification(JSONObject notification) {
        StringRequest req = new StringRequest(Request.Method.POST, FCM_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Check", "Response: " + response);
                Toast.makeText(notification_screen.this, "Response: " + response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            /**
             * Callback method that an error has been occurred with the provided error code and optional
             * user-readable message.
             *
             * @param error
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Check", "Error: " + error);
                Toast.makeText(notification_screen.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        }) {


            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return notification.toString().getBytes(StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "key=" + serverKey);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        addToRequestQueue(req);
    }
}

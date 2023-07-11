package com.example.learningplatform;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.learn.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    public MaterialButton btnLogin;
    public MaterialToolbar toolbar;
    private ProgressDialog pgdialog;
    private EditText email,password;
    private TextView gotosignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        gotosignup = findViewById(R.id.gotosignup);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage("Loading data...");
        pgdialog.setCancelable(false);
        btnLogin = findViewById(R.id.btnLogin);
        gotosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,CreateAccountActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login() {
        final String url = Utils.host + "/login";
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        pgdialog.show();
        try{
            body.put("email",email.getText().toString().trim());
            body.put("password",password.getText().toString().trim());
        }catch (JSONException ex){
            Log.d("JSONErr",ex.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp", response);
                        try {
                            JSONObject userInfo = new JSONObject(response);
                            if(userInfo.getBoolean("status")){
                                JSONObject obj = userInfo.getJSONArray("data").getJSONObject(0);
                                Utils utils = new Utils();
                                utils.setUserSession(Login.this,obj);
                                finish();
                                if(obj.getString("user_type").equals("Admin")){
                                    startActivity(new Intent(Login.this,AdminActivity.class));
                                }else{
                                    startActivity(new Intent(Login.this,ModulesActivity.class));
                                }
                            }else{
                                Snackbar.make(email,"Wrong username or password", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("jsonerr", "JSON Error " + (error != null ? error.getMessage() : ""));
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email",email.getText().toString().trim());
                params.put("password",password.getText().toString().trim());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                return headers;
            }
            @Override
            public byte[] getBody() {
                return body.toString().getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        ;

// add it to the RequestQueue
        queue.add(getRequest);
    }

}
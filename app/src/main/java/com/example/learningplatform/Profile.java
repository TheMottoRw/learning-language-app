package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.learn.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private GridView gridView;
    private ProfileAdapter profileAdapter;
    private FloatingActionButton fab;
    private ArrayList<ModuleModel> moduleModelList;
    private String level = "", url;
    private LinearLayout lnyLayout;
    private TextView tvEnrolled,tvCompleted,tvRemaining;
    private JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        gridView = findViewById(R.id.gridView);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvEnrolled = findViewById(R.id.tvEnrolled);
        tvRemaining = findViewById(R.id.tvRemaining);

        loadStats();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Umwirondoro: "+Utils.getUser(Profile.this,"name"));

        tvEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ModulesActivity.class);
                intent.putExtra("action","enrolled");
                intent.putExtra("modules",obj.toString());
                startActivity(intent);
            }
        });
        tvCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ModulesActivity.class);
                intent.putExtra("action","completed");
                intent.putExtra("modules",obj.toString());
                startActivity(intent);
            }
        });
        tvRemaining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ModulesActivity.class);
                intent.putExtra("action","remaining");
                intent.putExtra("modules",obj.toString());
                startActivity(intent);
            }
        });

    }

    private void loadStats() {
        url = Utils.host + "/stats/user/report?learner="+Utils.getUser(Profile.this,"id");
        Log.d("URL", url);
        RequestQueue queue = Volley.newRequestQueue(Profile.this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        Log.d("Logresp", response);
                        try {
                            obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray("completed");
                            setupJsonArrayToModuleModel(array);

                            tvEnrolled.setText(obj.getString("enrolled_len"));
                            tvCompleted.setText(obj.getString("completed_len"));
                            tvRemaining.setText(obj.getString("enrolled_not_completed_len"));
                            profileAdapter = new ProfileAdapter(Profile.this, moduleModelList);
                            gridView.setAdapter(profileAdapter);
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e("jsonerr", "JSON Error " + (error != null ? error.getMessage() : ""));
                    }
                }
        ) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                return headers;
            }
        };

// add it to the RequestQueue
        queue.add(getRequest);
    }

    private void setupJsonArrayToModuleModel(JSONArray array) {
        moduleModelList = new ArrayList<ModuleModel>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                if(Utils.getUser(Profile.this,"user_type").equals("Learner")) {
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"), obj.getString("is_enrolled"), obj.getString("is_enrolled").equals("enrolled")?obj.getString("marks"):"0", obj.getString("is_enrolled").equals("enrolled")?obj.getString("marks_total"):"0", obj.getString("is_enrolled").equals("enrolled")?obj.getString("is_completed"):"0"));
                }else{
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"),"","","",""));
                }
            } catch (JSONException e) {
                Log.d("jsonerr",e.getMessage());
            }

        }
    }

    protected void onResume() {
        super.onResume();
        loadStats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Backutton","D"+item.getItemId());
        if (item.getItemId() == 16908332) {
            finish();
        }else if (item.getItemId() == R.id.action_logout) {
            Utils.logout(Profile.this);
            finish();
            startActivity(new Intent(Profile.this, Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
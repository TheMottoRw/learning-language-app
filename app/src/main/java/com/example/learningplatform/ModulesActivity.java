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

public class ModulesActivity extends AppCompatActivity {
    private GridView gridView;
    private ModuleAdapter moduleAdapter;
    private FloatingActionButton fab;
    private ArrayList<ModuleModel> moduleModelList;
    private String level = "", moduleUrl = Utils.host + "/modules";
    private LinearLayout lnyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        gridView = findViewById(R.id.gridView);
        fab = findViewById(R.id.fab);
        lnyLayout = findViewById(R.id.lnyLayout);

        if (!Utils.getUser(ModulesActivity.this, "id").equals("0")) {
            if (Utils.getUser(ModulesActivity.this, "user_type").equals("Admin")) {
                level = getIntent().getStringExtra("id");
                moduleUrl = Utils.host + "/module/level/" + level;
            } else {
                fab.setVisibility(View.GONE);
                moduleUrl = Utils.host+"/modules/user/stats?learner="+Utils.getUser(ModulesActivity.this, "id");
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ModulesActivity.this, AddModuleActivity.class));
            }
        });
        loadModules();

    }

    private void loadModules() {
        Log.d("URL", moduleUrl);
        RequestQueue queue = Volley.newRequestQueue(ModulesActivity.this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, moduleUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        Log.d("Logresp", response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            if (arr.length() == 0)
                                lnyLayout.setVisibility(View.VISIBLE);
                            else
                                lnyLayout.setVisibility(View.GONE);
                            setupJsonArrayToModuleModel(arr);
                            moduleAdapter = new ModuleAdapter(ModulesActivity.this, moduleModelList);
                            gridView.setAdapter(moduleAdapter);
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ModulesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                if(Utils.getUser(ModulesActivity.this,"user_type").equals("Learner")) {
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"), obj.getString("is_enrolled")));
                }else{
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"),""));
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    protected void onResume() {
        super.onResume();
        loadModules();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_learner, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Utils.logout(ModulesActivity.this);
            finish();
            startActivity(new Intent(ModulesActivity.this, Login.class));
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
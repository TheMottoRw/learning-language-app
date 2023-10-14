package com.example.cyberawareness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
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
    private Intent intent;
    private JSONArray array;
    private ImageView imgNoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        imgNoContent = findViewById(R.id.imgNoContent);
//        Glide.with(this).load(R.drawable.congratulation).into(imgNoContent);
        gridView = findViewById(R.id.gridView);
        fab = findViewById(R.id.fab);
        lnyLayout = findViewById(R.id.lnyLayout);

        if (!Utils.getUser(ModulesActivity.this, "id").equals("0")) {
            if (Utils.getUser(ModulesActivity.this, "user_type").equals("Admin")) {
                level = getIntent().getStringExtra("id");
                moduleUrl = Utils.host + "/module/level/" + level;
                loadModules();
            } else if(Utils.getUser(ModulesActivity.this, "user_type").equals("Learner")){
                fab.setVisibility(View.GONE);
                intent = getIntent();
                if(intent.hasExtra("action")){
                    Log.d("HasAction","Module has action");
                    try{
                        JSONObject obj = new JSONObject(intent.getStringExtra("modules"));
                        switch (intent.getStringExtra("action")){
                            case "enrolled":
                                array = obj.getJSONArray("enrolled");break;
                            case "completed":
                                array = obj.getJSONArray("completed");break;
                            case "remaining":
                                array = obj.getJSONArray("enrolled_not_completed");break;
                            default:
                                Log.d("NoAction","No choice found");
                                array = new JSONArray();
                                break;
                        }
                        Log.d("ArrSize","Passed Len:: "+array.length());
                        if(array.length()==0){
                            lnyLayout.setVisibility(View.VISIBLE);
                        }else{
                            lnyLayout.setVisibility(View.GONE);
                            setupJsonArrayToModuleModel(array);
                            moduleAdapter = new ModuleAdapter(ModulesActivity.this, moduleModelList);
                            gridView.setAdapter(moduleAdapter);
                        }
                    }catch (JSONException ex){
                        Log.d("jsonerr00",ex.getMessage());
                    }
                }else {
                    moduleUrl = Utils.host + "/modules/user/stats?learner=" + Utils.getUser(ModulesActivity.this, "id");
                    loadModules();
                }
            }else{
                fab.setVisibility(View.GONE);
                moduleUrl = Utils.host+"/modules";
                loadModules();
            }
        }
        Log.d("JSURL",moduleUrl);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ModulesActivity.this, AddModuleActivity.class));
            }
        });

    }
    private void moduleDataLoader(){
        if (!Utils.getUser(ModulesActivity.this, "id").equals("0")) {
            if (Utils.getUser(ModulesActivity.this, "user_type").equals("Admin")) {
                level = getIntent().getStringExtra("id");
                moduleUrl = Utils.host + "/module/level/" + level;
            } else if(Utils.getUser(ModulesActivity.this, "user_type").equals("Learner")){
                fab.setVisibility(View.GONE);
                intent = getIntent();
                if(intent.hasExtra("action")){
                    Log.d("HasAction","Module has action "+intent.getStringExtra("action"));
                    try{
                        JSONObject obj = new JSONObject(intent.getStringExtra("modules"));
                        switch (intent.getStringExtra("action")){
                            case "enrolled":
                                moduleUrl = Utils.host + "/stats/enrolled?learner=" + Utils.getUser(ModulesActivity.this, "id");
//                                array = obj.getJSONArray("enrolled");
                                break;
                            case "completed":
                                moduleUrl = Utils.host + "/stats/completed?learner=" + Utils.getUser(ModulesActivity.this, "id");
//                                array = obj.getJSONArray("completed");
                                break;
                            case "remaining":
                                moduleUrl = Utils.host + "/stats/notcompleted?learner=" + Utils.getUser(ModulesActivity.this, "id");
//                                array = obj.getJSONArray("enrolled_not_completed");
                                break;
                            default:
                                moduleUrl = Utils.host + "/modules/user/stats?learner=" + Utils.getUser(ModulesActivity.this, "id");
                                Log.d("NoAction","No choice found");
//                                array = new JSONArray();
                                break;
                        }
                        Log.d("URLToLoad",moduleUrl);
                        loadModules();
                    }catch (JSONException ex){
                        Log.d("jsonerr00",ex.getMessage());
                    }
                }else {
                    moduleUrl = Utils.host + "/modules/user/stats?learner=" + Utils.getUser(ModulesActivity.this, "id");
                    loadModules();
                }
            }else{
                fab.setVisibility(View.GONE);
                moduleUrl = Utils.host+"/modules";
            }
        }

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
                            array = new JSONArray(response);
                            if (array.length() == 0)
                                lnyLayout.setVisibility(View.VISIBLE);
                            else
                                lnyLayout.setVisibility(View.GONE);
                            setupJsonArrayToModuleModel(array);
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
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"), obj.getString("is_enrolled"), obj.getString("is_enrolled").equals("enrolled")?obj.getString("marks"):"0", obj.getString("is_enrolled").equals("enrolled")?obj.getString("marks_total"):"0", obj.getString("is_enrolled").equals("enrolled")?obj.getString("is_completed"):"0"));
                }else{
                    moduleModelList.add(new ModuleModel(obj.getString("id"), obj.getString("level"), obj.getString("name"), obj.getString("icon"),"","","",""));
                }
            } catch (JSONException e) {
                Log.d("jsonerr01",e.getMessage());
            }

        }
    }

    protected void onResume() {
        super.onResume();
//        loadModules();
        moduleDataLoader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(Utils.getUser(ModulesActivity.this,"user_type").equals("Admin")){
            getMenuInflater().inflate(R.menu.menu_admin, menu);
        }else if(Utils.getUser(ModulesActivity.this,"user_type").equals("Learner")){
            getMenuInflater().inflate(R.menu.menu_learner, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(ModulesActivity.this, Profile.class));
            return super.onOptionsItemSelected(item);
        }else if (item.getItemId() == R.id.action_logout) {
            Utils.logout(ModulesActivity.this);
            finish();
            startActivity(new Intent(ModulesActivity.this, Login.class));
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
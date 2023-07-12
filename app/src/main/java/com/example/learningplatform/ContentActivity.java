package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
import java.util.List;
import java.util.Map;

public class ContentActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private LinearLayout lnyLayout;
    private JSONArray array;
    private ExpandableListDataPump edp;
    private ProgressDialog pgdialog;
    private String moduleId;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        pgdialog = new ProgressDialog(ContentActivity.this);
        pgdialog.setMessage("Loading data...");
        pgdialog.setCancelable(false);
        moduleId = getIntent().getStringExtra("id");
        lnyLayout = findViewById(R.id.lnyLayout);
        fab = findViewById(R.id.fab);
        loadContents();

        if(!Utils.getUser(ContentActivity.this,"user_type").equals("Admin")){
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this,AddContentActivity.class);
                intent.putExtra("id",moduleId);
                startActivity(intent);
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Expanded.",
//                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        expandableListTitle.get(groupPosition) + " List Collapsed.",
//                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//                Toast.makeText(
//                        getApplicationContext(),
//                        expandableListTitle.get(groupPosition)
//                                + " -> "
//                                + expandableListDetail.get(
//                                expandableListTitle.get(groupPosition)).get(
//                                childPosition), Toast.LENGTH_SHORT
//                ).show();
                return false;
            }
        });


    }

    private void initArray() {
        try {
            array = new JSONArray();
            edp = new ExpandableListDataPump();
            List<String> arr = new ArrayList<>();
            List<String> arrk = new ArrayList<>();

            arr.add("Hello");
            arr.add("Good morning");
            arr.add("Good afternoon");
            arr.add("Good bye my friend");
            arr.add("I was missing you");

            arrk.add("Muraho");
            arrk.add("Mwaramutse");
            arrk.add("Mwiriwe");
            arrk.add("Mwirirwe");
            arrk.add("Nari ngukumbuye");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = new JSONObject();
                obj.put("eng_word", arr.get(i));
                obj.put("kiny_word", arrk.get(i));
                array.put(obj);
            }
            edp.setData(array);
        } catch (JSONException ex) {
            Log.d("JSONErr", ex.getMessage());
        }
    }

    private void loadContents() {
        final String url = Utils.host + "/content/module/" + moduleId;
        pgdialog.show();
        Log.d("URL", url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp", response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            if (arr.length() == 0) {
                                Toast.makeText(ContentActivity.this, "No content found", Toast.LENGTH_SHORT).show();
                                lnyLayout.setVisibility(View.VISIBLE);
                            }else{
                                lnyLayout.setVisibility(View.GONE);
                            }
                            edp = new ExpandableListDataPump();
                            edp.setData(arr);
                            expandableListDetail = edp.getDetails();
                            expandableListTitle = edp.getTitles();
                            expandableListAdapter = new CustomExpandableListAdapter(ContentActivity.this, expandableListTitle, expandableListDetail);
                            expandableListView.setAdapter(expandableListAdapter);

                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(ContentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
        ;

// add it to the RequestQueue
        queue.add(getRequest);
    }
    @Override
    protected void onResume(){
        super.onResume();
        loadContents();
    }

}
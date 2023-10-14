package com.example.cyberawareness;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle, expandableListIds, expandableListExplanations;
    HashMap<String, List<String>> expandableListDetail;
    private LinearLayout lnyLayout;
    private JSONArray array;
    private ExpandableListDataPump edp;
    private ProgressDialog pgdialog;
    private String moduleId;
    private FloatingActionButton fab, fabUpload;
    private Button btnQuiz;

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
        fabUpload = findViewById(R.id.fabUpload);
        btnQuiz = findViewById(R.id.btnQuiz);
        if (Utils.getUser(ContentActivity.this, "user_type").equals("Admin")) {
            btnQuiz.setVisibility(View.GONE);
        } else {
            enrollModule();
            loadLearningStats();
        }
        loadContents();
        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, QuizActivity.class);
                intent.putExtra("module", moduleId);
                startActivity(intent);
            }
        });

        if (!Utils.getUser(ContentActivity.this, "user_type").equals("Admin")) {
            fab.setVisibility(View.GONE);
            fabUpload.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, AddContentActivity.class);
                intent.putExtra("id", moduleId);
                startActivity(intent);
            }
        });
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, UploadContentActivity.class);
                intent.putExtra("id", moduleId);
                startActivity(intent);
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Log.d("GPOS", "G " + expandableListIds.get(groupPosition));
                if (Utils.getUser(ContentActivity.this, "user_type").equals("Learner"))
                    enrollContent(expandableListIds.get(groupPosition));
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Intent intent = new Intent(ContentActivity.this,UpdateContentActivity.class);
                intent.putExtra("id",expandableListIds.get(childPosition));
                intent.putExtra("title",expandableListTitle.get(groupPosition));
                intent.putExtra("content",expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                intent.putExtra("explanation",expandableListExplanations.get(groupPosition));
                intent.putExtra("module",moduleId);
                startActivity(intent);
                Toast.makeText(ContentActivity.this,"ID:"+expandableListIds.get(groupPosition),Toast.LENGTH_SHORT).show();
                return false;
            }
        });


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
                                lnyLayout.setVisibility(View.VISIBLE);
                            } else {
                                lnyLayout.setVisibility(View.GONE);
                            }
                            edp = new ExpandableListDataPump();
                            edp.setData(arr);
                            expandableListDetail = edp.getDetails();
                            expandableListTitle = edp.getTitles();
                            expandableListIds = edp.getIds();
                            expandableListExplanations = edp.getExplanations();
                            expandableListAdapter = new CustomExpandableListAdapter(ContentActivity.this, moduleId, expandableListIds, expandableListTitle, expandableListDetail);
                            expandableListView.setAdapter(expandableListAdapter);

                            if (Utils.getUser(ContentActivity.this, "user_type").equals("Admin"))
                                btnQuiz.setVisibility(View.GONE);
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

    private void enrollContent(String contentId) {
        final String url = Utils.host + "/completed/content";
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        try {
            body.put("module", moduleId);
            body.put("content", contentId);
            body.put("learner", Utils.getUser(ContentActivity.this, "id"));
        } catch (JSONException ex) {
            Log.d("JSONErr", ex.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        try {
                            JSONObject res = new JSONObject(response);
                            Log.d("JSONInfo", res.toString());
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void enrollModule() {
        final String url = Utils.host + "/enroll";
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        try {
            body.put("module", moduleId);
            body.put("learner", Utils.getUser(ContentActivity.this, "id"));
        } catch (JSONException ex) {
            Log.d("JSONErr", ex.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        try {
                            JSONObject res = new JSONObject(response);
                            Log.d("JSONInfo", res.toString());
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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

    private void loadLearningStats() {
        final String url = Utils.host + "/stats/progress?module=" + moduleId + "&learner=" + Utils.getUser(ContentActivity.this, "id");
        Log.d("URL", url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        Log.d("Logresp", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getInt("percentage")==100 && !obj.getBoolean("is_completed")) {
                                btnQuiz.setVisibility(View.VISIBLE);
                            } else {
                                btnQuiz.setVisibility(View.GONE);
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
    protected void onResume() {
        super.onResume();
        if(Utils.getUser(ContentActivity.this,"user_type").equals("Learner")) {
            loadContents();
            loadLearningStats();
        }else{
            loadContents();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Utils.getUser(ContentActivity.this, "user_type").equals("Admin")) {
            getMenuInflater().inflate(R.menu.menu_content, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_quiz) {
            Intent intent = new Intent(ContentActivity.this, QuizManageActivity.class);
            intent.putExtra("module", moduleId);
            startActivity(intent);
            return super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.action_logout) {
            Utils.logout(ContentActivity.this);
            finish();
            startActivity(new Intent(ContentActivity.this, Login.class));
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
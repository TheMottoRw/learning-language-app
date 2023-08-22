package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class QuizManageActivity extends AppCompatActivity {
    private LinearLayoutManager lnyManager;
    private RecyclerView recyclerView;
    private ProgressDialog pgdialog;
    private String moduleId;
    private FloatingActionButton fabAdd,fabUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_manage);
        lnyManager = new LinearLayoutManager(QuizManageActivity.this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(lnyManager);
        fabAdd = findViewById(R.id.fabAdd);
        fabUpload = findViewById(R.id.fabUpload);
        moduleId = getIntent().getStringExtra("module");
        pgdialog = new ProgressDialog(QuizManageActivity.this);
        pgdialog.setMessage("Loading data...");
        pgdialog.setCancelable(false);
        moduleId = getIntent().getStringExtra("module");
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizManageActivity.this, AddQuizActivity.class);
                intent.putExtra("module",moduleId);
                startActivity(intent);
            }
        });
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizManageActivity.this, UploadQuizActivity.class);
                intent.putExtra("module",moduleId);
                startActivity(intent);
            }
        });

        loadQuiz();
    }
    private void loadQuiz() {
        final String url = Utils.host + "/quiz/module/"+moduleId;
        pgdialog.show();
        Log.d("URL", url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(QuizManageActivity.this);
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
                            if (arr.length() > 0) {
                                QuizManageAdapter adapter = new QuizManageAdapter(QuizManageActivity.this, arr);
                                recyclerView.setAdapter(adapter);
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
                        Toast.makeText(QuizManageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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



    protected void onResume() {
        super.onResume();
        loadQuiz();
    }
}
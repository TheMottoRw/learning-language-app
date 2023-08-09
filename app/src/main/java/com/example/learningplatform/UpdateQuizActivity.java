package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.learn.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateQuizActivity extends AppCompatActivity {
    private String quizId,moduleId;
    private EditText edtQuestion,edtAnswer,edtChoices;
    private Button btnCreate;
    private ProgressDialog pgdialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_quiz);
        edtQuestion = findViewById(R.id.edtQuestion);
        edtAnswer = findViewById(R.id.edtAnswer);
        edtChoices = findViewById(R.id.edtChoices);
        btnCreate = findViewById(R.id.btnCreate);
        pgdialog = new ProgressDialog(UpdateQuizActivity.this);
        pgdialog.setMessage("Saving data...");
        pgdialog.setCancelable(false);
        Intent intent = getIntent();
        Log.d("Data",intent.getStringExtra("question"));
        quizId = intent.getStringExtra("id");
        moduleId = intent.getStringExtra("module");
        edtQuestion.setText(intent.getStringExtra("question"));
        edtAnswer.setText(intent.getStringExtra("answer"));
        edtChoices.setText(intent.getStringExtra("options"));

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }
    private void save() {
        final String url = Utils.host + "/quiz/"+quizId;
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        pgdialog.show();
        try{
            body.put("question",edtQuestion.getText().toString().trim());
            body.put("options",edtChoices.getText().toString().trim());
            body.put("answer",edtAnswer.getText().toString().trim());
            body.put("module",moduleId);
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
                            JSONObject obj = new JSONObject(response);
                            Snackbar.make(edtQuestion,obj.getString("message"), Snackbar.LENGTH_SHORT).show();
                            edtQuestion.setText("");
                            edtChoices.setText("");
                            edtAnswer.setText("");
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(UpdateQuizActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

}
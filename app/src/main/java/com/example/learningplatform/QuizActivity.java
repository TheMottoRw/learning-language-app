package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizActivity extends AppCompatActivity {
    private ProgressDialog pgdialog;
    private LinearLayout lnyLayout;
    private Button btnSubmit;
    private JSONObject questionsObj;
    private String moduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        lnyLayout = findViewById(R.id.lnyLayout);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage("Loading...");
        pgdialog.setCancelable(false);
        btnSubmit = findViewById(R.id.btnSubmit);
        moduleId = getIntent().getStringExtra("module");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("QuestionsAnswers",questionsObj.toString());
                submitResult();
            }
        });
        questionsObj = new JSONObject();
        loadQuiz();
    }
    private void loadQuiz() {
        pgdialog.show();
        final String url = Utils.host+"/quiz/module/"+moduleId;
        Log.d("URL", url);
        RequestQueue queue = Volley.newRequestQueue(QuizActivity.this);
        StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        pgdialog.dismiss();
                        Log.d("Logresp", response);
                        try {
                            JSONArray arr = new JSONArray(response);
                            for(int i=0;i<arr.length();i++){
                                JSONObject obj = arr.getJSONObject(i);
                                String questionId = obj.getString("id");
                                TextView tv = new TextView(QuizActivity.this);
                                tv.setText((i+1)+". "+obj.getString("question"));
                                RadioGroup rg = new RadioGroup(QuizActivity.this);
                                rg.setId(i);
                                questionsObj.put(obj.getString("id"),"");
                                rg.setOrientation(RadioGroup.VERTICAL);
                                String options = arr.getJSONObject(i).getString("options");
                                String[] optionsArr = options.split(",");
                                RadioButton[] rb = new RadioButton[optionsArr.length];
                                for(int x=0;x<optionsArr.length;x++){
                                    int radioId = Integer.parseInt(obj.getString("id")+x);
                                    rb[x] = new RadioButton(QuizActivity.this);
                                    rb[x].setText(optionsArr[x]);
                                    rb[x].setId(radioId);
                                    rg.addView(rb[x]);

                                    int optionIndex = x;
                                    rb[x].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                questionsObj.put(questionId,rb[optionIndex].getText());
                                                Toast.makeText(QuizActivity.this,rb[optionIndex].getId()+"=>"+tv.getText(),Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }
                                lnyLayout.addView(tv);
                                lnyLayout.addView(rg);
                            }
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QuizActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void submitResult() {
        final String url = Utils.host + "/submit/quiz";
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        Log.d("User", Utils.getUser(QuizActivity.this,"id"));
        Log.d("Module", moduleId);
        pgdialog.show();
        try{
            body.put("data",questionsObj);
            body.put("learner",Utils.getUser(QuizActivity.this,"id"));
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
                            Snackbar.make(lnyLayout,obj.getString("message"), Snackbar.LENGTH_SHORT).show();
                            SweetAlertDialog alert = new SweetAlertDialog(QuizActivity.this);
                            if(obj.getInt("marks")>(obj.getInt("total_marks")/2)){
                                alert.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                alert.setContentText("Watsinze ku manota "+obj.getInt("marks") +"/"+obj.getInt("total_marks"));
                            }
                            else{
                                alert.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                alert.setContentText("Watsinzwe ku manota "+obj.getInt("marks") +"/"+obj.getInt("total_marks"));
                            }
                            alert.setConfirmButton("Close", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                            alert.show();


                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(QuizActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
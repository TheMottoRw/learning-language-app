package com.example.learningplatform;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.learn.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LevelsFragment extends Fragment {
    private Context context;
    private LinearLayoutManager lnyManager;
    private RecyclerView recyclerView;
    private ProgressDialog pgdialog;

    public LevelsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_levels, container, false);
        context = view.getContext();
        lnyManager = new LinearLayoutManager(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(lnyManager);
        pgdialog = new ProgressDialog(context);
        pgdialog.setMessage("Loading data...");
        pgdialog.setCancelable(false);
        loadLevels();
        return view;
    }

    private void loadLevels() {
        final String url = Utils.host + "/levels";
        pgdialog.show();
        Log.d("URL", url);
//        tvLoggingIn.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(context);
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
                                LevelsAdapter adapter = new LevelsAdapter(context, arr);
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
                        Toast.makeText(context, "Hari ibitagenze neza", Toast.LENGTH_SHORT).show();
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

}
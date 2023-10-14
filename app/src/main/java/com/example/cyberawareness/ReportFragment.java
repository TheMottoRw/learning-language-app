package com.example.cyberawareness;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportFragment extends Fragment {
    private Context context;
    private ProgressDialog pgdialog;
    private EditText start, to;
    private ArrayList<String> filterTypes;
    private Button btnFilter;
    private Spinner spnFilterType;
    private DatePickerDialog datePickerDialog, startCalendarPicker, toCalendarPicker;
    private Calendar dateSelected, startCalendar, toCalendar, newCalendar;
    private LinearLayoutManager lnyManager;
    private RecyclerView recyclerView;

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        context = view.getContext();
        pgdialog = new ProgressDialog(context);
        lnyManager = new LinearLayoutManager(context);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(lnyManager);
        pgdialog.setMessage("Loading data...");
        pgdialog.setCancelable(false);
        spnFilterType = view.findViewById(R.id.spnFilterType);
        start = view.findViewById(R.id.start);
        to = view.findViewById(R.id.to);
        loadReport();
        start.setEnabled(true);
        to.setEnabled(true);
        btnFilter = view.findViewById(R.id.btnFilter);
        filterTypes = new ArrayList<>();
        filterTypes.add("passed");
        filterTypes.add("notpassed");
        filterTypes.add("login_history");
        filterReport();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartDateTimeField();
            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToDateTimeField();
                ;
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterReport();
            }
        });

        return view;
    }

    private void setStartDateTimeField() {
        startCalendar = Calendar.getInstance();
        newCalendar = startCalendar;
        startCalendarPicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                start.setText(Utils.dateFormat(startCalendar.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        startCalendarPicker.show();
    }

    private void setToDateTimeField() {
        toCalendar = Calendar.getInstance();
        newCalendar = startCalendar;
        toCalendarPicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                toCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                to.setText(Utils.dateFormat(toCalendar.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        toCalendarPicker.show();
    }

    private void loadReport() {
        final String url = Utils.host + "/stats/report";//?start="+start.getText().toString()+"&end="+to.getText().toString()+"&product="+productIds.get(spnProducts.getSelectedItemPosition())+"&stock_type="+String.valueOf(spnStockAction.getSelectedItemPosition()).trim();
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        pgdialog.show();
//        try{
//            body.put("start",start.getText().toString().trim());
//            body.put("end",to.getText().toString().trim());
//            body.put("product",productIds.get(spnProducts.getSelectedItemPosition()));
//            body.put("stock_type",String.valueOf(spnStockAction.getSelectedItemPosition()).trim());
//        }catch (JSONException ex){
//            Log.d("JSONErr",ex.getMessage());
//        }
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
                            ReportAdapter adapter = new ReportAdapter(context, arr);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void filterReport() {
        String filterType = filterTypes.get(spnFilterType.getSelectedItemPosition());
        final String url = Utils.host + "/stats/filtered/report?start=" + start.getText().toString() + "&end=" + to.getText().toString() + "&filterType=" + filterType;
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        pgdialog.show();
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
                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("data");

                            switch (filterType) {
                                case "passed":
                                    ReportPassedAdapter reportPassedAdapter = new ReportPassedAdapter(context, arr);
                                    recyclerView.setAdapter(reportPassedAdapter);
                                    break;
                                case "notpassed":
                                    ReportNotPassedAdapter notPassedAdapter = new ReportNotPassedAdapter(context, arr);
                                    recyclerView.setAdapter(notPassedAdapter);
                                    break;
                                case "login_history":
                                    ReportLoginHistoryAdapter reportLoginHistoryAdapter = new ReportLoginHistoryAdapter(context, arr);
                                    recyclerView.setAdapter(reportLoginHistoryAdapter);
                                    break;
                                default:
                                    ReportAdapter adapter = new ReportAdapter(context, arr);
                                    recyclerView.setAdapter(adapter);
                                    break;
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
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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
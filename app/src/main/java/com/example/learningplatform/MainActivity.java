package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.learn.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    private JSONArray array;
    private ExpandableListDataPump edp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        initArray();
        expandableListDetail = edp.getDetails();
        expandableListTitle = edp.getTitles();
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
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
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    private void initArray(){
        try{
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
            for (int i=0;i<arr.size();i++){
                JSONObject obj = new JSONObject();
                obj.put("eng_word",arr.get(i));
                obj.put("kiny_word",arrk.get(i));
                array.put(obj);
            }
            edp.setData(array);
        }catch (JSONException ex){
            Log.d("JSONErr",ex.getMessage());
        }
    }
}
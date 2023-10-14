package com.example.cyberawareness;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    private List<String> titles,ids,explanations;
    private String moduleId = "";
    private HashMap<String,List<String>> details;
    public void setData(JSONArray data){
        try{
            ids = new ArrayList<>();
            titles = new ArrayList<>();
            explanations = new ArrayList<>();
            details = new HashMap<>();
            for (int i=0;i<data.length();i++){
                JSONObject obj = data.getJSONObject(i);
                ArrayList<String> arrayList = new ArrayList<String>();
                if(!obj.getString("explanation").equals(""))
                    arrayList.add(obj.getString("content")+"\n\n"+obj.getString("explanation"));
                else
                    arrayList.add(obj.getString("content"));
                ids.add(obj.getString("id"));
                explanations.add(obj.getString("explanation"));
                titles.add(obj.getString("title"));
                details.put(obj.getString("title"),arrayList);
            }
        }catch (JSONException ex){
            Log.d("JSONErr",ex.getMessage());
        }
    }
    public List<String> getTitles(){
        return titles;
    }
    public List<String> getIds(){
        return ids;
    }  public List<String> getExplanations(){
        return explanations;
    }
    public String getModuleId(){
        return moduleId;
    }
    public HashMap<String,List<String>> getDetails(){
        return details;
    }
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> cricket = new ArrayList<String>();
        cricket.add("India");
        cricket.add("Pakistan");
        cricket.add("Australia");
        cricket.add("England");
        cricket.add("South Africa");

        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        football.add("Spain");
        football.add("Germany");
        football.add("Netherlands");
        football.add("Italy");

        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        basketball.add("Spain");
        basketball.add("Argentina");
        basketball.add("France");
        basketball.add("Russia");

        expandableListDetail.put("CRICKET TEAMS", cricket);
        expandableListDetail.put("FOOTBALL TEAMS", football);
        expandableListDetail.put("BASKETBALL TEAMS", basketball);
        return expandableListDetail;
    }
}
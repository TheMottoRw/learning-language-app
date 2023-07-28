package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadContentActivity extends AppCompatActivity {
    private TextView chooseFile;
    private Button btnUpload;
    private String fileData,fileName;
    private Uri filePath;
    private int PICK_FILE_REQUEST = 1;
    private String moduleId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_content);
        chooseFile = findViewById(R.id.tvChooseFile);
        btnUpload = findViewById(R.id.btnUpload);
        moduleId = getIntent().getStringExtra("id");

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("BtnCl","Button clicked");
                if(!fileData.equals("")) {
                    Log.d("Data","Not empty");
                    sendData();
                }
                Log.d("BtnAfter","After button clicked");
            }
        });
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick a file"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
                if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                fileName = getFileName(filePath);

                byte[] byteData = getBytes(data.getData());
                Log.d("FileName",fileName);
                String mimeType = getContentResolver().getType(data.getData());
                fileData = new String(byteData, "UTF-8");

                Log.d("FileData",fileData);//show the picked uri
                chooseFile.setText(fileName);

            }
        } catch (Exception e) {
            Log.e("ImagePick", "Exception in onActivityResult : " + e.getMessage());
        }
    }

    private void sendData() {
        JSONObject body = new JSONObject();
        try{
            body.put("data",fileData);
        }catch (JSONException ex){
            Log.d("JSONErrPars",ex.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
// prepare the Request
        StringRequest getRequest = new StringRequest(Request.Method.POST, Utils.host+"/upload/content/"+moduleId,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // display response
                        Log.d("Logresp", response);
                        try {
                            JSONObject res = new JSONObject(response);
                            Log.d("Status",res.getString("status"));
                            Toast.makeText(UploadContentActivity.this,res.getString("message"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UploadContentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                Log.d("DataCont",body.toString());
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
    private String getFileName(Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Log.d("FilePath",uri.toString());
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        Log.d("FileCount",String.valueOf(cursor.getCount()));

        cursor.moveToFirst();

        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));

        cursor.close();

        return fileName;
    }

    byte[] getBytes(Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                Log.d("FData 1"+len,outputStream.toString());
            }
            return outputStream.toByteArray();
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage().toString());
            Toast.makeText(UploadContentActivity.this, "getBytes error:" + ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

}
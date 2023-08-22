package com.example.learningplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddLevel extends AppCompatActivity {
    private ProgressDialog pgdialog;
    private EditText edtLevel;
    private Button btnChooseIcon, btnCreate;
    private static int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;
    private Bitmap bitmap;
    private ImageView imageView;
    private static final String UPLOAD_URL = Utils.host + "/level";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_level);
        pgdialog = new ProgressDialog(this);
        pgdialog.setMessage("Saving data...");
        pgdialog.setCancelable(false);
        edtLevel = findViewById(R.id.edtLevel);
        imageView = findViewById(R.id.iconPreview);
        btnChooseIcon = findViewById(R.id.btnChooseIcon);
        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLevel();
            }
        });
        btnChooseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }
    private void save() {
        final String url = Utils.host + "/level";
        JSONObject body = new JSONObject();
        Log.d("URL", url);
        pgdialog.show();
        try{
            body.put("name",edtLevel.getText().toString().trim());
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
                            Snackbar.make(edtLevel,obj.getString("message"), Snackbar.LENGTH_SHORT).show();
                            edtLevel.setText("");
                        } catch (JSONException ex) {
                            Log.d("Json error", ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pgdialog.dismiss();
                        Toast.makeText(AddLevel.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                filePath = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    Display display = getWindowManager().getDefaultDisplay();
                    int height = 230, dispWidth = display.getWidth() * 2 / 3, dispHeight = display.getHeight();
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(dispWidth, height));
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e("ImagePick", "Exception in onActivityResult : " + e.getMessage());
        }
    }

    public String getStringImage(Bitmap bmp) {
        String encodedImage = "";
        if(bmp!=null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }
        return encodedImage;
    }

    private void addLevel() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddLevel.this, "Uploading...", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    Log.d("ResErr",s);
                    JSONObject obj = new JSONObject(s);
                    Toast.makeText(getApplicationContext(),obj.getString("message"),Toast.LENGTH_LONG).show();
                    edtLevel.setText("");

                }catch (JSONException ex){
                    Log.d("Jsonerr",ex.getMessage());
                }
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);
                Log.d("StrImageCount", String.valueOf(uploadImage.length()));

                HashMap<String, String> data = new HashMap<>();

                data.put("icon", uploadImage);
                data.put("name",edtLevel.getText().toString().trim());

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute(bitmap);
    }
}
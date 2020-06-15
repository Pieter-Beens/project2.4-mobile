package com.example.sodine;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {

    public EditText nameInput;
    public EditText passwordInput;
    public TextView errorText;

    public String[][] mockDetails = {
            {"Bart", "sense"},
            {"Anna", "hummer"},
            {"test", "test"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = findViewById(R.id.nameInput);
        passwordInput = findViewById(R.id.passwordInput);
        errorText = findViewById(R.id.errorTextView);
    }

    public void login(View view) {
        String username = nameInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean success = false;

        if(validate() != null) {
            success = true;
        }
        if (success) {
            MainActivity.session.setUser(username);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            errorText.setText(R.string.login_error);
        }
    }

    //TODO: move everything below here to API class

    public String validate() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(this::validateA);
        String resultFromAsync = "";
        try {
            resultFromAsync= completableFuture.get();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        Log.d("formdata", resultFromAsync);
        Log.d("formdata", "bitch please");
        try {
            JSONObject jsonObject = new JSONObject(resultFromAsync);
            String result = (String) jsonObject.get("access_token");
            Log.d("formdata", result);
            return result;
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }

    public String validateA() {
        String username = nameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Log.d("Username", username);
        Log.d("Password", password);
        String url = "http://sodine.nl:5000/api/v1.0/login";
        String result = "";

        String response = "";
        try {

            Map<String, String> params = new HashMap<String, String>(2);
            params.put("username", username);
            params.put("password", password);
            result = multipartRequest(url, params);

        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        return result;
    }

    public String multipartRequest(String urlTo, Map<String, String> parmas) throws IOException {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";


        try {
            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                throw new IOException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
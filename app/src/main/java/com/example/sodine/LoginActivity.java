package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

        nameInput = findViewById(R.id.mailInput);
        passwordInput = findViewById(R.id.passwordInput);
        errorText = findViewById(R.id.errorTextView);
    }

    public void login(View view) {
        String email = nameInput.getText().toString();
        String password = passwordInput.getText().toString();
        boolean success = false;

        String JWT = validate();

        if(JWT != null) {
            MainActivity.session.setUser(email);
            MainActivity.session.setJWT(JWT);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            errorText.setText(R.string.login_error);
        }
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //TODO: move everything below here to API class

    public String validate() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(this::validateA);
        String resultFromAsync = "";
        try {
            resultFromAsync = completableFuture.get();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        Log.d("formdata raw", resultFromAsync);
        try {
            JSONObject jsonObject = new JSONObject(resultFromAsync);
            String result = (String) jsonObject.get("access_token");
            Log.d("formdata JSONObject", result);
            return result;
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return null;
    }

    public String validateA() {
        String email = nameInput.getText().toString();
        String password = passwordInput.getText().toString();

        Log.d("email a", email);
        Log.d("Password a", password);
        String url = "https://sodine.nl:5000/api/v1.0/user/login";

        String result = "";
        try {
            Map<String, String> params = new HashMap<String, String>(2);
            params.put("email", email);
            params.put("password", password);
            result = multipartRequest(url, params);

        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        return result;
    }

    public String multipartRequest(String urlTo, Map<String, String> params) throws IOException {
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
            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

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
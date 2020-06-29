package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
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

public class EditHouseholdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_household);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(this::getRoommateData);
        String json = "";
        try {
            json = completableFuture.get();
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }


        HashMap<Button,String> buttonToJson = new HashMap<>();
        try {
            JSONArray jsonObject = new JSONArray(json);
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject usersLiveTogether = jsonObject.getJSONObject(i);

                Button newButton = new Button(this);
                newButton.setOnClickListener(v -> deleteRoommate(usersLiveTogether));
                String roommate = (String) usersLiveTogether.get("roommate");
                newButton.setText(roommate);

                buttonToJson.put(newButton, usersLiveTogether.toString());

                linearLayout.addView(newButton);
            }
        } catch (JSONException e) {
            Log.d("JSONERROR", e.toString());
        }
    }

    public String getRoommateData() {
        String userID = MainActivity.session.getUserID(); //TODO: change from name to ID from JWT
        String url = "https://sodine.nl:5000/api/v1.0/roommates/" + userID;
        String response = "";
        try {
            Map<String, String> params = new HashMap<>(1);
            params.put("id", userID);
            response = sendJWT(url);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
        return response;
    }


    public String sendFormdata(String urlTo, Map<String, String> params) throws IOException {
        HttpURLConnection connection;
        DataOutputStream outputStream;
        InputStream inputStream;

        //formdata stuff
        String TWO_HYPHENS = "--";
        String BOUNDARY = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String LINE_END = "\r\n";

        try {
            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; BOUNDARY=" + BOUNDARY);

            outputStream = new DataOutputStream(connection.getOutputStream());

            Iterator<String> keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = params.get(key);

                //formdata stuff
                outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
                outputStream.writeBytes("Content-Type: text/plain" + LINE_END);
                outputStream.writeBytes(LINE_END);
                outputStream.writeBytes(value);
                outputStream.writeBytes(LINE_END);
            }

            //formdata stuff
            outputStream.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);


            if (200 != connection.getResponseCode()) {
                throw new IOException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            String result = this.convertStreamToString(inputStream);

            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String sendJWT(String urlTo) throws IOException {
        HttpURLConnection connection;
        DataOutputStream outputStream;
        InputStream inputStream;

        try {
            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");

            outputStream = new DataOutputStream(connection.getOutputStream());

            String JWT = MainActivity.session.getJWT();
            outputStream.writeBytes(JWT);


            if (201 != connection.getResponseCode()) {
                throw new IOException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            String result = this.convertStreamToString(inputStream);

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

    public void deleteRoommate(JSONObject jsonObject) {
        Intent intent = new Intent(this, EditHouseholdActivity.class);
        //TODO: send delete command to API
        startActivity(intent);
    }
}

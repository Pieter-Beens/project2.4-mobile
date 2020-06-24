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

import java.util.HashMap;
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
        String response = "";
        //TODO: send get command to API
        return response;
    }

    public void deleteRoommate(JSONObject jsonObject) {
        Intent intent = new Intent(this, EditHouseholdActivity.class);
        //TODO: send delete command to API
        startActivity(intent);
    }
}

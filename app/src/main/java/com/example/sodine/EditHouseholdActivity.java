package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditHouseholdActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    EditText addRoommateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_household);

        this.linearLayout = findViewById(R.id.roommatesLayout);
        this.addRoommateInput = findViewById(R.id.editText);

        JSONObject roommatesJSON = MainActivity.API.GET("roommates", MainActivity.session.getUserID());

        System.out.println("roommatesJSON: " + roommatesJSON);

        HashMap<Button,String> map = new HashMap<>();
        try {
            JSONArray roommatesArray = (JSONArray) roommatesJSON.get("key");
            for (int i = 0; i < roommatesArray.length(); i++) {
                JSONArray usersLiveTogether = (JSONArray) roommatesArray.get(i);

                String userID = usersLiveTogether.get(2).toString();

                JSONObject userDetails = MainActivity.API.GET("user/username", userID);

                System.out.println("userDetails: " + userDetails);

                TextView nameTextView = new TextView(this);
                nameTextView.setTextSize(36);
                nameTextView.setText((String) userDetails.get("name"));

                Button deleteButton = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                deleteButton.setLayoutParams(params);
                deleteButton.setBackgroundColor(getResources().getColor(R.color.red));
                deleteButton.setTextSize(24);
                deleteButton.setTextColor(getColor(R.color.white));
                deleteButton.setOnClickListener(v -> deleteRoommate(userID));
                deleteButton.setText("DELETE");

                System.out.println(linearLayout);

                this.linearLayout.addView(nameTextView);
                this.linearLayout.addView(deleteButton);
            }
        } catch (JSONException e) {
            Log.d("JSONERROR", e.toString());
        }
    }

    public void addRoommate(View view) {
        String roommateID = this.addRoommateInput.getText().toString();

        HashMap<String,String> args = new HashMap<>();
        args.put("id", roommateID);

        Intent intent = new Intent(this, EditHouseholdActivity.class);
        MainActivity.API.POST("roommates", MainActivity.session.getUserID(), args);
        startActivity(intent);
    }

    public void deleteRoommate(String roommateID) {
        HashMap<String,String> args = new HashMap<>();
        args.put("id", roommateID);

        Intent intent = new Intent(this, EditHouseholdActivity.class);
        MainActivity.API.DELETE("roommates", MainActivity.session.getUserID(), args);
        startActivity(intent);
    }
}

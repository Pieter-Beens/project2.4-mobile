package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sodine.util.APIConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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

        CompletableFuture<JSONObject> completableFuture = CompletableFuture.supplyAsync(() -> MainActivity.API.validate(email, password));
        JSONObject jwtObject = new JSONObject();
        try {
            jwtObject = completableFuture.get();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        String JWT = null;
        try {
            JWT = (String) jwtObject.get("access_token");
            Log.d("JWT", JWT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        if (JWT != null) {
            MainActivity.session.setJWT(JWT);

            CompletableFuture<JSONObject> completableFuture2 = CompletableFuture.supplyAsync(() -> MainActivity.API.GET("user_id"));
            JSONObject response = new JSONObject();
            try {
                response = completableFuture2.get();
            } catch (Exception e) {
                Log.d("Error", e.toString());
            }

            JSONArray jsonArray = null;
            try {
                jsonArray = (JSONArray) response.get("Identity");
            } catch (Exception e) {
                Log.d("ERROR", e.toString());
            }
            try {
                jsonArray = (JSONArray) jsonArray.get(0);
                MainActivity.session.setUserID((jsonArray.get(0)).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
}
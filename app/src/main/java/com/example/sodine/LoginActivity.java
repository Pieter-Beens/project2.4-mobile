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

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> MainActivity.API.validate(email, password));
        String resultFromAsync = "";
        try {
            resultFromAsync = completableFuture.get();
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        Log.d("formdata raw", resultFromAsync);
        String JWT = null;
        try {
            JSONObject jsonObject = new JSONObject(resultFromAsync);
            JWT = (String) jsonObject.get("access_token");
            Log.d("JWT", JWT);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }

        if (JWT != null) {
            MainActivity.session.setJWT(JWT);

            CompletableFuture<HashMap> completableFuture2 = CompletableFuture.supplyAsync(() -> MainActivity.API.GET("user_id"));
            HashMap<String, String> resultFromAsync2 = new HashMap<>();
            try {
                resultFromAsync2 = completableFuture2.get();
            } catch (Exception e) {
                Log.d("Error", e.toString());
            }

            MainActivity.session.setUserID(resultFromAsync2.get("Identity").substring(2, resultFromAsync2.get("Identity").indexOf("]")));

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
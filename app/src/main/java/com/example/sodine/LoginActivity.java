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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
        for (String[] details: mockDetails) {
            if (username.equals(details[0]) && password.equals(details[1])) {
                success = true;
                break;
            }
        }

        if (success) {
            MainActivity.session.setUser(username);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            errorText.setText(R.string.login_error);
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void login(View view) {
//        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(this::validate); //TODO: need Java SDK 8
//        String response = "";
//        try {
//            response = completableFuture.get();
//        } catch (Exception e) {
//            Log.d("ERROR", e.toString());
//        }
//
//        if (response == "user found") {
//            Intent intent = new Intent(this, MainActivity.class);
//
//            startActivity(intent);
//        } else {
//            errorText.setText(R.string.login_error);
//        }
//    }
//
//    public String validate() {
//        String username = nameInput.getText().toString();
//        String password = passwordInput.getText().toString();
//
//        Log.d("Username", username);
//        Log.d("Password", password);
//        String url = "http://sodine.nl:5000/api/v1.0" + username + password; //TODO: this is not the right format
//        URL realUrl;
//        String response = "";
//        try {
//            realUrl = new URL(url);
//            Log.d("URL", realUrl.toString());
//            URLConnection tc = realUrl.openConnection();
//            tc.connect();
//            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                Log.d("Line", line);
//                response += line;
//            }
//        } catch (Exception e) {
//            Log.d("Error", e.toString());
//        }
//        Log.d("Full Response", response);
//        JsonParser parser = new JsonParser(); //TODO: need to import JsonParser
//        parser.parse(response);
//
//        return response;
//    }
}

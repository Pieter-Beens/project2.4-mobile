package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sodine.util.APIConnection;
import com.example.sodine.util.Session;

public class MainActivity extends AppCompatActivity {

    public static Session session;
    public static APIConnection API;

    public static int risico = 0;

    public TextView usernameView;
    public TextView idView;
    public TextView statusView;
    public ImageView coronaImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameView = findViewById(R.id.usernameView);
        idView = findViewById(R.id.idTextView);
        statusView = findViewById(R.id.riskTextView);
        coronaImageView = findViewById(R.id.coronaImageView);

        if (session == null) {
            session = new Session(MainActivity.this);
        }

        if (API == null) {
            API = new APIConnection();
        }

        if (!session.getJWT().equals("")) {
            usernameView.setText(session.getUser());
            //TODO:
            // CompletableFuture<Map> completableFuture = CompletableFuture.supplyAsync(() -> API.validate(email, password));API.GET("user" + session.getUserID());
            //
            idView.setText("(#" + session.getUserID() + ")");
            if (risico <= 1) {
                coronaImageView.setImageResource(R.drawable.corona_0);
                statusView.setText(R.string.risk0);
            } else if (risico <= 3) {
                coronaImageView.setImageResource(R.drawable.corona_1);
                statusView.setText(R.string.risk1);
            } else if (risico <= 5) {
                coronaImageView.setImageResource(R.drawable.corona_2);
                statusView.setText(R.string.risk2);
            } else if (risico <= 7) {
                coronaImageView.setImageResource(R.drawable.corona_3);
                statusView.setText(R.string.risk3);
            } else {
                coronaImageView.setImageResource(R.drawable.corona_4);
                statusView.setText(R.string.risk4);
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void logout(View view) {
        session.clearSession(MainActivity.this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void reportCorona(View view) {
        Intent intent = new Intent(this, CoronaFormActivity.class);
        String username = usernameView.getText().toString();
        intent.putExtra("com.example.sodine.username", username);
        startActivity(intent);
    }

    public void addContact(View view) {
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    public void viewContacts(View view) {
        Intent intent = new Intent(this, ViewContactsActivity.class);
        startActivity(intent);
    }

    public void editHousehold(View view) {
        Intent intent = new Intent(this, EditHouseholdActivity.class);
        startActivity(intent);
    }
}

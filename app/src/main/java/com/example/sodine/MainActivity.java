package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sodine.util.Session;

public class MainActivity extends AppCompatActivity {

    public static Session session;

    public static boolean fucked = false;

    public TextView usernameView;
    public TextView statusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameView = findViewById(R.id.usernameView);
        statusView = findViewById(R.id.textView3);

        session = new Session(MainActivity.this);

        System.out.println("USER FOUND IN SESSION: " + session.getUser());

        if (!session.getUser().equals("")) {
            usernameView.setText(session.getUser());
            if (fucked) {
                statusView.setText(R.string.fucked);
                statusView.setTextColor(Color.RED);
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

package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class CoronaFormActivity extends AppCompatActivity {

    public Switch[] symptoms;
    public TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_form);

        nameTextView = findViewById(R.id.nameTextView);

        symptoms = new Switch[]{
                findViewById(R.id.koortsSwitch), findViewById(R.id.hoofdpijnSwitch),
                findViewById(R.id.borstpijnSwitch), findViewById(R.id.keelpijnSwitch),
                findViewById(R.id.bewegingsvermogenSwitch), findViewById(R.id.diarreeSwitch),
                findViewById(R.id.bindvliesontstekingSwitch), findViewById(R.id.hoestSwitch),
                findViewById(R.id.ademhalingsproblemenSwitch)
        };

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = intent.getStringExtra("com.example.sodine.username");
        nameTextView.setText(username);
    }

    public void goBack(View view) {
        int score = 0;
        for (Switch symptom : symptoms) {
            if (symptom.isChecked()) score++;
        }
        MainActivity.risico = score;
        startActivity(new Intent(this, MainActivity.class));
    }
}

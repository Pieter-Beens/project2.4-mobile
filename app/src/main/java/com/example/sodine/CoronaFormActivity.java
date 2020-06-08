package com.example.sodine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

public class CoronaFormActivity extends AppCompatActivity {

    public Switch switch1;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_form);

        textView = findViewById(R.id.textView2);
        switch1 = findViewById(R.id.switch1);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String username = intent.getStringExtra("com.example.sodine.username");
        textView.setText(username);
        switch1.setChecked(MainActivity.fucked);
    }

    public void goBack(View view) {
        MainActivity.fucked = switch1.isChecked();
        startActivity(new Intent(this, MainActivity.class));
    }
}

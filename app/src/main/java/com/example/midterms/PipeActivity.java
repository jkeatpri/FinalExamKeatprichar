package com.example.midterms;

import static com.example.midterms.MainActivity.PREF_NIGHT;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PipeActivity extends AppCompatActivity {
    public static final String EXTRA_PIPE ="com.example.midterm.PIPE";
    public static final String EXTRA_RESULT ="com.example.midterm.RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pipe);
        btnConfirmListenerMethod();

        // TODO Milestone A: get SharedPreference on night mode and change false to variable
        SharedPreferences preferences = getSharedPreferences("Pipe", Activity.MODE_PRIVATE);
        boolean night = preferences.getBoolean(PREF_NIGHT, false);
        initializeNightMode(night);

    }

    // TODO Milestone A: implement night mode
    private void initializeNightMode(boolean night) {
        ConstraintLayout clPipe = findViewById(R.id.clPipe);
        int textviews[] = {R.id.tvLblBrand, R.id.rbAce, R.id.rbAmily, R.id.rbArad, R.id.tvLblDiam, R.id.rbOnePoint, R.id.rbOnePtFive, R.id.rbPointFive, R.id.rbPointThree};
        if (night) {
            clPipe.setBackgroundColor(Color.BLACK);
            for (int tv : textviews) {
                TextView view = findViewById(tv);
                view.setTextColor(Color.WHITE);
            }
        } else {
            clPipe.setBackgroundColor(Color.WHITE);
            for (int tv : textviews) {
                TextView view = findViewById(tv);
                view.setTextColor(Color.BLACK);
            }
        }
    }

    private void btnConfirmListenerMethod() {
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Intent outIntent = new Intent();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Milestone B-1: use the outIntent and store the chosen brand and the chosen diameter as extras
                //  and after which, use setResult with ACTION_OK and the intent as arguments
                String brand;
                double diameter;

                Intent outIntent = new Intent();
                outIntent.putExtra(EXTRA_RESULT, brand);
                outIntent.putExtra(EXTRA_RESULT, diameter);
                setResult(RESULT_OK, outIntent);
                finish();
            }
        });

    }
}
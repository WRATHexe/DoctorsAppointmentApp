package cse489.project.doctorsappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class details extends AppCompatActivity {
    private ImageView home, appointmentBtn, history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        home = findViewById(R.id.home);
        history = findViewById(R.id.history);
        appointmentBtn = findViewById(R.id.appointmentBtn);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(details.this, Homepage.class);
                startActivity(i);
                finish();

            }
        });
        appointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(details.this, AppontmentForm.class);
                startActivity(i);

            }
        });
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(details.this, History.class);
                startActivity(i);

            }
        });
    }
}
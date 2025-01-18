package cse489.project.doctorsappointmentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AppointmentDetails extends AppCompatActivity {
  TextView name,phone,age,time,date,address,gender,email;
  ImageView home,appointmentBtn,history;

  Button update;
  FirebaseFirestore db;
  FirebaseAuth mAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_appointment_details);
    name = findViewById(R.id.name);
    db = FirebaseFirestore.getInstance();
    mAuth=FirebaseAuth.getInstance();
    phone = findViewById(R.id.phone);
    age = findViewById(R.id.age);
    time = findViewById(R.id.time);
    address = findViewById(R.id.address);
    date = findViewById(R.id.date);
    gender = findViewById(R.id.gender);
    email = findViewById(R.id.email);
    update = findViewById(R.id.update);
    home=findViewById(R.id.home);
    appointmentBtn=findViewById(R.id.appointmentBtn);
    history=findViewById(R.id.history);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    String Name = extras.getString("name");
    String Phone = extras.getString("phone");
    String Age = extras.getString("age");
    String Time = extras.getString("time");
    String Address = extras.getString("address");
    String Date = extras.getString("date");
    String documentId = extras.getString("documentId");
    String p_id = extras.getString("p_id");
    String Gender = extras.getString("gender");
    String Email = extras.getString("email");

    name.setText(Name);
    phone.setText(Phone);
    age.setText(Age);
    time.setText(Time);
    address.setText(Address);
    date.setText(Date);
    gender.setText(Gender);
    email.setText(Email);
    home.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(AppointmentDetails.this, Homepage.class);
        startActivity(i);
        finish();

      }
    });
    appointmentBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(AppointmentDetails.this, AppontmentForm.class);
        startActivity(i);

      }
    });
    history.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent i = new Intent(AppointmentDetails.this, History.class);
        startActivity(i);

      }
    });

    update.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent updateIntent = new Intent(AppointmentDetails.this, UpdateFrom.class);
        Bundle b = new Bundle();
        b.putString("name", Name);
        b.putString("phone", Phone);
        b.putString("age", Age);
        b.putString("address", Address);
        b.putString("date", Date);
        b.putString("time",Time);
        b.putString("documentId", documentId);
        b.putString("p_id", p_id);
        updateIntent.putExtras(b);
        startActivity(updateIntent);

      }
    });


  }
}
package cse489.project.doctorsappointmentapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateFrom extends AppCompatActivity {
    private EditText name, phone, age, address;
    private ImageView home, appointmentBtn, history;
    private TextView date;
    private TextView time;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button update;
    private String documentId;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_from);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        time = findViewById(R.id.time);
//    home=findViewById(R.id.home);
//    appointmentBtn=findViewById(R.id.appointmentBtn);
//    history=findViewById(R.id.history);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        address = findViewById(R.id.address);
        date = findViewById(R.id.date);
        update = findViewById(R.id.update);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String Name = extras.getString("name");
        String Phone = extras.getString("phone");
        String Age = extras.getString("age");
        String Address = extras.getString("address");
        String Time = extras.getString("time");
        documentId = extras.getString("documentId");
        String Date = extras.getString("date");
        name.setText(Name);
        phone.setText(Phone);
        age.setText(Age);
        address.setText(Address);
        time.setText(Time);
        date.setText(Date);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    String Name = name.getText().toString();
                    String Phone = phone.getText().toString();
                    String Age = age.getText().toString();
                    String Address = address.getText().toString();
                    checkAppointmentExistence(documentId, Name, Phone, Age, Address);
                }
            }
        });
//    home.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//        Intent i = new Intent(UpdateFrom.this, Homepage.class);
//        startActivity(i);
//        finish();
//
//      }
//    });
//    appointmentBtn.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//        Intent i = new Intent(UpdateFrom.this, AppontmentForm.class);
//        startActivity(i);
//
//      }
//    });
//    history.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//
//        Intent i = new Intent(UpdateFrom.this, History.class);
//        startActivity(i);
//
//      }
//    });
    }

    private boolean validateFields() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Name cannot be empty");
            return false;
        }
        if (phone.getText().toString().isEmpty()) {
            phone.setError("Phone number cannot be empty");
            return false;
        }
        if (age.getText().toString().isEmpty()) {
            age.setError("Age cannot be empty");
            return false;
        }

        if (address.getText().toString().isEmpty()) {
            address.setError("Address cannot be empty");
            return false;
        }
        if (date.getText().toString().isEmpty()) {
            date.setError("date cannot be empty");
            return false;
        }


        return true;
    }

    private void checkAppointmentExistence(String DocumentId, String name, String phone, String age, String address) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection("appointments").document(DocumentId).update("name", name, "phone", phone, "age", age, "address", address).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(UpdateFrom.this, "Appointment updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UpdateFrom.this, Homepage.class);
                    startActivity(i);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UpdateFrom.this, "Failed to update appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            System.out.println("Error");
        }
    }

}
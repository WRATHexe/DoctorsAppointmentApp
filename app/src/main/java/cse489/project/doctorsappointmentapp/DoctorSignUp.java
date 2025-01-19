package cse489.project.doctorsappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DoctorSignUp extends AppCompatActivity {

    private EditText name;
    private Spinner doctorSpecialtySpinner;
    private TextView loginNow;
    private Button submitButton;
    private String selectedSpecialty = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_sign_up);

        // Initialize Spinner and Button
        name = findViewById(R.id.name);
        doctorSpecialtySpinner = findViewById(R.id.doctorSpecialty);
        submitButton = findViewById(R.id.signupBtn);
        loginNow = findViewById(R.id.loginNow);
        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DoctorSignUp.this, DoctorLogin.class));
            }
        });
        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.doctor_specialties, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doctorSpecialtySpinner.setAdapter(adapter);

        // Capture selected item
        doctorSpecialtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpecialty = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSpecialty = ""; // Reset specialty when nothing is selected
            }
        });

        // Set up Button Click Listener
        submitButton.setOnClickListener(v -> {
            String doctorName = name.getText().toString().trim();
            if (validateInputs(doctorName, selectedSpecialty)) {
                saveSpecialtyToFirebase(doctorName, selectedSpecialty);
            }
        });
    }

    private boolean validateInputs(String doctorName, String specialty) {
        if (doctorName.isEmpty()) {
            name.setError("Name cannot be empty");
            name.requestFocus();
            return false;
        }

        if (specialty.isEmpty() || specialty.equals("Select Specialty")) { // Adjust based on default spinner text
            Toast.makeText(this, "Please select a specialty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveSpecialtyToFirebase(String doctorName, String specialty) {
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to store the doctor data
        Map<String, Object> doctorData = new HashMap<>();
        doctorData.put("doctorName", doctorName);
        doctorData.put("specialty", specialty);

        // Add the data to a collection named "doctors"
        db.collection("doctors").add(doctorData).addOnSuccessListener(documentReference -> {
            // Data added successfully
            System.out.println("Doctor added with ID: " + documentReference.getId());
            Toast.makeText(this, "You have been successfully listed", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DoctorSignUp.this, LogInActivity.class));
        }).addOnFailureListener(e -> {
            // Error occurred while adding data
            System.err.println("Error adding doctor: " + e.getMessage());
            Toast.makeText(this, "There was an error during data entry", Toast.LENGTH_SHORT).show();
        });
    }
}

package cse489.project.doctorsappointmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DoctorLogin extends AppCompatActivity {

    private EditText email;
    private Button loginBtn;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);

        email = findViewById(R.id.email);
        loginBtn = findViewById(R.id.loginBtn);
        progressbar = findViewById(R.id.progressBar);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Set onClickListener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doctorEmail = email.getText().toString().trim();

                // Check if input is empty
                if (TextUtils.isEmpty(doctorEmail)) {
                    Toast.makeText(DoctorLogin.this, "Please enter your Name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show progress bar
                progressbar.setVisibility(View.VISIBLE);

                // Query Firestore to check if the doctorName exists
                db.collection("doctors") // Replace "doctors" with your collection name
                        .whereEqualTo("doctorName", doctorEmail)
                        .get()
                        .addOnCompleteListener(task -> {
                            progressbar.setVisibility(View.GONE); // Hide progress bar

                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    // Doctor found
                                    Toast.makeText(DoctorLogin.this, "Login successful "+ document.getId(), Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(DoctorLogin.this, DoctorHomepage.class);
                                    i.putExtra("DOCTOR-ID", document.getId());
                                    i.putExtra("DOCTOR-NAME", document.getString("doctorName"));
                                    startActivity(i);
                                    finish();
                                } else {
                                    // Doctor not found
                                    Toast.makeText(DoctorLogin.this, "Doctor not found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Error in query
                                Toast.makeText(DoctorLogin.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}

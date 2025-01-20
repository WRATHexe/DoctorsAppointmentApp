package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Homepage extends AppCompatActivity {
    private TextView empty, Name;
    private ImageView home, appointmentBtn, history, Logout, userAvatar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private LinearLayout appointmentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize views
        initializeViews();

        // Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Check user authentication
        checkUserAuthentication();

        // Set up RecyclerView for doctors
        setupDoctorRecyclerView();

        // Display user appointments
        displayUserAppointments();

        // Handle navigation and button clicks
        handleNavigationClicks();
    }

    private void initializeViews() {
        Name = findViewById(R.id.name);
        Logout = findViewById(R.id.logout);
        //home = findViewById(R.id.home);
        userAvatar = findViewById(R.id.userAvatar);
        //appointmentBtn = findViewById(R.id.appointmentBtn);
        history = findViewById(R.id.history);
        //empty = findViewById(R.id.empty);
        appointmentsLayout = findViewById(R.id.appointments_layout);

    }

    private void checkUserAuthentication() {
        if (user == null) {
            Intent i = new Intent(Homepage.this, LogInActivity.class);
            startActivity(i);
            finish();
        } else {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("name");
                    Name.setText(userName);
                }
            }).addOnFailureListener(e -> Log.e("Firestore Error", "Failed to fetch user data", e));
        }
    }

    private void setupDoctorRecyclerView() {
        List<Doctor> doctorList = new ArrayList<>();
        DoctorAdapter adapter = new DoctorAdapter(doctorList, new DoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(int position) {
                Doctor clickedDoctor = doctorList.get(position);
                Toast.makeText(Homepage.this, "Clicked: " + clickedDoctor.getDocumentId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onViewDetailsClick(int position) {
                Doctor clickedDoctor = doctorList.get(position);
                //Toast.makeText(Homepage.this, "View Details for: " + clickedDoctor.getDocumentId(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Homepage.this, AppontmentForm.class);
                i.putExtra("DOCTOR-ID", clickedDoctor.getDocumentId() );
                i.putExtra("DOCTOR-NAME", clickedDoctor.getName());
                i.putExtra("DOCTOR-SPECIALTY", clickedDoctor.getSpecialty());
                startActivity(i);
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Query Firestore for doctor data
        db.collection("doctors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        doctorList.clear(); // Clear the list to prevent duplicates

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String name = document.getString("doctorName");
                            String specialty = document.getString("specialty");
                            int imageResId = R.drawable.doc; // Placeholder for dynamic image handling

                            doctorList.add(new Doctor(documentId, name, specialty, imageResId));
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayUserAppointments() {
        db.collection("appointments")
                .whereEqualTo("p_id", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Calendar currentCalendar = Calendar.getInstance();
                        currentCalendar.set(Calendar.SECOND, 0);
                        currentCalendar.set(Calendar.MILLISECOND, 0);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentDate = document.getString("date");
                            String appointmentTimeRange = document.getString("time");
                            String value = document.getString("passed");

                            if (appointmentTimeRange != null && "0".equals(value)) {
                                try {
                                    String[] timeSplit = appointmentTimeRange.split("-");
                                    String startTime = timeSplit[0].trim();
                                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                                    String combinedDateTime = appointmentDate + " " + startTime + " PM";
                                    Calendar storedCalendar = Calendar.getInstance();
                                    storedCalendar.setTime(dateTimeFormat.parse(combinedDateTime));
                                    storedCalendar.set(Calendar.SECOND, 0);
                                    storedCalendar.set(Calendar.MILLISECOND, 0);

                                    if (storedCalendar.before(currentCalendar)) {
                                        document.getReference().update("passed", "1");
                                        Log.d("Appointment", "Updated passed value");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        displayAppointmentsWithZeroValue();
                    } else {
                        Log.d("Appointment", "Task unsuccessful: " + task.getException());
                    }
                });
    }

    private void displayAppointmentsWithZeroValue() {
        db.collection("appointments")
                .whereEqualTo("p_id", user.getUid())
                .whereEqualTo("passed", "0")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String appointmentDate = document.getString("date");
                            String appointmentTime = document.getString("time");
                            String name = document.getString("name");
                            String phone = document.getString("phone");
                            String age = document.getString("age");
                            String address = document.getString("address");
                            String gender = document.getString("gender");
                            String email = document.getString("email");
                            String d_id = document.getString("d_id"); // Doctor ID
                            String documentId = document.getId();
                            String passed = document.getString("passed");

                            // Inflate the appointment card layout
                            View appointmentCard = getLayoutInflater().inflate(R.layout.layout_appointmentlist, null);
                            TextView dateTextView = appointmentCard.findViewById(R.id.datelist);
                            TextView timeTextView = appointmentCard.findViewById(R.id.timelist);
                            TextView nameTextView = appointmentCard.findViewById(R.id.name);
                            TextView doctorInfoTV = appointmentCard.findViewById(R.id.doctorInfoTV);

                            // Populate appointment details
                            dateTextView.setText(appointmentDate);
                            timeTextView.setText(appointmentTime + " PM");
                            nameTextView.setText(name);

                            // Fetch and display doctor information based on d_id
                            if (d_id != null && !d_id.isEmpty()) {
                                db.collection("doctors").document(d_id).get()
                                        .addOnSuccessListener(doctorSnapshot -> {
                                            if (doctorSnapshot.exists()) {
                                                String doc_name = doctorSnapshot.getString("doctorName");
                                                String doc_sp = doctorSnapshot.getString("specialty");

                                                // Update doctor information TextView
                                                doctorInfoTV.setText("You have an appointment with Dr. " + doc_name +
                                                        ", " + doc_sp + ".");
                                            } else {
                                                doctorInfoTV.setText("Doctor information could not be retrieved.");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            doctorInfoTV.setText("Error retrieving doctor information.");
                                            e.printStackTrace();
                                        });
                            } else {
                                doctorInfoTV.setText("Doctor ID is missing in the appointment.");
                            }

                            // Add the card to the appointments layout
                            appointmentsLayout.addView(appointmentCard);

                            // Set long click listener for canceling appointment
                            appointmentCard.setOnLongClickListener(v -> {
                                showCancelDialog(this, appointmentDate, appointmentTime);
                                return true;
                            });

                            // Set click listener to view appointment details
                            appointmentCard.setOnClickListener(view -> {
                                Intent detailsIntent = new Intent(Homepage.this, AppointmentDetails.class);
                                detailsIntent.putExtra("date", appointmentDate);
                                detailsIntent.putExtra("time", appointmentTime);
                                detailsIntent.putExtra("name", name);
                                detailsIntent.putExtra("phone", phone);
                                detailsIntent.putExtra("age", age);
                                detailsIntent.putExtra("address", address);
                                detailsIntent.putExtra("gender", gender);
                                detailsIntent.putExtra("email", email);
                                detailsIntent.putExtra("d_id", d_id);
                                startActivity(detailsIntent);
                            });
                        }
                    } else {
                        // No appointments found
                        empty.setText("No Upcoming Appointments");
                    }
                });
    }

    private void handleNavigationClicks() {
        Logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Homepage.this, LogInActivity.class));
            finish();
        });

        userAvatar.setOnClickListener(view -> startActivity(new Intent(Homepage.this, PasswordChange.class)));

//        home.setOnClickListener(view -> {
//            startActivity(new Intent(Homepage.this, Homepage.class));
//            finish();
//        });

        //appointmentBtn.setOnClickListener(view -> startActivity(new Intent(Homepage.this, AppontmentForm.class)));

        history.setOnClickListener(view -> startActivity(new Intent(Homepage.this, History.class)));
    }

    private void showCancelDialog(final Context context, final String date, final String time) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.cancel_appointment);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> db.collection("appointments")
                .whereEqualTo("date", date)
                .whereEqualTo("time", time)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Appointment Cancelled Successfully", Toast.LENGTH_SHORT).show();
                                        recreate();
                                    })
                                    .addOnFailureListener(e -> dialog.dismiss());
                        }
                    }
                }));
        dialog.show();
    }
}

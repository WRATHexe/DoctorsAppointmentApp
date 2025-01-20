package cse489.project.doctorsappointmentapp;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DoctorHomepage extends AppCompatActivity {

    private TextView empty, name;
    private ImageView Logout, userAvatar;
    private FirebaseFirestore db;
    private LinearLayout appointmentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_homepage);

        // Initialize UI components
        // empty = findViewById(R.id.empty);
        Logout = findViewById(R.id.logout);
        userAvatar = findViewById(R.id.userAvatar);
        appointmentsLayout = findViewById(R.id.appointments_layout);
        name = findViewById(R.id.name);


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get doctor ID from intent
        Intent i = getIntent();
        String d_id = i.getStringExtra("DOCTOR-ID");
        String d_name = i.getStringExtra("DOCTOR-NAME");
        name.setText("DR. "+ d_name);

        // Display user appointments
        displayUserAppointments(d_id);

        // Set logout functionality (optional)
        Logout.setOnClickListener(view -> {
            Toast.makeText(DoctorHomepage.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DoctorLogin.class));
            finish(); // Close the activity
        });
    }

    private void displayUserAppointments(String d_id) {
        db.collection("appointments")
                .whereEqualTo("d_id", d_id)
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

                            if (appointmentTimeRange != null) {
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
                                    } else if ("0".equals(value)) {
                                        // Add to active appointments display
                                        addAppointmentCard(document);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        Log.d("Appointment", "Task unsuccessful: " + task.getException());
                    }
                });
    }

    private void addAppointmentCard(QueryDocumentSnapshot document) {
        String appointmentDate = document.getString("date");
        String appointmentTime = document.getString("time");
        String name = document.getString("name");
        String d_id = document.getString("d_id");

        // Inflate the card layout
        View appointmentCard = getLayoutInflater().inflate(R.layout.layout_appointmentlist, null);
        TextView dateTextView = appointmentCard.findViewById(R.id.datelist);
        TextView timeTextView = appointmentCard.findViewById(R.id.timelist);
        TextView nameTextView = appointmentCard.findViewById(R.id.name);
        TextView doctorInfoTV = appointmentCard.findViewById(R.id.doctorInfoTV);

        // Populate appointment details
        dateTextView.setText(appointmentDate);
        timeTextView.setText(appointmentTime + " PM");
        nameTextView.setText(name);

        // Fetch doctor details
        db.collection("doctors").document(d_id).get()
                .addOnSuccessListener(doctorSnapshot -> {

                    if (doctorSnapshot.exists()) {
                        String docName = doctorSnapshot.getString("doctorName");
                        String docSpecialty = doctorSnapshot.getString("specialty");
                        doctorInfoTV.setText("Appointment with Dr. " + docName + ", " + docSpecialty);
                    } else {
                        doctorInfoTV.setText("Doctor info unavailable.");
                    }
                });

        // Add card to layout
        appointmentsLayout.addView(appointmentCard);

        // Set long click listener for canceling appointment
//        appointmentCard.setOnLongClickListener(v -> {
//            showCancelDialog(this, appointmentDate, appointmentTime);
//            return true;
//        });
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

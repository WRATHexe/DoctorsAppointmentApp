package cse489.project.doctorsappointmentapp;

import static android.content.ContentValues.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PreviewForm extends AppCompatActivity {
    private TextView name, phone, age, email, time, date, address, gender, doctorID;
    private Button submit, cancel;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_form);
        name = findViewById(R.id.name);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        phone = findViewById(R.id.phone);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        time = findViewById(R.id.time);
        address = findViewById(R.id.address);
        gender = findViewById(R.id.gender);
        date = findViewById(R.id.date);
        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        doctorID = findViewById(R.id.doctorID);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String Name = extras.getString("name");
        String Phone = extras.getString("phone");
        String Age = extras.getString("age");
        String Time = extras.getString("time");
        String Address = extras.getString("address");
        String Gender = extras.getString("gender");
        String Date = extras.getString("date");
        String d_id = extras.getString("d_id");
        String d_name = extras.getString("d_name");
        String d_sp = extras.getString("d_sp");
        doctorID.setText(d_name + " | " + d_sp);
        name.setText(Name);
        phone.setText(Phone);
        age.setText(Age);
        time.setText(Time);
        address.setText(Address);
        gender.setText(Gender);
        date.setText(Date);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            email.setText(currentUser.getEmail());
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, Object> appointment = new HashMap<>();
                    appointment.put("name", Name);
                    appointment.put("p_id", currentUser.getUid());
                    appointment.put("email", currentUser.getEmail());
                    appointment.put("phone", Phone);
                    appointment.put("age", Age);
                    appointment.put("time", Time);
                    appointment.put("date", Date);
                    appointment.put("address", Address);
                    appointment.put("gender", Gender);
                    appointment.put("d_id", d_id);
                    appointment.put("passed", "0");

                    db.collection("appointments").add(appointment).addOnSuccessListener(documentReference -> {
                        Toast.makeText(PreviewForm.this, "New Appointment Added on " + Date, Toast.LENGTH_SHORT).show();

                        // Schedule Notification
                        scheduleNotification(Date, Time);

                        Intent i = new Intent(PreviewForm.this, Homepage.class);
                        startActivity(i);
                        finish();
                    }).addOnFailureListener(e -> Log.w(TAG, "Error adding appointment", e));
                }
            });

//            submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Map<String, Object> appointment = new HashMap<>();
//                    appointment.put("name", Name);
//                    appointment.put("p_id", currentUser.getUid());
//                    appointment.put("email", currentUser.getEmail());
//                    appointment.put("phone", Phone);
//                    appointment.put("age", Age);
//                    appointment.put("time", Time);
//                    appointment.put("date", Date);
//                    appointment.put("address", Address);
//                    appointment.put("gender", Gender);
//                    appointment.put("d_id", d_id);
//                    appointment.put("passed", "0");
//
//                    db.collection("appointments").add(appointment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(PreviewForm.this, "New Appointment Added on " + Date, Toast.LENGTH_SHORT).show();
//
//                            // Delay Notification by 10 seconds
//                            new android.os.Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    NotificationHelper.showNotification(
//                                            PreviewForm.this,
//                                            "Appointment Scheduled",
//                                            "Your appointment has been successfully scheduled on " + Date + " at " + Time
//                                    );
//                                }
//                            }, 10000); // Delay in milliseconds (10 seconds)
//
//                            Intent i = new Intent(PreviewForm.this, Homepage.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding appointment", e);
//                        }
//                    });
//                }
//            });


//            submit.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Map<String, Object> appointment = new HashMap<>();
//                    appointment.put("name", Name);
//                    appointment.put("p_id", currentUser.getUid());
//                    appointment.put("email", currentUser.getEmail());
//                    appointment.put("phone", Phone);
//                    appointment.put("age", Age);
//                    appointment.put("time", Time);
//                    appointment.put("date", Date);
//                    appointment.put("address", Address);
//                    appointment.put("gender", Gender);
//                    appointment.put("d_id", d_id);
//                    appointment.put("passed", "0");
//
//                    db.collection("appointments").add(appointment).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(PreviewForm.this, "New Appointment Added on " + Date, Toast.LENGTH_SHORT).show();
//
//                            // Show Notification Instantly
//                            NotificationHelper.showNotification(
//                                    PreviewForm.this,
//                                    "Appointment Scheduled",
//                                    "Your appointment has been successfully scheduled on " + Date + " at " + Time
//                            );
//
//                            Intent i = new Intent(PreviewForm.this, Homepage.class);
//                            startActivity(i);
//                            finish();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error adding appointment", e);
//                        }
//                    });
//                }
//            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(PreviewForm.this, AppontmentForm.class);
                    startActivity(i);
                    finish();

                }

            });
        } else {
            Toast.makeText(PreviewForm.this, "User is Empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void scheduleNotification(String date, String time) {
        try {
            // Parse date and time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date appointmentDate = sdf.parse(date + " " + time);

            if (appointmentDate != null) {
                long triggerAtMillis = System.currentTimeMillis() + 3600000 ;

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(this, AppointmentReminderReceiver.class);
                intent.putExtra("appointment_time", time);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
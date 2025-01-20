package cse489.project.doctorsappointmentapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppointmentReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String appointmentTime = intent.getStringExtra("appointment_time");
        NotificationHelper.showNotification(
                context,
                "Appointment Reminder",
                "Your appointment is in 1 Hour at " + appointmentTime
        );
    }
}

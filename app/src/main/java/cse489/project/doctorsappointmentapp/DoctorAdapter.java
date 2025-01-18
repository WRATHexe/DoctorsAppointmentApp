package cse489.project.doctorsappointmentapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private List<Doctor> doctorList;
    private OnDoctorClickListener listener;

    // Define a custom interface for click events
    public interface OnDoctorClickListener {
        void onDoctorClick(int position);
        void onViewDetailsClick(int position);
    }

    // Constructor
    public DoctorAdapter(List<Doctor> doctorList, OnDoctorClickListener listener) {
        this.doctorList = doctorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doctor_card, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        // Bind data to views
        holder.doctorName.setText(doctor.getName());
        holder.doctorSpecialty.setText(doctor.getSpecialty());
        holder.doctorImage.setImageResource(doctor.getImageResourceId()); // Replace with Glide/Picasso for URL images

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(position);
            }
        });

        holder.viewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, doctorSpecialty;
        ImageView doctorImage;
        Button viewDetails;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctorName);
            doctorSpecialty = itemView.findViewById(R.id.doctorSpecialty);
            doctorImage = itemView.findViewById(R.id.doctorImage);
            viewDetails = itemView.findViewById(R.id.viewDetails);
        }
    }
}

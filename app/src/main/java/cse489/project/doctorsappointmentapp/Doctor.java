package cse489.project.doctorsappointmentapp;

public class Doctor {
    private String name;
    private String specialty;
    private int imageResourceId; // For demo purposes; replace with URL for real data.

    public Doctor(String name, String specialty, int imageResourceId) {
        this.name = name;
        this.specialty = specialty;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}

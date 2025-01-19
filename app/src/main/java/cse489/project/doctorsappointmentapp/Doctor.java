package cse489.project.doctorsappointmentapp;

public class Doctor {
    private final String documentId; // Firestore Document ID
    private final String name;
    private final String specialty;
    private final int imageResourceId; // For demo purposes; replace with URL for real data.

    // Constructor
    public Doctor(String documentId, String name, String specialty, int imageResourceId) {
        this.documentId = documentId;
        this.name = name;
        this.specialty = specialty;
        this.imageResourceId = imageResourceId;
    }

    // Getters
    public String getDocumentId() {
        return documentId;
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

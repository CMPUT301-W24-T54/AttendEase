package com.example.attendease;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Database {
    private static Database instance = null;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    private final CollectionReference attendeesRef;
    private final CollectionReference organizersRef;
    private final CollectionReference adminRef;
    private final CollectionReference checkInsRef;
    private final CollectionReference signInsRef;
    private final CollectionReference eventsRef;
    private final CollectionReference imagesRef;
    private final CollectionReference notificationsRef;

    private Database() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        attendeesRef = db.collection("attendees");
        organizersRef = db.collection("organizers");
        adminRef = db.collection("administrators");
        checkInsRef = db.collection("checkIns");
        signInsRef = db.collection("signIns");
        eventsRef = db.collection("events");
        imagesRef = db.collection("images");
        notificationsRef = db.collection("notifications");
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public StorageReference getStorageRef() {
        return storageRef;
    }

    public CollectionReference getAttendeesRef() {
        return attendeesRef;
    }

    public CollectionReference getOrganizersRef() {
        return organizersRef;
    }

    public CollectionReference getAdminRef() {
        return adminRef;
    }

    public CollectionReference getCheckInsRef() {
        return checkInsRef;
    }

    public CollectionReference getSignInsRef() {
        return signInsRef;
    }

    public CollectionReference getEventsRef() {
        return eventsRef;
    }

    public CollectionReference getImagesRef() {
        return imagesRef;
    }

    public CollectionReference getNotificationsRef() {
        return notificationsRef;
    }
}

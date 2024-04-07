package com.example.attendease;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Singleton;

/**
 * This class represents a Singleton Firestore Database instance
 */
@Singleton
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

    /**
     * Private constructor to prevent instantiation from outside.
     * Initializes the Firebase Firestore database and storage references.
     */
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

    /**
     * Provides a singleton instance of the Database class.
     * Ensures only one instance of Database exists throughout the application.
     * @return Singleton instance of the Database class.
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Retrieves the Firebase Firestore instance.
     * @return Firebase Firestore instance.
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * Retrieves the Firebase Storage instance.
     * @return Firebase Storage instance.
     */
    public FirebaseStorage getStorage() {
        return storage;
    }

    /**
     * Retrieves the root StorageReference for Firebase Storage.
     * @return Root StorageReference for Firebase Storage.
     */
    public StorageReference getStorageRef() {
        return storageRef;
    }

    /**
     * Retrieves the reference to the "attendees" collection in Firestore.
     * @return CollectionReference to the "attendees" collection.
     */
    public CollectionReference getAttendeesRef() {
        return attendeesRef;
    }

    /**
     * Retrieves the reference to the "organizers" collection in Firestore.
     * @return CollectionReference to the "organizers" collection.
     */
    public CollectionReference getOrganizersRef() {
        return organizersRef;
    }

    /**
     * Retrieves the reference to the "administrators" collection in Firestore.
     * @return CollectionReference to the "administrators" collection.
     */
    public CollectionReference getAdminRef() {
        return adminRef;
    }

    /**
     * Retrieves the reference to the "checkIns" collection in Firestore.
     * @return CollectionReference to the "checkIns" collection.
     */
    public CollectionReference getCheckInsRef() {
        return checkInsRef;
    }

    /**
     * Retrieves the reference to the "signIns" collection in Firestore.
     * @return CollectionReference to the "signIns" collection.
     */
    public CollectionReference getSignInsRef() {
        return signInsRef;
    }

    /**
     * Retrieves the reference to the "events" collection in Firestore.
     * @return CollectionReference to the "events" collection.
     */
    public CollectionReference getEventsRef() {
        return eventsRef;
    }

    /**
     * Retrieves the reference to the "images" collection in Firestore.
     * @return CollectionReference to the "images" collection.
     */
    public CollectionReference getImagesRef() {
        return imagesRef;
    }

    /**
     * Retrieves the reference to the "notifications" collection in Firestore.
     * @return CollectionReference to the "notifications" collection.
     */
    public CollectionReference getNotificationsRef() {
        return notificationsRef;
    }
}

package com.example.database.firebase;

import com.example.database.student.Students;
import com.google.firebase.database.FirebaseDatabase;

public final class FirebaseDatabaseHelper {

    private FirebaseDatabaseHelper() {
    }

    /**
     * Firebase Database instance
     */
    private static FirebaseDatabase database = null;

    /**
     * Initializes Firebase database instance and students API
     */
    public static void initialize() {
        if (database != null) return;

        // get firebase database instance
        database = FirebaseDatabase.getInstance();

        // initialize students api
        Students.initialize(database);
    }

    /**
     * Destroys Firebase database instance and students API
     */
    public static void destroy() {
        if (database == null) return;

        // remove firebase database instance
        database = null;

        // destroy students api
        Students.destroy();
    }
}

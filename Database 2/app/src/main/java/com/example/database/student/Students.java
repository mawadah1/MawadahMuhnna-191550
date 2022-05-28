package com.example.database.student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public final class Students {

    /**
     * Students changes callback
     */
    public interface OnStudentsChangeListener {
        void onStudentsChange();

        void onStudentsError(String message);
    }

    /**
     * Listener for students changes
     */
    private static OnStudentsChangeListener onStudentsChangeListener = null;

    private Students() {
    }

    /**
     * Firebase database students ref
     */
    private static DatabaseReference studentsRef = null;

    /**
     * Students map
     */
    private static Map<String, Student> studentsMap = null;

    /**
     * Listener for firebase database students changes
     */
    private static final ChildEventListener studentsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (studentsMap == null || !snapshot.exists()) return;

            // get student data
            String id = snapshot.getKey();
            StudentEntry entry = snapshot.getValue(StudentEntry.class);
            if (id == null || entry == null) throw new IllegalArgumentException("No student entry");

            // parse student
            Student student;
            try {
                student = new Student(id, entry);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Couldn't parse student");
            }

            // add student to map
            studentsMap.put(id, student);
            if (onStudentsChangeListener != null) {
                onStudentsChangeListener.onStudentsChange();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            if (studentsMap == null || !snapshot.exists()) return;

            // get student data
            String id = snapshot.getKey();
            StudentEntry entry = snapshot.getValue(StudentEntry.class);
            if (id == null || entry == null) throw new IllegalArgumentException("No student entry");

            // parse student
            Student student;
            try {
                student = new Student(id, entry);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Couldn't parse student");
            }

            // update student in map
            studentsMap.remove(id);
            studentsMap.put(id, student);
            if (onStudentsChangeListener != null) {
                onStudentsChangeListener.onStudentsChange();
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            if (studentsMap == null || !snapshot.exists()) return;

            // get student data
            String id = snapshot.getKey();

            // remove student from map
            studentsMap.remove(id);
            if (onStudentsChangeListener != null) {
                onStudentsChangeListener.onStudentsChange();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            if (onStudentsChangeListener != null) {
                onStudentsChangeListener.onStudentsError(error.getMessage());
            }
        }
    };

    /**
     * Initializes students api
     */
    public static void initialize(FirebaseDatabase database) {
        if (studentsRef != null) return;

        // initialize student map
        studentsMap = new HashMap<>();

        // get students reference
        studentsRef = database.getReference("students");

        // start listening for students list changes
        studentsRef.addChildEventListener(studentsEventListener);
    }

    /**
     * Sets listener for students changes
     */
    public static void setOnStudentsChangeListener(OnStudentsChangeListener listener) {
        // attach students change listener
        onStudentsChangeListener = listener;
    }

    /**
     * Removes listener for students changes
     */
    public static void removeOnStudentsChangeListener() {
        // detach students change listener
        onStudentsChangeListener = null;
    }

    /**
     * Inserts student into firebase database
     */
    @Nullable
    public static Task<Void> putStudent(Student student) {
        if (studentsRef == null) return null;

        // get student data
        String id = student.id;
        StudentEntry entry = student.toStudentEntry();
        if (entry.isDirty()) return null;

        // add student to database
        DatabaseReference studentRef = studentsRef.child(id);
        return studentRef.setValue(entry);
    }

    /**
     * Updates student in firebase database
     */
    @Nullable
    public static Task<Void> updateStudent(Student student) {
        if (studentsRef == null) return null;

        // get student updates
        String id = student.id;
        HashMap<String, Object> updates = student.toMap();
        if (updates.isEmpty()) return null;

        // update student in database
        DatabaseReference studentRef = studentsRef.child(id);
        return studentRef.updateChildren(updates);
    }

    /**
     * Removes student from firebase database
     */
    @Nullable
    public static Task<Void> removeStudent(@NonNull String id) {
        if (studentsRef == null) return null;

        // remove student from database
        DatabaseReference studentRef = studentsRef.child(id);
        return studentRef.removeValue();
    }

    /**
     * Gets student from firebase database
     */
    @Nullable
    public static Task<Student> getStudentFromDatabase(@NonNull String id) {
        if (studentsRef == null) return null;

        // get student from database
        return studentsRef.child(id).get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getValue(Student.class);
            } else {
                return null;
            }
        });
    }

    /**
     * Gets all students from firebase database
     */
    @Nullable
    public static Task<Student[]> getAllStudentsFromDatabase() {
        if (studentsRef == null) return null;

        // get all students from database
        return studentsRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                return task.getResult().getValue(Student[].class);
            } else {
                return null;
            }
        });
    }

    /**
     * Gets student from students map
     */
    @Nullable
    public static Student getStudent(@NonNull String id) {
        if (studentsMap == null) return null;

        // get student from map
        return studentsMap.get(id);
    }

    /**
     * Gets all students from students map
     */
    @Nullable
    public static Student[] getAllStudents() {
        if (studentsMap == null) return null;

        // get all students from map
        return studentsMap.values().toArray(new Student[0]);
    }

    /**
     * Destroys students api
     */
    public static void destroy() {
        if (studentsRef == null) return;

        // stop listening for students list changes
        studentsRef.removeEventListener(studentsEventListener);

        // remove student reference
        studentsRef = null;

        // destroy student map
        studentsMap = null;
    }
}

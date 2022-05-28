package com.example.database.sqli;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.database.sqli.StudentsContract.StudentDbEntry;
import com.example.database.student.Student;
import com.example.database.student.StudentEntry;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class StudentsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Students.db";

    /**
     * SQL for creating students table
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StudentDbEntry.TABLE_NAME + " (" +
                    StudentDbEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    StudentDbEntry.COLUMN_NAME_NAME + " TEXT," +
                    StudentDbEntry.COLUMN_NAME_SURNAME + " TEXT," +
                    StudentDbEntry.COLUMN_NAME_FATHER_NAME + " TEXT," +
                    StudentDbEntry.COLUMN_NAME_NATIONAL_ID + " TEXT," +
                    StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH + " INTEGER," +
                    StudentDbEntry.COLUMN_NAME_GENDER + " TEXT)";

    /**
     * SQL for deleting students table
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StudentDbEntry.TABLE_NAME;

    /**
     * Students changes callback
     */
    public interface OnStudentsChangeListener {
        void onStudentsChange();
    }

    /**
     * Listener for students changes
     */
    private OnStudentsChangeListener onStudentsChangeListener = null;

    public StudentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Sets listener for students changes
     */
    public void setOnStudentsChangeListener(OnStudentsChangeListener listener) {
        // attach students change listener
        onStudentsChangeListener = listener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Inserts student into SQLi database
     */
    public boolean putStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();

        // get student data
        String id = student.id;
        StudentEntry entry = student.toStudentEntry();
        if (entry.isDirty()) return false;

        ContentValues values = new ContentValues();
        values.put(StudentDbEntry.COLUMN_NAME_ID, id);
        values.put(StudentDbEntry.COLUMN_NAME_NAME, entry.name);
        values.put(StudentDbEntry.COLUMN_NAME_SURNAME, entry.surName);
        values.put(StudentDbEntry.COLUMN_NAME_FATHER_NAME, entry.fatherName);
        values.put(StudentDbEntry.COLUMN_NAME_NATIONAL_ID, entry.nationalId);
        values.put(StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH, entry.dateOfBirth);
        values.put(StudentDbEntry.COLUMN_NAME_GENDER, entry.gender);

        // insert student to SQLi database
        long row = db.insert(StudentDbEntry.TABLE_NAME, null, values);
        if (row != -1 && onStudentsChangeListener != null) {
            onStudentsChangeListener.onStudentsChange();
        }
        return row != -1;
    }

    /**
     * Updates student in SQLi database
     */
    public boolean updateStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();

        // get student updates
        String id = student.id;
        HashMap<String, Object> updates = student.toMap();
        if (updates.isEmpty()) return false;

        ContentValues values = new ContentValues();
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    values.put(StudentDbEntry.COLUMN_NAME_NAME, (String) entry.getValue());
                    break;
                case "surName":
                    values.put(StudentDbEntry.COLUMN_NAME_SURNAME, (String) entry.getValue());
                    break;
                case "fatherName":
                    values.put(StudentDbEntry.COLUMN_NAME_FATHER_NAME, (String) entry.getValue());
                    break;
                case "nationalId":
                    values.put(StudentDbEntry.COLUMN_NAME_NATIONAL_ID, (String) entry.getValue());
                    break;
                case "dateOfBirth":
                    values.put(StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH, (Long) entry.getValue());
                    break;
                case "gender":
                    values.put(StudentDbEntry.COLUMN_NAME_GENDER, (String) entry.getValue());
                    break;
                default:
                    return false;
            }
        }

        String selection = StudentDbEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id};

        // update student in SQLi database
        int count = db.update(StudentDbEntry.TABLE_NAME, values, selection, selectionArgs);
        if (count > 0 && onStudentsChangeListener != null) {
            onStudentsChangeListener.onStudentsChange();
        }
        return count > 0;
    }

    /**
     * Removes student from SQLi database
     */
    public boolean removeStudent(@NonNull String id) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = StudentDbEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id};

        // remove student from SQLi database
        int count = db.delete(StudentDbEntry.TABLE_NAME, selection, selectionArgs);
        if (count > 0 && onStudentsChangeListener != null) {
            onStudentsChangeListener.onStudentsChange();
        }
        return count > 0;
    }

    /**
     * Gets student from SQLi database
     */
    @Nullable
    public Student getStudent(@NonNull String id) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                StudentDbEntry.COLUMN_NAME_NAME,
                StudentDbEntry.COLUMN_NAME_SURNAME,
                StudentDbEntry.COLUMN_NAME_FATHER_NAME,
                StudentDbEntry.COLUMN_NAME_NATIONAL_ID,
                StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH,
                StudentDbEntry.COLUMN_NAME_GENDER,
        };

        String selection = StudentDbEntry.COLUMN_NAME_ID + " = ?";
        String[] selectionArgs = {id};

        // query student
        Cursor cursor = db.query(
                StudentDbEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            try {
                // get student from SQLi database
                int nameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_NAME);
                int surNameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_SURNAME);
                int fatherNameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_FATHER_NAME);
                int nationalIdIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_NATIONAL_ID);
                int dateOfBirthIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH);
                int genderIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_GENDER);
                String name = cursor.getString(nameIndex);
                String surName = cursor.getString(surNameIndex);
                String fatherName = cursor.getString(fatherNameIndex);
                String nationalId = cursor.getString(nationalIdIndex);
                Long dateOfBirth = cursor.getLong(dateOfBirthIndex);
                String gender = cursor.getString(genderIndex);
                cursor.close();

                // create student
                StudentEntry entry = new StudentEntry(name, surName, fatherName, nationalId, dateOfBirth, gender);
                return new Student(id, entry);
            } catch (IllegalArgumentException | ParseException e) {
                cursor.close();
                return null;
            }
        }

        return null;
    }

    /**
     * Gets all students from SQLi database
     */
    public Student[] getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                StudentDbEntry.COLUMN_NAME_ID,
                StudentDbEntry.COLUMN_NAME_NAME,
                StudentDbEntry.COLUMN_NAME_SURNAME,
                StudentDbEntry.COLUMN_NAME_FATHER_NAME,
                StudentDbEntry.COLUMN_NAME_NATIONAL_ID,
                StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH,
                StudentDbEntry.COLUMN_NAME_GENDER,
        };

        // query all students
        Cursor cursor = db.query(
                StudentDbEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Student> students = new ArrayList<>();
        while (cursor.moveToNext()) {
            try {
                // get student from SQLi database
                int idIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_ID);
                int nameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_NAME);
                int surNameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_SURNAME);
                int fatherNameIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_FATHER_NAME);
                int nationalIdIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_NATIONAL_ID);
                int dateOfBirthIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_DATE_OF_BIRTH);
                int genderIndex = cursor.getColumnIndexOrThrow(StudentDbEntry.COLUMN_NAME_GENDER);
                String id = cursor.getString(idIndex);
                String name = cursor.getString(nameIndex);
                String surName = cursor.getString(surNameIndex);
                String fatherName = cursor.getString(fatherNameIndex);
                String nationalId = cursor.getString(nationalIdIndex);
                Long dateOfBirth = cursor.getLong(dateOfBirthIndex);
                String gender = cursor.getString(genderIndex);

                // create student
                StudentEntry entry = new StudentEntry(name, surName, fatherName, nationalId, dateOfBirth, gender);
                students.add(new Student(id, entry));
            } catch (IllegalArgumentException | ParseException e) {
                cursor.close();
                return null;
            }
        }

        cursor.close();
        return students.toArray(new Student[0]);
    }
}
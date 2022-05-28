package com.example.database;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.student.Gender;
import com.example.database.student.Student;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentViewHolder extends RecyclerView.ViewHolder {

    /**
     * Student view click callback
     */
    public interface OnStudentClickListener {
        void onClick(String id);

        void onDelete(String id);
    }

    private String id;

    private final TextView idText;
    private final TextView nameText;
    private final TextView nationalText;
    private final TextView birthText;
    private final TextView genderText;

    public StudentViewHolder(@NonNull View view, OnStudentClickListener listener) {
        super(view);

        // get UI elements
        idText = view.findViewById(R.id.idText);
        nameText = view.findViewById(R.id.nameText);
        nationalText = view.findViewById(R.id.nationalText);
        birthText = view.findViewById(R.id.birthText);
        genderText = view.findViewById(R.id.genderText);

        // listen for delete button
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(id);
            }
        });

        // listen for view click
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(id);
            }
        });
    }

    /**
     * Gets full name
     */
    private static String formatName(String name, String surName, String fatherName) {
        return String.format("%s %s %s", name, fatherName, surName);
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    /**
     * Gets string from date
     */
    private static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Gets string from gender enum
     */
    private static String formatGender(Gender gender) {
        return Gender.toString(gender);
    }

    /**
     * Fills UI with student
     */
    public void fillStudent(Student student) {
        id = student.id;
        idText.setText(student.id);
        nameText.setText(formatName(student.name, student.surName, student.fatherName));
        nationalText.setText(student.nationalId);
        birthText.setText(formatDate(student.dateOfBirth));
        genderText.setText(formatGender(student.gender));
    }
}
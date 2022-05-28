package com.example.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.student.Student;
import com.example.database.student.Students;

import java.util.Arrays;

public class StudentsAdapter extends RecyclerView.Adapter<StudentViewHolder> {

    /**
     * Listener for student click
     */
    private StudentViewHolder.OnStudentClickListener onStudentClickListener = null;

    /**
     * Array of students to render
     */
    private Student[] localStudents;

    public StudentsAdapter() {
        // initialize local students array
        updateDataSet();
    }

    public void updateDataSet() {
        // get students from Firebase API
        Student[] students = Students.getAllStudents();
        if (students == null) return;

        // fill local students array
        localStudents = students;
        Arrays.sort(localStudents);
    }

    /**
     * Sets student on click listener
     */
    public void setOnStudentClickListener(StudentViewHolder.OnStudentClickListener listener) {
        onStudentClickListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create student view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_view_holder, parent, false);
        return new StudentViewHolder(view, onStudentClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        // fill student view with data
        holder.fillStudent(localStudents[position]);
    }

    @Override
    public int getItemCount() {
        // gets count of students
        return localStudents.length;
    }
}
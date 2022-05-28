package com.example.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.sqli.StudentsDbHelper;
import com.example.database.student.Student;

import java.util.Arrays;

public class SQLiStudentsAdapter extends RecyclerView.Adapter<StudentViewHolder> {

    /**
     * Listener for student click
     */
    private StudentViewHolder.OnStudentClickListener onStudentClickListener = null;

    /**
     * Array of students to render
     */
    private Student[] localStudents;

    public SQLiStudentsAdapter(StudentsDbHelper dbHelper) {
        // initialize local students array
        updateDataSet(dbHelper);
    }

    public void updateDataSet(StudentsDbHelper dbHelper) {
        // get students from Firebase API and fill local students array
        localStudents = dbHelper.getAllStudents();
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
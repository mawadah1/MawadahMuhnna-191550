package com.example.database;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.firebase.FirebaseDatabaseHelper;
import com.example.database.student.Students;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class ListStudentsActivity extends AppCompatActivity {

    /**
     * Shows toast with message
     */
    private void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        // initialize firebase database
        FirebaseDatabaseHelper.initialize();

        // create students adapter
        LinearLayoutManager manager = new LinearLayoutManager(this);
        StudentsAdapter adapter = new StudentsAdapter();
        adapter.setOnStudentClickListener(new StudentViewHolder.OnStudentClickListener() {
            @Override
            public void onClick(String id) {
                // open edit student activity
                Intent editStudentIntent = new Intent(ListStudentsActivity.this, EditStudentActivity.class);
                editStudentIntent.putExtra("id", id);
                startActivity(editStudentIntent);
            }

            @Override
            public void onDelete(String id) {
                // ask for deletion confirmation
                AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(ListStudentsActivity.this);
                deleteAlertBuilder.setTitle("Confirm Deletion")
                        .setMessage("Sure that you want to remove this student?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            // remove student from api
                            Task<Void> removeStudent = Students.removeStudent(id);
                            if (removeStudent == null) {
                                ShowToast("Couldn't remove student");
                                return;
                            }

                            // wait for remove task to complete
                            removeStudent.addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // removed student
                                    ShowToast("Removed student successfully");
                                } else {
                                    ShowToast(Objects.requireNonNull(task.getException()).getMessage());
                                }
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setCancelable(true);
                AlertDialog deleteAlert = deleteAlertBuilder.create();
                deleteAlert.show();
            }
        });

        // initialize students recycler view
        RecyclerView studentsRecycler = findViewById(R.id.studentsRecycler);
        studentsRecycler.setLayoutManager(manager);
        studentsRecycler.setAdapter(adapter);

        // add new student button
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent addStudentIntent = new Intent(ListStudentsActivity.this, AddStudentActivity.class);
            startActivity(addStudentIntent);
        });

        // change to SQLi database
        Button switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(v -> {
            Intent listSQLiStudentsIntent = new Intent(ListStudentsActivity.this, SQLiListStudentsActivity.class);
            startActivity(listSQLiStudentsIntent);
        });

        // listen for students changes
        Students.setOnStudentsChangeListener(new Students.OnStudentsChangeListener() {
            @Override
            public void onStudentsChange() {
                // update students adapter
                adapter.updateDataSet();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onStudentsError(String message) {
                ShowToast(message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // remove students changes listener
        Students.removeOnStudentsChangeListener();

        // destroy firebase database
        FirebaseDatabaseHelper.destroy();
    }
}
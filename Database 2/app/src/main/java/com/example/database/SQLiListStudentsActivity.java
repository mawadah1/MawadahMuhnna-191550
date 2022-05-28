package com.example.database;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.database.sqli.StudentsDbHelper;

public class SQLiListStudentsActivity extends AppCompatActivity {

    /**
     * Shows toast with message
     */
    private void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqli_list_students);

        // initialize SQLi database
        StudentsDbHelper dbHelper = new StudentsDbHelper(this);

        // create students adapter
        LinearLayoutManager manager = new LinearLayoutManager(this);
        SQLiStudentsAdapter adapter = new SQLiStudentsAdapter(dbHelper);
        adapter.setOnStudentClickListener(new StudentViewHolder.OnStudentClickListener() {
            @Override
            public void onClick(String id) {
                // open edit student activity
                Intent editStudentIntent = new Intent(SQLiListStudentsActivity.this, SQLiEditStudentActivity.class);
                editStudentIntent.putExtra("id", id);
                startActivity(editStudentIntent);
            }

            @Override
            public void onDelete(String id) {
                // ask for deletion confirmation
                AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(SQLiListStudentsActivity.this);
                deleteAlertBuilder.setTitle("Confirm Deletion")
                        .setMessage("Sure that you want to remove this student?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            boolean removed = dbHelper.removeStudent(id);
                            if (removed) {
                                ShowToast("Removed student successfully");
                            } else {
                                ShowToast("Couldn't remove student");
                            }
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
            Intent addStudentIntent = new Intent(SQLiListStudentsActivity.this, SQLiAddStudentActivity.class);
            startActivity(addStudentIntent);
        });

        // change to Firebase database
        Button switchButton = findViewById(R.id.switchButton);
        switchButton.setOnClickListener(v -> {
            Intent listStudentsIntent = new Intent(SQLiListStudentsActivity.this, ListStudentsActivity.class);
            startActivity(listStudentsIntent);
        });

        // listen for students changes
        dbHelper.setOnStudentsChangeListener(() -> {
            // update students adapter
            adapter.updateDataSet(dbHelper);
            adapter.notifyDataSetChanged();
        });
    }
}
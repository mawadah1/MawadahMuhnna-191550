package com.example.database;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.openweather.OpenWeatherWrapper;
import com.example.database.student.Gender;
import com.example.database.student.Student;
import com.example.database.student.Students;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddStudentActivity extends AppCompatActivity {

    /**
     * Shows toast with message
     */
    private void ShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // get UI elements
        EditText idEdit = findViewById(R.id.idEdit);
        EditText nameEdit = findViewById(R.id.nameEdit);
        EditText surNameEdit = findViewById(R.id.surNameEdit);
        EditText fatherNameEdit = findViewById(R.id.fatherNameEdit);
        EditText nationalEdit = findViewById(R.id.nationalEdit);

        // choose birth date dialog
        EditText birthEdit = findViewById(R.id.birthEdit);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            birthEdit.setText(formatDate(calendar.getTime()));
        };
        birthEdit.setOnClickListener(view -> new DatePickerDialog(AddStudentActivity.this, date,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());

        // choose gender dialog
        Spinner genderSpinner = findViewById(R.id.genderSpinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Gender.allStrings());
        genderSpinner.setAdapter(adapter);

        // show weather fragment
        WeatherFragment weatherFragment = (WeatherFragment) getSupportFragmentManager().findFragmentById(R.id.weatherFragment);
        if (weatherFragment != null) {
            weatherFragment.setOnWeatherClickListener(() -> {
                // show change query city dialog
                final EditText cityInput = new EditText(AddStudentActivity.this);
                cityInput.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder changeCityAlertBuilder = new AlertDialog.Builder(AddStudentActivity.this);
                changeCityAlertBuilder.setTitle("Change City")
                        .setView(cityInput)
                        .setMessage("Enter city to fetch weather:")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // change query city
                            String city = cityInput.getText().toString();
                            if (city.isEmpty()) {
                                ShowToast("Invalid city");
                                return;
                            }
                            OpenWeatherWrapper.setCity(city);
                            weatherFragment.updateWeather();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                        .setCancelable(true);
                AlertDialog changeCityAlert = changeCityAlertBuilder.create();
                changeCityAlert.show();
            });
        }

        // listen for confirm
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            // parse student values from UI
            String id, name, surName, fatherName, nationalId;
            try {
                id = idEdit.getText().toString();
                if (id.isEmpty()) throw new IllegalArgumentException("Invalid id");
                name = nameEdit.getText().toString();
                if (name.isEmpty()) throw new IllegalArgumentException("Invalid name");
                surName = surNameEdit.getText().toString();
                if (surName.isEmpty()) throw new IllegalArgumentException("Invalid surname");
                fatherName = fatherNameEdit.getText().toString();
                if (fatherName.isEmpty()) throw new IllegalArgumentException("Invalid father name");
                nationalId = nationalEdit.getText().toString();
                if (nationalId.isEmpty()) throw new IllegalArgumentException("Invalid national id");
            } catch (IllegalArgumentException e) {
                ShowToast(e.getMessage());
                return;
            }

            // parse birth date
            Date dateOfBirth;
            try {
                dateOfBirth = parseDate(birthEdit.getText().toString());
            } catch (ParseException e) {
                ShowToast("Couldn't parse date of birth");
                return;
            }

            // parse gender
            Gender gender;
            try {
                gender = parseGender((String) adapter.getItem(genderSpinner.getSelectedItemPosition()));
            } catch (ParseException e) {
                ShowToast("Couldn't parse gender");
                return;
            }

            // insert student using api
            Task<Void> addStudent = Students.putStudent(new Student(id, name, surName, fatherName,
                    nationalId, dateOfBirth, gender));
            if (addStudent == null) {
                ShowToast("Couldn't add student");
                return;
            }

            // wait for insert task to complete
            addStudent.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // student inserted
                    ShowToast("Added student successfully");
                    Intent listStudentsIntent = new Intent(AddStudentActivity.this, ListStudentsActivity.class);
                    startActivity(listStudentsIntent);
                } else {
                    ShowToast(Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        });

        birthEdit.setText(formatDate(calendar.getTime()));
        genderSpinner.setSelection(adapter.getPosition(formatGender(Gender.MALE)));
    }

    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    /**
     * Gets date from string
     */
    private static Date parseDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    /**
     * Gets string from date
     */
    private static String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Gets gender enum from string
     */
    private static Gender parseGender(String genderString) throws ParseException {
        return Gender.fromString(genderString);
    }

    /**
     * Gets string from gender enum
     */
    private static String formatGender(Gender gender) {
        return Gender.toString(gender);
    }
}
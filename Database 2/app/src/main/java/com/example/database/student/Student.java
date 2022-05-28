package com.example.database.student;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

public class Student implements Comparable<Student> {

    @NonNull
    public final String id;
    public final String name;
    public final String surName;
    public final String fatherName;
    public final String nationalId;
    public final Date dateOfBirth;
    public final Gender gender;

    /**
     * Creates student
     */
    public Student(@NonNull String id, String name, String surName, String fatherName, String nationalId,
                   Date dateOfBirth, Gender gender) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.fatherName = fatherName;
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    /**
     * Creates student with entry
     */
    public Student(@NonNull String id, StudentEntry entry) throws ParseException {
        this.id = id;
        this.name = entry.name;
        this.surName = entry.surName;
        this.fatherName = entry.fatherName;
        this.nationalId = entry.nationalId;
        this.dateOfBirth = new Date(entry.dateOfBirth);
        this.gender = Gender.fromString(entry.gender);
    }

    /**
     * Converts student to entry
     */
    public StudentEntry toStudentEntry() {
        return new StudentEntry(name, surName, fatherName, nationalId,
                dateOfBirth.getTime(), Gender.toString(gender));
    }

    /**
     * Converts student to map
     */
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (name != null) map.put("name", name);
        if (surName != null) map.put("surName", surName);
        if (fatherName != null) map.put("fatherName", fatherName);
        if (nationalId != null) map.put("nationalId", nationalId);
        if (dateOfBirth != null) map.put("dateOfBirth", dateOfBirth.getTime());
        if (gender != null) map.put("gender", Gender.toString(gender));
        return map;
    }

    /**
     * Compares two students using their name
     */
    @Override
    public int compareTo(Student student) {
        return name.compareTo(student.name);
    }
}

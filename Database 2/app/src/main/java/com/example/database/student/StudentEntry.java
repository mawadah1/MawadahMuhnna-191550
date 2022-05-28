package com.example.database.student;

import com.google.firebase.database.Exclude;

public class StudentEntry {
    public String name;
    public String surName;
    public String fatherName;
    public String nationalId;
    public Long dateOfBirth;
    public String gender;

    public StudentEntry() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /**
     * Creates student entry
     */
    public StudentEntry(String name, String surName, String fatherName, String nationalId,
                        Long dateOfBirth, String gender) {
        this.name = name;
        this.surName = surName;
        this.fatherName = fatherName;
        this.nationalId = nationalId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    /**
     * Checks entry is valid
     */
    @Exclude
    public boolean isDirty() {
        return name == null || surName == null || fatherName == null || nationalId == null ||
                dateOfBirth == null || gender == null;
    }
}

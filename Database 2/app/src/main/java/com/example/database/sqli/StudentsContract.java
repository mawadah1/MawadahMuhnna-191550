package com.example.database.sqli;

public final class StudentsContract {

    private StudentsContract() {
    }

    /**
     * Students SQLi database entry columns
     */
    public static class StudentDbEntry {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SURNAME = "surname";
        public static final String COLUMN_NAME_FATHER_NAME = "father_name";
        public static final String COLUMN_NAME_NATIONAL_ID = "national_id";
        public static final String COLUMN_NAME_DATE_OF_BIRTH = "date_of_birth";
        public static final String COLUMN_NAME_GENDER = "gender";
    }
}
package com.example.database.student;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public enum Gender {

    MALE,
    FEMALE;

    /**
     * Mapping from gender enum to string
     */
    private static final HashMap<Gender, String> genderToStringMap = new HashMap<Gender, String>() {{
        put(MALE, "Male");
        put(FEMALE, "Female");
    }};

    /**
     * Mapping from string to gender enum
     */
    private static final HashMap<String, Gender> stringToGenderMap = new HashMap<String, Gender>() {{
        put(genderToStringMap.get(MALE), MALE);
        put(genderToStringMap.get(FEMALE), FEMALE);
    }};

    /**
     * Converts string to gender enum
     */
    public static Gender fromString(String genderString) throws ParseException {
        Gender gender = stringToGenderMap.get(genderString);
        if (gender == null) {
            throw new ParseException("Unparseable gender: \"" + genderString + "\"", 0);
        }
        return gender;
    }

    /**
     * Converts gender enum to string
     */
    public static String toString(Gender gender) {
        return genderToStringMap.get(gender);
    }

    /**
     * Returns all gender enum values as strings
     */
    public static String[] allStrings() {
        ArrayList<String> genders = new ArrayList<>();
        for (Gender gender : Gender.values()) {
            genders.add(Gender.toString(gender));
        }
        return genders.toArray(new String[0]);
    }
}



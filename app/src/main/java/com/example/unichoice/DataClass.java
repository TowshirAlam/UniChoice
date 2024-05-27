package com.example.unichoice;

public class DataClass {
    private String collegeName;
    private static String collegeName2;
    private String imageUrl;

    public DataClass() {
        // Default constructor required for calls to DataSnapshot.getValue(DataClass.class)
    }

    public DataClass(String collegeName, String imageUrl) {
        this.collegeName = collegeName;
        this.imageUrl = imageUrl;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static String getCollegeName2() {
        return collegeName2;
    }

    public static void setCollegeName2(String collegeName2) {
        DataClass.collegeName2 = collegeName2;
    }
}

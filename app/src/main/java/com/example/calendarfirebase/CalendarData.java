package com.example.calendarfirebase;

public class CalendarData {
    private String title;
    private String firebaseKey;
    private String imageUrl;

    public CalendarData(String key,String title) {
        this.firebaseKey = key;
        this.title = title;
    }

    public CalendarData() {
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}

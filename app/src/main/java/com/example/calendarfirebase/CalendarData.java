package com.example.calendarfirebase;

public class CalendarData {
    private String title;
    private String firebaseKey;
//    TODO uid imageUrlをうまいこと合わせる　違いはユーザーごとにできるかできないか
    private String imageUrl;

    public CalendarData(String key,String title, String imageUrl) {
        this.firebaseKey = key;
        this.title = title +"歳";
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

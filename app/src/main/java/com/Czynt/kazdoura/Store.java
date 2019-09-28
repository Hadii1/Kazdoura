package com.Czynt.kazdoura;

public class Store {
    private static final String TAG = "InfoClass";
    private String name;
    private Float rating;
    private String description;
    private String category;
    private String photo;
    private String type;
    private String id;

    public Store() {
        //empty constructor needed for FirestoreQueries
    }

    public Store(String name, String description, Float rating, String category) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.category = category;

    }

    public String getName() {
        return name;
    }

    public Float getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

   public String getPhoto() {
        return photo;
    }

    public String getType() {
        return type;
    }

    public String getId(){
        return id;
    }


}

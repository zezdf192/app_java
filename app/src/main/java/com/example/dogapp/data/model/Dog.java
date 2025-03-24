package com.example.dogapp.data.model;

public class Dog {
    private String id;
    private String name;
    private String size;
    private String lifeSpan;
    private String temperament;
    private String des;
    private String takeCare;
    private String sick;
    private String image;

    // Constructor
    public Dog(String id, String name, String size, String lifeSpan, String temperament,
               String des, String takeCare, String sick, String image) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.lifeSpan = lifeSpan;
        this.temperament = temperament;
        this.des = des;
        this.takeCare = takeCare;
        this.sick = sick;
        this.image = image;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public String getLifeSpan() { return lifeSpan; }
    public String getTemperament() { return temperament; }
    public String getDes() { return des; }
    public String getTakeCare() { return takeCare; }
    public String getSick() { return sick; }
    public String getImage() { return image; }
}
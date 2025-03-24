package com.example.dogapp.data.model;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String size;
    private String color;
    private String des;
    private int price;
    private List<String> images;

    public Product(String id, String name, String size, String color, String des, int price, List<String> images) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.color = color;
        this.des = des;
        this.price = price;
        this.images = images;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getDes() { return des; }
    public int getPrice() { return price; }
    public List<String> getImages() { return images; }
}
package com.example.dogapp.data.model;

public class DogPost {
    private String name;
    private String line;
    private String character;
    private String health;
    private String medical;
    private boolean fullVaccin;
    private String vaccin;
    private boolean sterilization;
    private String takeCare;
    private String specialTakeCare;
    private String habit;
    private boolean liveTogether;
    private boolean train;
    private String imageUrl;
    private String user;
    private String phoneNumber;
    private String address;

    // Constructor
    public DogPost(String name, String line, String character, String health, String medical,
                   boolean fullVaccin, String vaccin, boolean sterilization, String takeCare,
                   String specialTakeCare, String habit, boolean liveTogether, boolean train,
                   String imageUrl, String user, String phoneNumber, String address) {
        this.name = name;
        this.line = line;
        this.character = character;
        this.health = health;
        this.medical = medical;
        this.fullVaccin = fullVaccin;
        this.vaccin = vaccin;
        this.sterilization = sterilization;
        this.takeCare = takeCare;
        this.specialTakeCare = specialTakeCare;
        this.habit = habit;
        this.liveTogether = liveTogether;
        this.train = train;
        this.imageUrl = imageUrl;
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters
    public String getName() { return name; }
    public String getLine() { return line; }
    public String getCharacter() { return character; }
    public String getHealth() { return health; }
    public String getMedical() { return medical; }
    public boolean isFullVaccin() { return fullVaccin; }
    public String getVaccin() { return vaccin; }
    public boolean isSterilization() { return sterilization; }
    public String getTakeCare() { return takeCare; }
    public String getSpecialTakeCare() { return specialTakeCare; }
    public String getHabit() { return habit; }
    public boolean isLiveTogether() { return liveTogether; }
    public boolean isTrain() { return train; }
    public String getImageUrl() { return imageUrl; }
    public String getUser() { return user; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
}
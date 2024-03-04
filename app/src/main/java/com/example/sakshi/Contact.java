package com.example.sakshi;

public class Contact {
    private String name;
    private String phone;

    // Required empty constructor for Firebase realtime
    public Contact() {}

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}

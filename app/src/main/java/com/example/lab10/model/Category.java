package com.example.lab10.model;

public class Category {
    private int Id;

    private String name;

    private String description;

    private int status;

    public Category(int id, String name, String description, int status) {
        Id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

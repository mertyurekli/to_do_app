package com.example.payup;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private boolean done;

    public String date;

    // Constructors
    public Task() {}

    public Task(String name, String description, String date, boolean done) {
        this.name = name;
        this.description = description;
        this.done = done;
        this.date = date;
    }
    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


}

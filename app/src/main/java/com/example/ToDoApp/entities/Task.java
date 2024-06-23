package com.example.ToDoApp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(tableName = "tasks", foreignKeys = @ForeignKey(entity = TaskList.class, parentColumns = "id", childColumns = "taskListId", onDelete = ForeignKey.CASCADE))
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private boolean done;
    private int taskListId;

    public String date;

    // Constructors
    public Task() {}

    public Task(String name, String description, String date, boolean done,  int taskListId) {
        this.name = name;
        this.description = description;
        this.done = done;
        this.date = date;
        this.taskListId = taskListId;
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

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }

}

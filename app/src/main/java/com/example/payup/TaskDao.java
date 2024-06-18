package com.example.payup;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE id=:taskId")
    Task getTask(int taskId);

    @Query("SELECT * FROM tasks WHERE id=:taskId")
    LiveData<Task> getTaskById(int taskId);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

}

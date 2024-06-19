package com.example.payup.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.payup.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE done = 1")
    LiveData<List<Task>> getFinishedTasks();

    @Query("SELECT * FROM tasks WHERE done = 0")
    LiveData<List<Task>> getUnfinishedTasks();

    @Query("SELECT * FROM tasks WHERE id=:taskId")
    LiveData<Task> getTaskById(int taskId);

    @Query("DELETE FROM tasks WHERE done = 1")
    void deleteFinishedTasks();

    @Update
    void update(Task task);



    @Delete
    void delete(Task task);

}

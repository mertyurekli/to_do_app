package com.example.ToDoApp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ToDoApp.entities.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Query("SELECT * FROM tasks WHERE taskListId = :taskListId")
    LiveData<List<Task>> getAllTasks(int taskListId);

    @Query("SELECT * FROM tasks WHERE done = 1 AND taskListId = :taskListId")
    LiveData<List<Task>> getFinishedTasks(int taskListId);

    @Query("SELECT * FROM tasks WHERE done = 0 AND taskListId = :taskListId")
    LiveData<List<Task>> getUnfinishedTasks(int taskListId);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<Task> getTaskById(int taskId);

    @Query("DELETE FROM tasks WHERE done = 1 AND taskListId = :taskListId")
    void deleteFinishedTasks(int taskListId);

    @Update
    void update(Task task);

    @Query("SELECT * FROM tasks WHERE taskListId = :taskListId")
    LiveData<List<Task>> getTasksByTaskListId(int taskListId);

    @Delete
    void delete(Task task);
}

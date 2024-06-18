package com.example.payup.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.payup.entities.TaskList;

import java.util.List;

@Dao
public interface TaskListDao {
    @Insert
    void insert(TaskList tasklist);

    @Query("SELECT * FROM task_lists")
    LiveData<List<TaskList>> getAllTaskLists();


    @Query("SELECT * FROM task_lists WHERE id=:taskListId")
    LiveData<TaskList> getTaskListById(int taskListId);

    @Update
    void update(TaskList taskList);

    @Delete
    void delete(TaskList taskList);

}

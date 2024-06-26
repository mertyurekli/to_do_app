package com.example.ToDoApp;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.ToDoApp.dao.TaskDao;
import com.example.ToDoApp.dao.TaskListDao;
import com.example.ToDoApp.entities.Task;
import com.example.ToDoApp.entities.TaskList;

@Database(entities = {Task.class, TaskList.class}, version = 4, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract TaskListDao taskListDao();

    private static volatile TaskDatabase INSTANCE;

    public static TaskDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TaskDatabase.class, "task_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

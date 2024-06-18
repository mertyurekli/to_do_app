package com.example.payup;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.payup.dao.TaskDao;
import com.example.payup.dao.TaskListDao;
import com.example.payup.entities.Task;
import com.example.payup.entities.TaskList;

@Database(entities = {Task.class, TaskList.class}, version = 3) // Include TaskList entity
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    public abstract TaskListDao taskListDao();

    private static volatile TaskDatabase INSTANCE;

    // Make this method public so it can be accessed from other packages
    public static TaskDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TaskDatabase.class, "task_database")
                            .fallbackToDestructiveMigration() // Handle schema changes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

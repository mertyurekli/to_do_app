package com.example.ToDoApp.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.ToDoApp.TaskDatabase;
import com.example.ToDoApp.dao.TaskListDao;
import com.example.ToDoApp.entities.TaskList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskListRepository {
    private static TaskListRepository INSTANCE;
    private TaskListDao mTaskListDao;
    private LiveData<List<TaskList>> mAllTaskLists;

    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    private TaskListRepository(Context context) {
        TaskDatabase db = TaskDatabase.getDatabase(context); // Access public method
        mTaskListDao = db.taskListDao();
        mAllTaskLists = mTaskListDao.getAllTaskLists();
        ensureDefaultTaskList();
    }

    public static TaskListRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TaskListRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskListRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private void ensureDefaultTaskList() {
        databaseWriteExecutor.execute(() -> {
            if (mTaskListDao.getTaskListCount() == 0) {
                TaskList defaultTaskList = new TaskList("Default Task List");
                mTaskListDao.insert(defaultTaskList);
            }
        });
    }

    public LiveData<List<TaskList>> getAllTaskLists() {
        return mAllTaskLists;
    }

    public LiveData<TaskList> getTaskListById(int id) {
        return mTaskListDao.getTaskListById(id);
    }

    public void insert(TaskList tasklist) {
        new insertAsyncTaskList(mTaskListDao).execute(tasklist);
    }

    public void update(TaskList tasklist) {
        new updateAsyncTaskList(mTaskListDao).execute(tasklist);
    }

    public void delete(TaskList tasklist) {
        new deleteAsyncTask(mTaskListDao).execute(tasklist);
    }

    private static class insertAsyncTaskList extends android.os.AsyncTask<TaskList, Void, Void> {
        private TaskListDao mAsyncTaskListDao;

        insertAsyncTaskList(TaskListDao dao) {
            mAsyncTaskListDao = dao;
        }

        @Override
        protected Void doInBackground(final TaskList... params) {
            mAsyncTaskListDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTaskList extends android.os.AsyncTask<TaskList, Void, Void> {
        private TaskListDao mAsyncTaskListDao;

        updateAsyncTaskList(TaskListDao dao) {
            mAsyncTaskListDao = dao;
        }

        @Override
        protected Void doInBackground(final TaskList... params) {
            mAsyncTaskListDao.update(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends android.os.AsyncTask<TaskList, Void, Void> {
        private TaskListDao mAsyncTaskListDao;

        deleteAsyncTask(TaskListDao dao) {
            mAsyncTaskListDao = dao;
        }

        @Override
        protected Void doInBackground(final TaskList... params) {
            mAsyncTaskListDao.delete(params[0]);
            return null;
        }
    }
}

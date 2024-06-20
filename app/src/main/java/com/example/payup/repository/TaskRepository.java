package com.example.payup.repository;
import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.payup.TaskDatabase;
import com.example.payup.dao.TaskDao;
import com.example.payup.entities.Task;

import java.util.List;

public class TaskRepository {
    private static TaskRepository INSTANCE;
    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    private LiveData<List<Task>> mFinishedTasks;

    private LiveData<List<Task>> mUnfinishedTasks;


    private TaskRepository(Context context) {
        TaskDatabase db = TaskDatabase.getDatabase(context);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAllTasks();
        mFinishedTasks = mTaskDao.getFinishedTasks();
    }

    public static TaskRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskRepository(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
    public LiveData<List<Task>> getFinishedTasks() {
        return mFinishedTasks;
    }


    public LiveData<List<Task>> getUnfinishedTasks() {
        return mTaskDao.getUnfinishedTasks();
    }

    public LiveData<List<Task>> getTasksByTaskListId(int taskListId) {
        return mTaskDao.getTasksByTaskListId(taskListId);
    }
    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<Task> getTaskById(int id) {
        return mTaskDao.getTaskById(id);
    }

    public void insert(Task task) {
        if (task.getTaskListId() == 0) {
            // Ensure a valid taskListId is set
            task.setTaskListId(1);  // Set a default valid task list ID if not specified
        }
        new insertAsyncTask(mTaskDao).execute(task);
    }

    public void update(Task task) {
        new updateAsyncTask(mTaskDao).execute(task);
    }

    public void delete(Task task) {
        new deleteAsyncTask(mTaskDao).execute(task);
    }

    private static class insertAsyncTask extends android.os.AsyncTask<Task, Void, Void> {
        private TaskDao mAsyncTaskDao;

        insertAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class updateAsyncTask extends android.os.AsyncTask<Task, Void, Void> {
        private TaskDao mAsyncTaskDao;

        updateAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends android.os.AsyncTask<Task, Void, Void> {
        private TaskDao mAsyncTaskDao;

        deleteAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public void deleteFinishedTasks() {
        new deleteFinishedTasksAsyncTask(mTaskDao).execute();
    }

    private static class deleteFinishedTasksAsyncTask extends android.os.AsyncTask<Void, Void, Void> {
        private TaskDao mAsyncTaskDao;

        deleteFinishedTasksAsyncTask(TaskDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mAsyncTaskDao.deleteFinishedTasks();
            return null;
        }
    }


}

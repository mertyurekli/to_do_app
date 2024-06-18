package com.example.payup.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;

import com.example.payup.TaskDatabase;
import com.example.payup.dao.TaskListDao;
import com.example.payup.entities.TaskList;

import java.util.List;

public class TaskListRepository {
    private static TaskListRepository INSTANCE;
    private TaskListDao mTaskListDao;
    private LiveData<List<TaskList>> mAllTaskLists;

    private TaskListRepository(Context context) {
        TaskDatabase db = TaskDatabase.getDatabase(context); // Access public method
        mTaskListDao = db.taskListDao();
        mAllTaskLists = mTaskListDao.getAllTaskLists();
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

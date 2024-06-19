package com.example.payup.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.payup.entities.TaskList;
import com.example.payup.repository.TaskListRepository;

import java.util.List;

public class TaskListViewModel extends AndroidViewModel {
    private TaskListRepository mRepository;
    private LiveData<List<TaskList>> mAllTaskLists;

    public TaskListViewModel(Application application) {
        super(application);
        mRepository = TaskListRepository.getInstance(application);
        mAllTaskLists = mRepository.getAllTaskLists();
    }

    public LiveData<List<TaskList>> getAllTaskLists() {
        return mAllTaskLists;
    }

    public LiveData<TaskList> getTaskListById(int id) {
        return mRepository.getTaskListById(id);
    }

    public void insert(TaskList tasklist) {
        mRepository.insert(tasklist);
    }

    public void update(TaskList tasklist) {
        mRepository.update(tasklist);
    }

    public void delete(TaskList tasklist) {
        mRepository.delete(tasklist);
    }
}

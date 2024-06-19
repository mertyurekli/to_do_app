package com.example.payup.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.payup.repository.TaskRepository;
import com.example.payup.entities.Task;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Task>> mFinishedTasks;
    private LiveData<List<Task>> mUnfinishedTasks;
    public TaskViewModel(Application application) {
        super(application);
        mRepository = TaskRepository.getInstance(application);
        mAllTasks = mRepository.getAllTasks();
        mFinishedTasks = mRepository.getFinishedTasks();
        mUnfinishedTasks = mRepository.getUnfinishedTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    public LiveData<List<Task>> getFinishedTasks() {
        return mFinishedTasks;
    }

    public LiveData<List<Task>> getUnfinishedTasks() {
        return mUnfinishedTasks;
    }

    public void deleteFinishedTasks() {
        mRepository.deleteFinishedTasks();
    }

    public LiveData<Task> getTaskById(int id) {
        return mRepository.getTaskById(id);
    }

    public void insert(Task task) {
        mRepository.insert(task);
    }

    public void update(Task task) {
        mRepository.update(task);
    }

    public void delete(Task task) {
        mRepository.delete(task);
    }

}

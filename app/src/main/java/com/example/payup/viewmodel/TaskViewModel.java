package com.example.payup.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.payup.entities.Task;
import com.example.payup.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private LiveData<List<Task>> mAllTasks;
    private LiveData<List<Task>> mFinishedTasks;
    private LiveData<List<Task>> mUnfinishedTasks;
    private final MediatorLiveData<List<Task>> filteredTasks = new MediatorLiveData<>();

    private LiveData<List<Task>> currentSource;

    public TaskViewModel(Application application) {
        super(application);
        mRepository = TaskRepository.getInstance(application);
        mAllTasks = mRepository.getAllTasks();
        mFinishedTasks = mRepository.getFinishedTasks();
        mUnfinishedTasks = mRepository.getUnfinishedTasks();
        setFilter(mAllTasks); // Set the initial filter
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

    public LiveData<List<Task>> getFilteredTasks() {
        return filteredTasks;
    }

    public void setFilter(LiveData<List<Task>> tasks) {
        if (currentSource != null) {
            filteredTasks.removeSource(currentSource);
        }
        currentSource = tasks;
        filteredTasks.addSource(tasks, filteredTasks::setValue);
    }

    public void deleteFinishedTasks() {
        mRepository.deleteFinishedTasks();
    }

    public LiveData<Task> getTaskById(int id) {
        return mRepository.getTaskById(id);
    }

    public LiveData<List<Task>> getTasksByTaskListId(int taskListId) {
        return mRepository.getTasksByTaskListId(taskListId);
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

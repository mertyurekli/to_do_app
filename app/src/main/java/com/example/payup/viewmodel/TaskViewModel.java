package com.example.payup.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.payup.entities.Task;
import com.example.payup.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository mRepository;
    private final MediatorLiveData<List<Task>> filteredTasks = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isFiltering = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> selectedTaskListId = new MutableLiveData<>(1); // Default to 1

    private LiveData<List<Task>> currentSource;

    public TaskViewModel(Application application) {
        super(application);
        mRepository = TaskRepository.getInstance(application);
        setFilter(mRepository.getAllTasks(selectedTaskListId.getValue())); // Set the initial filter
    }

    public LiveData<List<Task>> getFilteredTasks() {
        return filteredTasks;
    }

    public LiveData<Boolean> getIsFiltering() {
        return isFiltering;
    }

    public LiveData<Integer> getSelectedTaskListId() {
        return selectedTaskListId;
    }

    public void setSelectedTaskListId(int taskListId) {
        selectedTaskListId.setValue(taskListId);
        applyFilter();
    }

    private void applyFilter() {
        if (isFiltering.getValue() != null && isFiltering.getValue()) {
            setFilter(mRepository.getUnfinishedTasks(selectedTaskListId.getValue()));
        } else {
            setFilter(mRepository.getAllTasks(selectedTaskListId.getValue()));
        }
    }

    public void setFilter(LiveData<List<Task>> tasks) {
        if (currentSource != null) {
            filteredTasks.removeSource(currentSource);
        }
        currentSource = tasks;
        filteredTasks.addSource(tasks, filteredTasks::setValue);
    }

    public void deleteFinishedTasks() {
        mRepository.deleteFinishedTasks(selectedTaskListId.getValue());
    }

    public LiveData<Task> getTaskById(int id) {
        return mRepository.getTaskById(id);
    }

    public LiveData<List<Task>> getAllTasks(int taskListId) {
        return mRepository.getAllTasks(taskListId);
    }
    public LiveData<List<Task>> getUnfinishedTasks(int taskListId) {
        return mRepository.getUnfinishedTasks(taskListId);
    }
    public void insert(Task task) {
        mRepository.insert(task);
        applyFilter();  // Reapply filter after inserting a task
    }

    public void update(Task task) {
        mRepository.update(task);
        applyFilter();  // Reapply filter after updating a task
    }

    public void delete(Task task) {
        mRepository.delete(task);
        applyFilter();  // Reapply filter after deleting a task
    }
}

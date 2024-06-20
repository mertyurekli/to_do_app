package com.example.payup.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payup.MainActivity;
import com.example.payup.R;
import com.example.payup.TaskEditFragment;
import com.example.payup.entities.Task;
import com.example.payup.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private FragmentActivity activity;
    private FragmentManager fragmentManager;
    private TaskViewModel taskViewModel;
    private List<Task> taskList = new ArrayList<>();

    public TaskAdapter(FragmentActivity activity, FragmentManager fragmentManager, TaskViewModel taskViewModel) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.taskViewModel = taskViewModel;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task current = taskList.get(position);

        // Detach listener to avoid triggering it when setting state programmatically
        holder.taskDoneCheckBox.setOnCheckedChangeListener(null);

        // Bind data to the view holder
        holder.bind(current);

        // Set the checkbox state
        holder.taskDoneCheckBox.setChecked(current.isDone());

        // Set the item click listener for task details
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(activity, "Task: " + current.getName() + "\nDescription: " + current.getDescription(), Toast.LENGTH_SHORT).show();
            if (activity.getResources().getBoolean(R.bool.isTablet)) {
                TaskEditFragment fragment = new TaskEditFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("TASK_ID", current.getId());
                fragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.edit_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("TASK_ID", current.getId());
                activity.startActivity(intent);
            }
        });

        // Attach listener to handle checkbox state change
        holder.taskDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (current.isDone() != isChecked) {
                // Update the task's done state
                current.setDone(isChecked);

                // Update the task in ViewModel
                taskViewModel.update(current);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = new ArrayList<>(tasks); // Create a new list to avoid modification issues
        notifyDataSetChanged(); // This should only be called once initially or when the entire list changes
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitleView;
        private final CheckBox taskDoneCheckBox;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskTitleView = itemView.findViewById(R.id.textViewTitle);
            taskDoneCheckBox = itemView.findViewById(R.id.checkBox2);
        }

        public void bind(Task task) {
            taskTitleView.setText(task.getName());
            taskDoneCheckBox.setChecked(task.isDone());
        }
    }
}

package com.example.payup;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private FragmentActivity activity;
    private FragmentManager fragmentManager;
    private TaskViewModel taskViewModel;

    protected TaskAdapter(@NonNull DiffUtil.ItemCallback<Task> diffCallback, FragmentActivity activity, FragmentManager fragmentManager, TaskViewModel taskViewModel) {
        super(diffCallback);
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
        Task current = getItem(position);
        holder.bind(current.getName(), current.isDone());

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
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitleView;
        private final CheckBox taskDoneCheckBox;

        private TaskViewHolder(View itemView) {
            super(itemView);
            taskTitleView = itemView.findViewById(R.id.textViewTitle);
            taskDoneCheckBox = itemView.findViewById(R.id.checkBox2);
        }

        public void bind(String title, boolean isDone) {
            taskTitleView.setText(title);
            taskDoneCheckBox.setChecked(isDone);
        }
    }

    static class TaskDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    }
}

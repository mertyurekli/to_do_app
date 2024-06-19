package com.example.payup.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.payup.MainActivity;
import com.example.payup.R;
import com.example.payup.TaskEditFragment;
import com.example.payup.entities.Task;
import com.example.payup.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private FragmentActivity activity;
    private FragmentManager fragmentManager;
    private TaskViewModel taskViewModel;
    private List<Task> displayedTasks = new ArrayList<>();
    private boolean isFiltering = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isUpdating = false;

    public TaskAdapter(@NonNull DiffUtil.ItemCallback<Task> diffCallback, FragmentActivity activity, FragmentManager fragmentManager, TaskViewModel taskViewModel) {
        super(diffCallback);
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.taskViewModel = taskViewModel;
        setHasStableIds(true);  // Enable stable IDs
    }

    @Override
    public long getItemId(int position) {
        return displayedTasks.get(position).getId();  // Use task ID as stable ID
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task current = displayedTasks.get(position);
        holder.bind(current);

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

        // Remove any existing listener to avoid unwanted triggers
        holder.taskDoneCheckBox.setOnCheckedChangeListener(null);
        holder.taskDoneCheckBox.setChecked(current.isDone());

        // Set the new listener
        holder.taskDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdating) {
                return;
            }
            if (current.isDone() != isChecked) {
                isUpdating = true;
                current.setDone(isChecked);
                handler.postDelayed(() -> {
                    taskViewModel.update(current);
                    if (isFiltering) {
                        holder.taskDoneCheckBox.post(() -> notifyItemChanged(holder.getAdapterPosition()));
                    }
                    isUpdating = false;
                }, 200); // Debounce delay
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayedTasks != null ? displayedTasks.size() : 0;
    }

    public void setDisplayedTasks(List<Task> tasks, boolean isFiltering) {
        this.isFiltering = isFiltering;
        this.displayedTasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
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

    public static class TaskDiff extends DiffUtil.ItemCallback<Task> {

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

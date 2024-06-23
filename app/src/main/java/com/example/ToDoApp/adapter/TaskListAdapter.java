package com.example.ToDoApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToDoApp.R;
import com.example.ToDoApp.entities.TaskList;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> {

    private List<TaskList> taskLists;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TaskList taskList);
    }

    public TaskListAdapter(List<TaskList> taskLists, OnItemClickListener listener) {
        this.taskLists = taskLists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task_list, parent, false);
        return new TaskListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        TaskList currentTaskList = taskLists.get(position);
        holder.bind(currentTaskList, listener);
    }

    @Override
    public int getItemCount() {
        return taskLists.size();
    }

    public void setTaskLists(List<TaskList> taskLists) {
        this.taskLists = taskLists;
        notifyDataSetChanged();
    }

    static class TaskListViewHolder extends RecyclerView.ViewHolder {
        private TextView taskListNameView;

        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
            taskListNameView = itemView.findViewById(R.id.text_view_task_list_name);
        }

        public void bind(final TaskList taskList, final OnItemClickListener listener) {
            taskListNameView.setText(taskList.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(taskList));
        }
    }
}

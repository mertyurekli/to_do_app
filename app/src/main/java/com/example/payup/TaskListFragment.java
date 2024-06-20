package com.example.payup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payup.adapter.TaskAdapter;
import com.example.payup.entities.Task;
import com.example.payup.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private TaskViewModel mTaskViewModel;
    private TaskAdapter adapter;
    private int taskListId = -1;

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.r_view);
        Button addButton = rootView.findViewById(R.id.AddTaskButton);

        mTaskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        adapter = new TaskAdapter(requireActivity(), requireActivity().getSupportFragmentManager(), mTaskViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe selected task list ID
        mTaskViewModel.getSelectedTaskListId().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer selectedId) {
                if (selectedId != null) {
                    taskListId = selectedId;
                    mTaskViewModel.setFilter(mTaskViewModel.getAllTasks(taskListId));
                }
            }
        });

        // Observe filtered tasks and update RecyclerView when ready
        mTaskViewModel.getFilteredTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                if (tasks != null && taskListId != -1) {
                    adapter.setTasks(tasks);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    adapter.setTasks(new ArrayList<>()); // Clear tasks to avoid displaying old data
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getIntent().removeExtra("TASK_ID");
                openTaskEditFragmentToAddTask();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    Task task = adapter.getTaskAtPosition(position);

                    mTaskViewModel.delete(task);

                    Toast.makeText(requireContext(), "Task deleted: " + task.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        return rootView;
    }

    private void openTaskEditFragmentToAddTask() {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TASK_LIST_ID", taskListId);  // Pass the selected TaskList ID
        fragment.setArguments(bundle);
        if (!getResources().getBoolean(R.bool.isTablet)) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

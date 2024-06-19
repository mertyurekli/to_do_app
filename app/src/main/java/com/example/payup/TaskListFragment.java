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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payup.adapter.TaskAdapter;
import com.example.payup.entities.Task;
import com.example.payup.viewmodel.TaskViewModel;

import java.util.List;

public class TaskListFragment extends Fragment {

    private TaskViewModel mTaskViewModel;
    private TaskAdapter adapter;

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.r_view);
        Button addButton = rootView.findViewById(R.id.AddTaskButton);
        Button showAllTasksButton = rootView.findViewById(R.id.showAllTasksButton);
        Button showUnfinishedTasksButton = rootView.findViewById(R.id.showUnfinishedTasksButton);
        Button deleteFinishedTasksButton = rootView.findViewById(R.id.deleteFinishedTasksButton);

        // Initialize ViewModel
        mTaskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        adapter = new TaskAdapter(new TaskAdapter.TaskDiff(), requireActivity(), requireActivity().getSupportFragmentManager(), mTaskViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe filtered tasks
        mTaskViewModel.getFilteredTasks().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable final List<Task> tasks) {
                adapter.setDisplayedTasks(tasks, isFiltering(tasks));
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset TASK_ID to -1
                requireActivity().getIntent().removeExtra("TASK_ID");
                openTaskEditFragmentToAddTask();
            }
        });

        // Set onClickListeners for the buttons programmatically
        showAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskViewModel.setFilter(mTaskViewModel.getAllTasks());
                adapter.setDisplayedTasks(mTaskViewModel.getAllTasks().getValue(), false);
            }
        });

        showUnfinishedTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskViewModel.setFilter(mTaskViewModel.getUnfinishedTasks());
                adapter.setDisplayedTasks(mTaskViewModel.getUnfinishedTasks().getValue(), true);
            }
        });

        deleteFinishedTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTaskViewModel.deleteFinishedTasks();
                Toast.makeText(getContext(), "Finished tasks deleted", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private boolean isFiltering(List<Task> tasks) {
        if (tasks == null) {
            return false;
        }
        for (Task task : tasks) {
            if (task.isDone()) {
                return true;
            }
        }
        return false;
    }

    private void openTaskEditFragmentToAddTask() {
        if (!getResources().getBoolean(R.bool.isTablet)) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TaskEditFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.edit_fragment, new TaskEditFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
}

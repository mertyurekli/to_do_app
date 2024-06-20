package com.example.payup;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payup.adapter.TaskListAdapter;
import com.example.payup.entities.TaskList;
import com.example.payup.viewmodel.TaskListViewModel;
import com.example.payup.viewmodel.TaskViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TaskListViewModel taskListViewModel;
    private TaskListAdapter adapter;
    private TaskViewModel taskViewModel;
    private int selectedTaskListId = -1;  // Initialize with an invalid ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Use the appropriate layout for phones and tablets
        if (getResources().getBoolean(R.bool.isTablet)) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        // Set toolbar navigation click listener
        toolbar.setNavigationOnClickListener(v -> openTaskListFragment());

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the navigation item selection listener
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_create_task_list) {
                showCreateTaskListDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

        // Initialize RecyclerView for Task Lists in Navigation Drawer
        RecyclerView recyclerView = findViewById(R.id.recycler_view_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskListAdapter(new ArrayList<>(), taskList -> {
            selectedTaskListId = taskList.getId();  // Set the selected task list ID
            taskViewModel.setSelectedTaskListId(selectedTaskListId);
            fetchTasksForTaskList(selectedTaskListId);
            // Show a toast message with the name and ID of the selected task list
            Toast.makeText(MainActivity.this, "Selected Task List: " + taskList.getName() + ", ID: " + taskList.getId(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        taskListViewModel.getAllTaskLists().observe(this, taskLists -> adapter.setTaskLists(taskLists));

        if (savedInstanceState == null) {
            openTaskListFragment();
            if (getResources().getBoolean(R.bool.isTablet)) {
                openTaskEditFragment(-1);  // Open empty TaskEditFragment
            }
        }

        // Check if editing a task (if taskId is provided)
        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId != -1) {
            openTaskEditFragment(taskId);
        }
    }

    private void showCreateTaskListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Task List");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Task List Name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String taskListName = input.getText().toString().trim();
            if (!taskListName.isEmpty()) {
                createTaskList(taskListName);
            } else {
                Toast.makeText(MainActivity.this, "Task list name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createTaskList(String taskListName) {
        TaskList taskList = new TaskList(taskListName);
        taskListViewModel.insert(taskList);
        Toast.makeText(this, "Task list '" + taskListName + "' created", Toast.LENGTH_SHORT).show();
    }

    private void openTaskListFragment() {
        TaskListFragment fragment = new TaskListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (getResources().getBoolean(R.bool.isTablet)) {
            transaction.replace(R.id.list_fragment, fragment);
        } else {
            transaction.replace(R.id.fragment_container, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fetchTasksForTaskList(int taskListId) {
        taskViewModel.getTasksByTaskListId(taskListId).observe(this, tasks -> {
            taskViewModel.setFilter(taskViewModel.getTasksByTaskListId(taskListId));
        });
    }

    private void openTaskEditFragment(int taskId) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TASK_ID", taskId);
        if (taskViewModel != null && taskViewModel.getSelectedTaskListId().getValue() != null) {
            bundle.putInt("TASK_LIST_ID", taskViewModel.getSelectedTaskListId().getValue());  // Get the selected TaskList ID from ViewModel
        } else {
            bundle.putInt("TASK_LIST_ID", -1);  // Fallback value
        }
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (getResources().getBoolean(R.bool.isTablet)) {
            transaction.replace(R.id.edit_fragment, fragment);
        } else {
            transaction.replace(R.id.fragment_container, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

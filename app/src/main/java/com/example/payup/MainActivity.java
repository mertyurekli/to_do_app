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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.payup.entities.Task;
import com.example.payup.entities.TaskList;
import com.example.payup.viewmodel.TaskListViewModel;
import com.example.payup.viewmodel.TaskViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TaskListViewModel taskListViewModel;
    private com.example.payup.adapter.TaskListAdapter adapter;
    private TaskViewModel taskViewModel;

    private float startX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the appropriate layout for phones and tablets
        if (getResources().getBoolean(R.bool.isTablet)) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View leftClickableArea = findViewById(R.id.left_clickable_area);
        drawerLayout = findViewById(R.id.drawer_layout);

        leftClickableArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float endX = event.getX();
                        if (endX - startX > 100) { // Belirli bir mesafe sürüklenmişse
                            drawerLayout.openDrawer(GravityCompat.START);
                            return true;
                        }
                        break;
                }
                return false;
            }
        });


//        leftClickableArea.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });

        // Set toolbar navigation click listener
        toolbar.setNavigationOnClickListener(v -> openTaskListFragment());

        NavigationView navigationView = findViewById(R.id.nav_view);

        // Set up the navigation item selection listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_create_task_list) {
                    showCreateTaskListDialog();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            }
        });



        // Initialize RecyclerView for Task Lists in Navigation Drawer
        RecyclerView recyclerView = findViewById(R.id.recycler_view_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.example.payup.adapter.TaskListAdapter(new ArrayList<>(), new com.example.payup.adapter.TaskListAdapter.OnItemClickListener() {

            public void onItemClick(TaskList taskList) {
                // Handle item click
            }
        });
        recyclerView.setAdapter(adapter);

        taskListViewModel = new ViewModelProvider(this).get(TaskListViewModel.class);
        taskListViewModel.getAllTaskLists().observe(this, new Observer<List<TaskList>>() {
            @Override
            public void onChanged(List<TaskList> taskLists) {
                adapter.setTaskLists(taskLists);
            }
        });

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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskListName = input.getText().toString().trim();
                if (!taskListName.isEmpty()) {
                    // Handle the creation of the task list
                    createTaskList(taskListName);
                } else {
                    Toast.makeText(MainActivity.this, "Task list name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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
        taskViewModel.getTasksByTaskListId(taskListId).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Update UI with the tasks for the selected task list
                // For example, you can update a RecyclerView adapter
                Toast.makeText(MainActivity.this, "Fetched " + tasks.size() + " tasks for task list ID " + taskListId, Toast.LENGTH_SHORT).show();
                // Here you can update your UI with the fetched tasks
                // For example, open a new fragment and display tasks
                //openTaskFragment(tasks);
            }
        });
    }

    private void openTaskEditFragment(int taskId) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TASK_ID", taskId);
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

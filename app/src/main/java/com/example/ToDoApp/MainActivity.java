package com.example.ToDoApp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ToDoApp.adapter.TaskListAdapter;
import com.example.ToDoApp.entities.TaskList;
import com.example.ToDoApp.viewmodel.TaskListViewModel;
import com.example.ToDoApp.viewmodel.TaskViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TaskListViewModel taskListViewModel;
    private TaskListAdapter adapter;
    private TaskViewModel taskViewModel;
    private int selectedTaskListId = 1;
    private String CurrentTaskName = "Default Task List";

    private float startX;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use the appropriate layout for phones and tablets
        if (getResources().getBoolean(R.bool.isTablet)) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        toolbar = findViewById(R.id.toolbar);
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
                        if (endX - startX > 100) { // Dragged a certain distance
                            drawerLayout.openDrawer(GravityCompat.START);
                            return true;
                        }
                        break;
                }
                return false;
            }
        });

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

            drawerLayout.closeDrawer(GravityCompat.START);  // Automatically close the drawer
            return false;
        });

        // Initialize RecyclerView for Task Lists in Navigation Drawer
        RecyclerView recyclerView = findViewById(R.id.recycler_view_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskListAdapter(new ArrayList<>(), taskList -> {
            selectedTaskListId = taskList.getId();  // Set the selected task list ID
            taskViewModel.setSelectedTaskListId(selectedTaskListId);
            fetchTasksForTaskList(selectedTaskListId);
            // Set the action bar title to the selected task list name
            setActionBarTitle(taskList.getName());
            CurrentTaskName = taskList.getName();
            Toast.makeText(MainActivity.this, "Selected Task List: " + taskList.getName() + ", ID: " + taskList.getId(), Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);  // Automatically close the drawer
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

        // Check if editing a task
        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId != -1) {
            openTaskEditFragment(taskId);
        }
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        getSupportFragmentManager().addOnBackStackChangedListener(
                this::updateToolbar
        );

        updateToolbar();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            menu.findItem(R.id.action_show_all_tasks).setVisible(true);
            menu.findItem(R.id.action_show_unfinished_tasks).setVisible(true);
            menu.findItem(R.id.action_delete_finished_tasks).setVisible(true);
        } else {
            // Get the current fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

            // Hide menu items if the current fragment is TaskEditFragment
            if (currentFragment instanceof TaskEditFragment) {
                menu.findItem(R.id.action_show_all_tasks).setVisible(false);
                menu.findItem(R.id.action_show_unfinished_tasks).setVisible(false);
                menu.findItem(R.id.action_delete_finished_tasks).setVisible(false);
            } else {
                menu.findItem(R.id.action_show_all_tasks).setVisible(true);
                menu.findItem(R.id.action_show_unfinished_tasks).setVisible(true);
                menu.findItem(R.id.action_delete_finished_tasks).setVisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.action_show_all_tasks) {
            showAllTasks();
            return true;
        } else if (itemId == R.id.action_show_unfinished_tasks) {
            showUnfinishedTasks();
            return true;
        } else if (itemId == R.id.action_delete_finished_tasks) {
            deleteFinishedTasks();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showAllTasks() {
        if (selectedTaskListId != -1) {
            taskViewModel.setFilter(taskViewModel.getAllTasks(selectedTaskListId));
        }
    }

    private void showUnfinishedTasks() {
        if (selectedTaskListId != -1) {
            taskViewModel.setFilter(taskViewModel.getUnfinishedTasks(selectedTaskListId));
        }
    }

    private void deleteFinishedTasks() {
        if (selectedTaskListId != -1) {
            taskViewModel.deleteFinishedTasks();
            Toast.makeText(this, "Finished tasks deleted", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        taskViewModel.setFilter(taskViewModel.getAllTasks(taskListId));
    }

    private void openTaskEditFragment(int taskId) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TASK_ID", taskId);
        bundle.putInt("TASK_LIST_ID", selectedTaskListId);  // Pass the selected TaskList ID
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

    private void openTaskEditFragmentToAddTask(int taskListId) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("TASK_LIST_ID", taskListId);  // Pass the selected TaskList ID
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

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void updateToolbar() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        Fragment currentFragment = fragmentManager.findFragmentById(isTablet ? R.id.edit_fragment : R.id.fragment_container);

        boolean isEditFragment = currentFragment instanceof TaskEditFragment;

        if (isTablet) {
            //getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setTitle(CurrentTaskName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            if (isEditFragment) {
                // Show the back button in phone mode if TaskEditFragment is displayed
                getSupportActionBar().setTitle("Task Details");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            } else {
                // Show the drawer menu button in phone mode if TaskListFragment is displayed
                //getSupportActionBar().setTitle(R.string.app_name);
                getSupportActionBar().setTitle(CurrentTaskName);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        // Set toolbar navigation click listener
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            if (isEditFragment && !isTablet) {
                fragmentManager.popBackStack();
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        // Update the options menu whenever the back stack changes
        invalidateOptionsMenu();
    }
}

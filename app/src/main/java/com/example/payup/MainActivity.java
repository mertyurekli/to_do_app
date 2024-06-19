package com.example.payup;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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

import com.example.payup.entities.TaskList;
import com.example.payup.viewmodel.TaskListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TaskListViewModel taskListViewModel;
    private com.example.payup.TaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        // Use the appropriate layout for phones and tablets
        if (getResources().getBoolean(R.bool.isTablet)) {
            setContentView(R.layout.activity_main_tablet);
        } else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View leftClickableArea = findViewById(R.id.left_clickable_area);
        leftClickableArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Set toolbar navigation click listener
        toolbar.setNavigationOnClickListener(v -> openTaskListFragment());

        // Initialize RecyclerView for Task Lists in Navigation Drawer
        RecyclerView recyclerView = findViewById(R.id.recycler_view_task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.example.payup.TaskListAdapter(new ArrayList<>(), new com.example.payup.TaskListAdapter.OnItemClickListener() {
            @Override
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

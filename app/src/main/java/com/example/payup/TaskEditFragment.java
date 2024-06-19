package com.example.payup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.payup.entities.Task;
import com.example.payup.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class TaskEditFragment extends Fragment {

    private TaskViewModel mTaskViewModel;
    private DatePickerDialog datePickerDialog;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private CheckBox doneCheckBox;
    private Button dateButton;
    private String date;
    private boolean isNewTask = true;
    private int taskId = -1;  // Task ID

    public TaskEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        if (getArguments() != null) {
            taskId = getArguments().getInt("TASK_ID", -1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_edit, container, false);

        // Initialize views
        nameEditText = view.findViewById(R.id.textInputEditText2);
        descriptionEditText = view.findViewById(R.id.textInputEditText);
        doneCheckBox = view.findViewById(R.id.checkBox);
        dateButton = view.findViewById(R.id.button);

        initDatePicker();
        dateButton.setText(getCurrentDate());
        dateButton.setOnClickListener(v -> openDatePicker());

        // Check if editing existing task
        if (taskId != -1) {
            isNewTask = false;
            mTaskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
                if (task != null) {
                    nameEditText.setText(task.getName());
                    doneCheckBox.setChecked(task.isDone());
                    descriptionEditText.setText(task.getDescription());
                    dateButton.setText(task.getDate());
                }
            });
        }

        FloatingActionButton saveButton = view.findViewById(R.id.floatingActionButton);
        saveButton.setOnClickListener(v -> onClickSaveButton());

        return view;
    }

    private void initDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, month1, dayOfMonth) -> {
                    month1 = month1 + 1;
                    date = makeDateString(dayOfMonth, month1, year1);
                    dateButton.setText(date);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.setTitle("Select Date");
    }

    private void openDatePicker() {
        datePickerDialog.show();
    }

    private String makeDateString(int day, int month, int year) {
        return month + "-" + day + "-" + year;
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return month + "-" + day + "-" + year;
    }

    private void onClickSaveButton() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        boolean isDone = doneCheckBox.isChecked();
        String date = dateButton.getText().toString();
        Task task = new Task(name, description, date, isDone, 1);


        if (isNewTask) {
            mTaskViewModel.insert(task);
        } else {
            task.setId(taskId);
            mTaskViewModel.update(task);
        }

        // Navigate back or close the fragment
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}

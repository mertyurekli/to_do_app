package com.example.payup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class TaskDetailActivity extends AppCompatActivity {

    private TaskRepositoryInMemoryImpl taskRepository;
    public Button dateButton;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        initDatePicker();
        dateButton = findViewById(R.id.button);
        dateButton.setText((getCurrentDate()));
        dateButton.setOnClickListener(v -> openDataPicker());

        Toolbar toolbar = findViewById(R.id.toolbar);

        taskRepository = TaskRepositoryInMemoryImpl.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });
    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
    }

    public void onClickSaveButton(View view) {
        EditText shortNameEditText = findViewById(R.id.textInputEditText2);
        EditText descriptionEditText = findViewById(R.id.textInputEditText);
        CheckBox c = findViewById(R.id.checkBox);
        Button dateButton = findViewById(R.id.button);

        String shortName = shortNameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String Date = dateButton.getText().toString();
        boolean done;
        done = c.isChecked();

        // Create Task Object
        Task newTask = new Task(shortName);
        newTask.setShortName(shortName);
        newTask.setDescription(description);
        newTask.setDone(done);

        taskRepository.mTasks.add(newTask);

        // Reset
        shortNameEditText.setText("");
        descriptionEditText.setText("");
        c.setChecked(false);
    }

    private String makeDateString(int day, int month, int year) {
        return month  + "-" + day + "-" + year;
    }
    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    public void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        // Set initial date to current date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        // Set style of DatePicker
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        datePickerDialog.setTitle("Select Date");
    }

    public void openDataPicker() {
        datePickerDialog.show();
    }
}
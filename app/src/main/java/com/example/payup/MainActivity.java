package com.example.payup;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TaskRepositoryInMemoryImpl taskRepository;
    public TextView date;
    public Date mCreationDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initViews();
        taskRepository = TaskRepositoryInMemoryImpl.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void initViews() {
        mCreationDate = new Date();
        date = findViewById(R.id.textView6);
        String formattedDate = DateFormat.getDateInstance().format(mCreationDate);
        date.setText(formattedDate);
    }

    public void onClickSaveButton(View view) {
        EditText shortNameEditText = findViewById(R.id.textInputEditText2);
        EditText descriptionEditText = findViewById(R.id.textInputEditText);
        CheckBox c = findViewById(R.id.checkBox);
        TextView date = findViewById(R.id.textView6);

        String shortName = shortNameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String mydate = date.getText().toString();
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
}
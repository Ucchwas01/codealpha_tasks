package com.example.smartflashcards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView questionText, answerText;
    Button showAnswerBtn, nextBtn, prevBtn, addBtn, deleteBtn;
    Switch darkModeSwitch;
    LinearLayout mainLayout, cardLayout;

    ArrayList<String[]> flashcards = new ArrayList<>();
    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
        showAnswerBtn = findViewById(R.id.showAnswerBtn);
        nextBtn = findViewById(R.id.nextBtn);
        prevBtn = findViewById(R.id.prevBtn);
        addBtn = findViewById(R.id.addBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        mainLayout = findViewById(R.id.mainLayout);
        cardLayout = findViewById(R.id.cardLayout);

        flashcards.add(new String[]{"What is Java?", "A programming language"});
        flashcards.add(new String[]{"What is Android?", "Mobile OS"});
        flashcards.add(new String[]{"What is OOP?", "Object Oriented Programming"});

        showCard();

        showAnswerBtn.setOnClickListener(v -> answerText.setVisibility(TextView.VISIBLE));

        nextBtn.setOnClickListener(v -> {
            if (currentIndex < flashcards.size() - 1) {
                currentIndex++;
                showCard();
            }
        });

        prevBtn.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showCard();
            }
        });

        addBtn.setOnClickListener(v -> showAddDialog());

        deleteBtn.setOnClickListener(v -> {
            if (flashcards.size() > 1) {
                flashcards.remove(currentIndex);
                currentIndex = 0;
                showCard();
            }
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainLayout.setBackgroundColor(Color.BLACK);
                cardLayout.setBackgroundColor(Color.parseColor("#1E1E1E"));
                questionText.setTextColor(Color.WHITE);
                answerText.setTextColor(Color.LTGRAY);
            } else {
                mainLayout.setBackgroundColor(Color.parseColor("#EEF2F7"));
                cardLayout.setBackgroundColor(Color.WHITE);
                questionText.setTextColor(Color.BLACK);
                answerText.setTextColor(Color.BLACK);
            }
        });
    }

    private void showCard() {
        questionText.setText(flashcards.get(currentIndex)[0]);
        answerText.setText(flashcards.get(currentIndex)[1]);
        answerText.setVisibility(TextView.GONE);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Flashcard");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText qInput = new EditText(this);
        qInput.setHint("Enter Question");

        EditText aInput = new EditText(this);
        aInput.setHint("Enter Answer");

        layout.addView(qInput);
        layout.addView(aInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String q = qInput.getText().toString();
            String a = aInput.getText().toString();

            if (!q.isEmpty() && !a.isEmpty()) {
                flashcards.add(new String[]{q, a});
                currentIndex = flashcards.size() - 1;
                showCard();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
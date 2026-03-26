package com.example.randomquotegenerator;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView quoteText, messageText;
    Button newQuoteBtn, onlineQuoteBtn;
    Switch darkModeSwitch;
    Spinner categorySpinner;
    LinearLayout mainLayout, quoteCard;

    Random random = new Random();
    Map<String, String[]> quotes = new HashMap<>();
    String currentCategory = "Motivation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteText = findViewById(R.id.quoteText);
        messageText = findViewById(R.id.messageText);
        newQuoteBtn = findViewById(R.id.newQuoteBtn);
        onlineQuoteBtn = findViewById(R.id.onlineQuoteBtn);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        categorySpinner = findViewById(R.id.categorySpinner);
        mainLayout = findViewById(R.id.mainLayout);
        quoteCard = findViewById(R.id.quoteCard);

        // Quotes
        quotes.put("Motivation", new String[]{
                "Push yourself because no one else will.",
                "Start where you are.",
                "Dream big, act bigger.",
                "Stay hungry, stay foolish."
        });

        quotes.put("Success", new String[]{
                "Success is earned.",
                "Hard work beats talent.",
                "Consistency is key.",
                "Discipline leads to success."
        });

        quotes.put("Failure", new String[]{
                "Failure builds strength.",
                "Learn from failure.",
                "Every failure is progress.",
                "Try again stronger."
        });

        quotes.put("Life", new String[]{
                "Life is what you make it.",
                "Enjoy every moment.",
                "Live fully.",
                "Be present."
        });

        quotes.put("Feelings", new String[]{
                "Your feelings matter.",
                "Stay strong inside.",
                "Emotions make us human.",
                "Express yourself."
        });

        quotes.put("Study", new String[]{
                "Study now, shine later.",
                "Focus creates success.",
                "Consistency beats motivation.",
                "Learn daily."
        });

        String[] categories = {"Motivation", "Success", "Failure", "Life", "Feelings", "Study"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                currentCategory = categories[position];
                showQuote();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        showQuote();

        newQuoteBtn.setOnClickListener(v -> {
            messageText.setText("");
            showQuote();
        });

        onlineQuoteBtn.setOnClickListener(v -> {
            messageText.setText("This app is under construction, this option will available soon");
        });

        // Dark Mode FIXED
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainLayout.setBackgroundColor(Color.BLACK);
                quoteCard.setBackgroundColor(Color.parseColor("#1E1E1E"));

                quoteText.setTextColor(Color.WHITE);
                messageText.setTextColor(Color.LTGRAY);

            } else {
                mainLayout.setBackgroundColor(Color.parseColor("#EEF2F7"));
                quoteCard.setBackgroundColor(Color.WHITE);

                quoteText.setTextColor(Color.parseColor("#333333"));
                messageText.setTextColor(Color.RED);
            }
        });
    }

    private void showQuote() {
        String[] categoryQuotes = quotes.get(currentCategory);
        int index = random.nextInt(categoryQuotes.length);
        quoteText.setText("\"" + categoryQuotes[index] + "\"");
    }
}
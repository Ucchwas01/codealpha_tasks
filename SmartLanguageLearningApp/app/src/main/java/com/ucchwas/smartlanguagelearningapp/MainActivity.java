package com.ucchwas.smartlanguagelearningapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView wordText, meaningText, statusText;
    private EditText customWordInput;
    private LinearLayout mainLayout;
    private Translator translator;
    private Spinner sourceLangSpinner, targetLangSpinner;

    private HashMap<String, String[]> data;
    private String[] currentWords;
    private final Random random = new Random();

    // Map to store Language Names and their ML Kit Codes
    private final Map<String, String> languageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Language Map
        initLanguageMap();

        // Initialize Views
        wordText = findViewById(R.id.wordText);
        meaningText = findViewById(R.id.meaningText);
        statusText = findViewById(R.id.statusText);
        customWordInput = findViewById(R.id.customWordInput);
        sourceLangSpinner = findViewById(R.id.sourceLangSpinner);
        targetLangSpinner = findViewById(R.id.targetLangSpinner);
        
        Button showBtn = findViewById(R.id.showBtn);
        Button nextBtn = findViewById(R.id.nextBtn);
        Button translateBtn = findViewById(R.id.translateBtn);
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        SwitchCompat darkSwitch = findViewById(R.id.darkSwitch);
        mainLayout = findViewById(R.id.mainLayout);

        setupLanguageSpinners();

        // Categories (Static words for quick learning)
        final String[] categories = {"Basic", "Intermediate"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(catAdapter);

        // Prepare Predefined Data
        data = new HashMap<>();
        data.put("Basic", new String[]{"Hello - নমস্কার", "Good - ভালো", "Book - বই", "Food - খাবার"});
        data.put("Intermediate", new String[]{"Success - সফলতা", "Failure - ব্যর্থতা", "Knowledge - জ্ঞান", "Effort - প্রচেষ্টা"});

        // Listeners
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWord(categories[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        showBtn.setOnClickListener(v -> meaningText.setVisibility(View.VISIBLE));
        nextBtn.setOnClickListener(v -> showRandomWord());
        
        translateBtn.setOnClickListener(v -> {
            String text = customWordInput.getText().toString().trim();
            if (!text.isEmpty()) {
                translateText(text);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a word", Toast.LENGTH_SHORT).show();
            }
        });

        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int bgColor = isChecked ? Color.BLACK : Color.parseColor("#EEF2F7");
            int txtColor = isChecked ? Color.WHITE : Color.BLACK;
            int subTxtColor = isChecked ? Color.LTGRAY : Color.DKGRAY;

            mainLayout.setBackgroundColor(bgColor);
            wordText.setTextColor(txtColor);
            meaningText.setTextColor(subTxtColor);
            statusText.setTextColor(subTxtColor);
        });

        loadWord("Basic");
    }

    private void initLanguageMap() {
        languageMap.put("English", TranslateLanguage.ENGLISH);
        languageMap.put("Bengali", TranslateLanguage.BENGALI);
        languageMap.put("Hindi", TranslateLanguage.HINDI);
        languageMap.put("Spanish", TranslateLanguage.SPANISH);
        languageMap.put("French", TranslateLanguage.FRENCH);
        languageMap.put("German", TranslateLanguage.GERMAN);
        languageMap.put("Arabic", TranslateLanguage.ARABIC);
        languageMap.put("Chinese", TranslateLanguage.CHINESE);
    }

    private void setupLanguageSpinners() {
        List<String> languages = new ArrayList<>(languageMap.keySet());
        Collections.sort(languages);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, languages);

        sourceLangSpinner.setAdapter(adapter);
        targetLangSpinner.setAdapter(adapter);

        // Set Defaults
        sourceLangSpinner.setSelection(adapter.getPosition("English"));
        targetLangSpinner.setSelection(adapter.getPosition("Bengali"));

        AdapterView.OnItemSelectedListener languageListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                reinitTranslator();
            }
            @Override
            public void onNothingSelected(AdapterView<?> p) {}
        };

        sourceLangSpinner.setOnItemSelectedListener(languageListener);
        targetLangSpinner.setOnItemSelectedListener(languageListener);
    }

    private void reinitTranslator() {
        if (translator != null) {
            translator.close();
        }

        String sourceName = sourceLangSpinner.getSelectedItem().toString();
        String targetName = targetLangSpinner.getSelectedItem().toString();

        String sourceCode = languageMap.get(sourceName);
        String targetCode = languageMap.get(targetName);

        if (sourceCode.equals(targetCode)) {
            statusText.setText("Status: Source and Target language cannot be same");
            return;
        }

        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceCode)
                .setTargetLanguage(targetCode)
                .build();
        
        translator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();

        statusText.setText("Status: Loading " + targetName + " model...");
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(unused -> statusText.setText("Status: " + sourceName + " to " + targetName + " Ready"))
                .addOnFailureListener(e -> statusText.setText("Status: Model Error"));
    }

    private void translateText(String text) {
        if (translator == null) return;
        
        statusText.setText("Status: Translating...");
        translator.translate(text)
                .addOnSuccessListener(translated -> {
                    wordText.setText(text);
                    meaningText.setText(translated);
                    meaningText.setVisibility(View.VISIBLE);
                    statusText.setText("Status: Ready");
                })
                .addOnFailureListener(e -> {
                    statusText.setText("Status: Error");
                    Toast.makeText(this, "Wait for model download", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadWord(String category) {
        currentWords = data.get(category);
        showRandomWord();
    }

    private void showRandomWord() {
        if (currentWords != null && currentWords.length > 0) {
            int index = random.nextInt(currentWords.length);
            String[] parts = currentWords[index].split(" - ");
            if (parts.length == 2) {
                wordText.setText(parts[0]);
                meaningText.setText(parts[1]);
                meaningText.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (translator != null) translator.close();
    }
}

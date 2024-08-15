package com.java.tomorrowstailnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchActivity extends AppCompatActivity {
    private String suggestion, warning;

    private Toolbar toolbar;
    private ImageView imgSearch;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getIntent();
        toolbar = findViewById(R.id.toolbarSearchActivity);
        imgSearch = findViewById(R.id.imgSearchSearchActivity);
        editText = findViewById(R.id.editTextSearchActivity);
        suggestion = getApplicationContext().getResources().getString(R.string.searchSuggestion);
        warning = suggestion + getApplicationContext().getResources().getString(R.string.searchWarning);
        editText.setHint(warning);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyword = editText.getText().toString();
                Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                keyword = keyword.isEmpty() ? suggestion : keyword;
                intent.putExtra("keyword", keyword);
                Log.d("--------", "onClick: " + keyword);
                startActivity(intent);
            }
        });
    }
}
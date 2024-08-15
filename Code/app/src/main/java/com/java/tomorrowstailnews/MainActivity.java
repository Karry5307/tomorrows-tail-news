package com.java.tomorrowstailnews;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private StarFragment starFragment;
    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavBar);
        selectFragment(0);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.bottomMenuHome) {
                    selectFragment(0);
                }
                if (item.getItemId() == R.id.bottomMenuStar) {
                    selectFragment(1);
                }
                if (item.getItemId() == R.id.bottomMenuHistory) {
                    selectFragment(2);
                }
                return true;
            }
        });
    }

    private void selectFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragments(fragmentTransaction);
        if (position == 0) {
            if (homeFragment == null) {
                homeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.frameLayoutContent, homeFragment);
            } else {
                fragmentTransaction.show(homeFragment);
            }
        }
        if (position == 1) {
            if (starFragment == null) {
                starFragment = new StarFragment();
                fragmentTransaction.add(R.id.frameLayoutContent, starFragment);
            } else {
                fragmentTransaction.show(starFragment);
            }
        }
        if (position == 2) {
            if (historyFragment == null) {
                historyFragment = new HistoryFragment();
                fragmentTransaction.add(R.id.frameLayoutContent, historyFragment);
            } else {
                fragmentTransaction.show(historyFragment);
            }
        }
        fragmentTransaction.commit();
    }

    private void hideAllFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (starFragment != null) {
            transaction.hide(starFragment);
        }
        if (historyFragment != null) {
            transaction.hide(historyFragment);
        }
    }
}
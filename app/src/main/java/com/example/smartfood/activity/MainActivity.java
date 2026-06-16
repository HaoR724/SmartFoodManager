package com.example.smartfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chapter01.R;
import com.example.smartfood.fragment.HealthFragment;
import com.example.smartfood.fragment.HomeFragment;
import com.example.smartfood.fragment.IngredientFragment;
import com.example.smartfood.fragment.RecipeFragment;
import com.example.smartfood.fragment.ShoppingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private TextView appTitleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_main);
        appTitleText = findViewById(R.id.tv_app_title);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        findViewById(R.id.fab_quick_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddMealRecordActivity.class));
            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) return showFragment(new HomeFragment());
                if (item.getItemId() == R.id.nav_ingredient) return showFragment(new IngredientFragment());
                if (item.getItemId() == R.id.nav_recipe) return showFragment(new RecipeFragment());
                if (item.getItemId() == R.id.nav_health) return showFragment(new HealthFragment());
                if (item.getItemId() == R.id.nav_shopping) return showFragment(new ShoppingFragment());
                return false;
            }
        });
        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean showFragment(Fragment fragment) {
        if (appTitleText != null) {
            boolean useImmersiveHeader = fragment instanceof HomeFragment || fragment instanceof IngredientFragment;
            appTitleText.setVisibility(useImmersiveHeader ? View.GONE : View.VISIBLE);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        return true;
    }
}

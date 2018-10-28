package com.ao.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.ao.android.data.Student;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
        }

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_logout :
                                drawerLayout.closeDrawers();
                                logout();
                                return true;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });

        TextView welcomeView = findViewById(R.id.welcome_view);

        Student student = (Student) getIntent().getSerializableExtra("STUDENT");
        String welcomeText = getString(R.string.text_view_welcome, student.getUsername());
        welcomeView.setText(welcomeText);

        String studentId = student.getStudentId();
        Log.d(TAG, "Student ID: " + studentId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }
}

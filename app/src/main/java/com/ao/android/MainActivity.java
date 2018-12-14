package com.ao.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.ao.android.adapter.RecyclerViewHLA;
import com.ao.android.data.Classroom;
import com.ao.android.data.Student;
import com.ao.android.utils.AOUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
                            case R.id.nav_classes :
                                drawerLayout.closeDrawers();

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

        TextView dateView = findViewById(R.id.date_text_view);
        String[] details = AOUtil.getDateDetails();
        String dateText = getString(R.string.current_date, details[0], details[1], details[2]);
        dateView.setText(dateText);

        List<Classroom> classrooms = createFakeClassrooms();
        Log.d(TAG, "Created test classrooms");

        RecyclerView recyclerView = findViewById(R.id.rec_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.HORIZONTAL));
        RecyclerViewHLA recyclerViewHLA = new RecyclerViewHLA(classrooms, getApplicationContext());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(recyclerViewHLA);
        recyclerViewHLA.notifyDataSetChanged();
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
        intent.putExtra("LOGIN_QUICK", false);
        startActivity(intent);
    }

    private List<Classroom> createFakeClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        //Fake class 1
        Classroom c1 = new Classroom("CSCI 318", "M02");
        c1.setClassType(Classroom.ClassType.LECTURE);
        Calendar s1 = Calendar.getInstance(Locale.US);
        s1.set(Calendar.HOUR_OF_DAY, 9);
        s1.set(Calendar.MINUTE, 35);
        s1.set(Calendar.AM_PM, Calendar.AM);
        c1.setStartTime(s1);
        s1.set(Calendar.HOUR_OF_DAY, 12);
        s1.set(Calendar.MINUTE, 25);
        s1.set(Calendar.AM_PM, Calendar.PM);
        c1.setEndTime(s1);
        c1.setLocation("16 West 61 Street 624");

        //Fake class 2
        Classroom c2 = new Classroom("CSCI 310", "M01");
        c2.setClassType(Classroom.ClassType.LECTURE);
        Calendar s2 = Calendar.getInstance(Locale.US);
        s2.set(Calendar.HOUR_OF_DAY, 9);
        s2.set(Calendar.MINUTE, 35);
        s2.set(Calendar.AM_PM, Calendar.AM);
        c2.setStartTime(s2);
        s2.set(Calendar.HOUR_OF_DAY, 12);
        s2.set(Calendar.MINUTE, 25);
        s2.set(Calendar.AM_PM, Calendar.PM);
        c2.setEndTime(s2);
        c2.setLocation("16 West 61st Street 723");

        classrooms.add(c1);
        classrooms.add(c2);
        return classrooms;
    }

}

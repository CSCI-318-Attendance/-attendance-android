package com.ao.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ao.android.data.Student;
import com.ao.android.database.SQLManager;
import com.ao.android.emulation.HostCardEmulatorService;
import com.ao.android.utils.AOUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private HandlerThread thread;
    private Intent hceIntent;
    private UUID applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*if (true) {
            Intent i = new Intent(this, MainActivity.class);
            Student s = new Student("333333");
            s.setUsername("Henlo");
            i.putExtra("STUDENT", s);
            startActivity(i);
        }*/

        boolean attemptLogin = getIntent().getBooleanExtra("LOGIN_QUICK", true);
        String[] storeInformation = quickLogin(attemptLogin);

        final EditText username = findViewById(R.id.username_view);
        final EditText password = findViewById(R.id.password_view);
        final CheckBox remember_me = findViewById(R.id.remember_me_view);
        final ProgressBar loginProgress = findViewById(R.id.login_progress);

        final Button loginBtn = findViewById(R.id.login_button_view);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginBtnClick(loginProgress, username, password, remember_me, loginBtn);
            }
        });

        if (storeInformation != null && storeInformation.length == 2) {
            username.setText(storeInformation[0]);
            password.setText(storeInformation[1]);
            handleLoginBtnClick(loginProgress, username, password, remember_me, loginBtn);
        }

        final ImageView createUserView = findViewById(R.id.create_user_view);
        createUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateUserClick();
            }
        });

        checkApplicationId();
        Log.d(TAG, applicationId.toString());

        hceIntent = new Intent(this, HostCardEmulatorService.class);
        hceIntent.putExtra("APPLICATION_ID", applicationId.toString());
        startService(hceIntent);
        Log.d(TAG, "Starting... HostCardEmulatorService");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            thread.quit();
            thread.interrupt();
        }
        stopService(hceIntent);
    }

    private void handleLoginBtnClick(final ProgressBar progressBar, final View... views) {
        thread = new HandlerThread("login_thread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        AOUtil.toggleViews(false, views);
        progressBar.setVisibility(View.VISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String username = ((EditText) views[0]).getText().toString();
                String password = ((EditText) views[1]).getText().toString();
                final SQLManager manager = handleLogin(username, password);
                if (manager != null) {
                    if (!storeLogin((CheckBox) views[2], username, password)) {
                        Log.d(TAG, "Login information was not stored...");
                    }
                    changeActivity(manager);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (manager == null) {
                            AOUtil.toggleViews(true, views);
                        }
                    }
                });
            }
        }, 100);
    }

    private void handleCreateUserClick() {
        Intent i = new Intent(getBaseContext(), RegisterActivity.class);
        i.putExtra("APPLICATION_ID", applicationId.toString());
        startActivity(i);
    }

    private SQLManager handleLogin(String username, String password) {
        SQLManager manager = new SQLManager(applicationId);

        if (!manager.connect()) {
            Log.d(TAG, "Something went wrong when connecting to the database.");
            return null;
        }

        if (!manager.login(username, password)) {
            Log.d(TAG, "Something went wrong when logging in.");
            return null;
        }
        if (manager.getStudent() == null) {
            Log.d(TAG, "Student is null.");
            return null;
        }

        return manager;
    }

    private void changeActivity(SQLManager manager) {
        Student student = manager.getStudent();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("STUDENT", student);
        intent.putExtra("APPLICATION_ID", applicationId);
        startActivity(intent);
    }

    private boolean storeLogin(CheckBox remember_me, String username, String password) {
        if (!remember_me.isChecked()) {
            return false;
        }
        String storeFilename = "login.txt";
        String storeContents = username + "," + password;
        FileOutputStream oStream;
        try {
            oStream = openFileOutput(storeFilename, MODE_PRIVATE);
            oStream.write(storeContents.getBytes());
            oStream.close();
        } catch (IOException ex) {
            Log.d(TAG, "Trouble when trying to store login information. " + ex);
            return false;
        }
        return true;
    }

    private String[] quickLogin(boolean login) {
        String storeFilename = "login.txt";
        if (!login) {
            if (fileExists(storeFilename)) {
                File storeFile = new File(getFilesDir(), storeFilename);
                if (!storeFile.delete()) {
                    Log.d(TAG, "Error wiping stored login data");
                }
            }
            return null;
        }
        if (!fileExists(storeFilename)) {
            Log.d(TAG, "Login store file not found skipping quick login.");
            return null;
        }
        StringBuilder storeContents = new StringBuilder();
        FileInputStream iStream;
        BufferedReader reader;
        try {
            String line;
            iStream = openFileInput(storeFilename);
            reader = new BufferedReader(new InputStreamReader(iStream));
            while ((line = reader.readLine()) != null) {
                storeContents.append(line);
            }
            Log.d(TAG, "Successfully found information for quick login.");
        } catch (IOException ex) {
            Log.d(TAG, "Trouble when trying to access quick login. " + ex);
            return null;
        }
        Log.d(TAG, storeContents.toString());
        return storeContents.toString().split(",");
    }

    private boolean fileExists(String filename) {
        return new File(getApplicationContext().getFilesDir(), filename).exists();
    }

    private void checkApplicationId() {
        SharedPreferences shared = getPreferences(MODE_PRIVATE);
        String savedUUID = shared.getString(getString(R.string.uuid_map), "");
        if (!savedUUID.equals("")) {
            applicationId = UUID.fromString(savedUUID);
        } else {
            applicationId = UUID.randomUUID();
            SharedPreferences.Editor editor = shared.edit();
            editor.putString(getString(R.string.uuid_map), applicationId.toString());
            editor.apply();
        }
    }
}

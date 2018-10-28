package com.ao.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ao.android.data.Student;
import com.ao.android.database.SQLManager;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        final EditText username = findViewById(R.id.username_view);
        final EditText password = findViewById(R.id.password_view);
        final CheckBox remember_me = findViewById(R.id.remember_me_view);
        final ProgressBar loginProgress = findViewById(R.id.login_progress);

        final Button loginBtn = findViewById(R.id.login_button_view);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    handleLoginBtnClick(loginProgress, username, password, remember_me);
                } catch (Exception ex) {
                    Log.d(TAG, "Something went wrong when clicking the login button: " + ex);
                }
            }
        });

    }

    private void handleLoginBtnClick(final ProgressBar progressBar, final View... views) {
        HandlerThread thread = new HandlerThread("login_thread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        toggleViews(false, views);
        progressBar.setVisibility(View.VISIBLE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login((EditText) views[0], (EditText) views[1]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        toggleViews(true, views);
                    }
                });
            }
        }, 500);

    }

    private void toggleViews(boolean enabled, View... views) {
        for (View view : views) {
            view.setEnabled(enabled);
        }
    }

    private void login(EditText usernameTxt, EditText passwordTxt) {
        SQLManager manager = new SQLManager();
        if (!manager.isInitialize()) {
            Log.d(TAG, "Something went wrong when connecting to the database.");
            return;
        }

        String username = usernameTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        if (!manager.login(username, password)) {
            Log.d(TAG, "Something went wrong when logging in.");
            return;
        }
        if (manager.getStudent() == null) {
            Log.d(TAG, "Student is null.");
            return;
        }
        Student student = manager.getStudent();
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("STUDENT", student);
        startActivity(intent);
    }
}

package com.ao.android;

import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ao.android.database.SQLManager;
import com.ao.android.utils.AOUtil;

import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private HandlerThread thread;
    private AppCompatActivity activity;
    private AlertDialog dialog;
    private UUID applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        activity = this;

        final EditText usernameText = findViewById(R.id.username_cu_view);
        final EditText emailText = findViewById(R.id.email_cu_view);
        final EditText passwordText = findViewById(R.id.password_cu_view);
        final EditText passwordConfirm = findViewById(R.id.password_cu_confirm);
        final EditText studentId = findViewById(R.id.student_id_cu_view);
        final ProgressBar progress = findViewById(R.id.create_user_progress);
        final ImageButton backBtn = findViewById(R.id.back_cu_button);

        final Button createUserBtn = findViewById(R.id.create_user_button);
        createUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateUserBtnClick(progress, usernameText, emailText, passwordText,
                        passwordConfirm, studentId, createUserBtn, backBtn);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backBtn.setEnabled(false);
                handleBackButtonClick();
            }
        });
        applicationId = UUID.fromString(getIntent().getStringExtra("APPLICATION_ID"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            thread.quit();
            thread.interrupt();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void handleCreateUserBtnClick(final ProgressBar progressBar, final View... views) {
        thread = new HandlerThread("create_user_thread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());

        AOUtil.toggleViews(true, views);
        progressBar.setVisibility(View.VISIBLE);

        if (!passwordsMatch(views[2], views[3])) {
            AOUtil.alertUser(this, "Create Account", "Your password do not match.");
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String username = ((EditText) views[0]).getText().toString();
                String email = ((EditText) views[1]).getText().toString();
                String password = ((EditText) views[2]).getText().toString();
                String studentId = ((EditText) views[4]).getText().toString();
                final SQLManager manager = handleCreateUser(username, password, studentId, email);
                if (manager != null) {
                    dialog = AOUtil.alertUser(activity, "Create Account",
                            "Successfully created your new account");
                    dialog.show();
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

    private void handleBackButtonClick() {
        navigateToLogin();
    }

    private SQLManager handleCreateUser(String username, String password, String studentId, String email) {
        SQLManager manager = new SQLManager(applicationId);

        if (manager.connect()) {
            List<UUID> applicationIds = manager.checkAllDeviceIds();
            if (!applicationIds.contains(applicationId)) {
                if (manager.createUserAccount(username, email, password, studentId)) {
                    Log.d(TAG, "Account created");
                } else {
                    Log.d(TAG, "Something went wrong when creating a new account.");
                    return null;
                }
            } else {
                AOUtil.alertUser(activity, "Account Creation",
                        "An account is already register to this device. " +
                                "You cannot have multiple accounts on a single device.");
                return null;
            }
        } else {
            Log.d(TAG, "Something went wrong when connecting to the database.");
            return null;
        }

        return manager;
    }

    private void navigateToLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private boolean passwordsMatch(View password, View passwordC) {
        String pwd = ((EditText) password).getText().toString();
        String pwdC = ((EditText) passwordC).getText().toString();
        System.out.println("Password: " + pwd);
        System.out.println("Password Confirm: " + pwdC);
        return pwd.equals(pwdC);
    }
}

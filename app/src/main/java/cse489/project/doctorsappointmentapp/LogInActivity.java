package cse489.project.doctorsappointmentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.app.ActivityManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView signup, doctorbtn;
    private Button loginBtn;
    private CheckBox rememberMe;
    private boolean passwordVisible;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        Boolean checkbox = preferences.getBoolean("remember", false);
        if (checkbox) {
            email.setText(preferences.getString("email", ""));
            password.setText(preferences.getString("password", ""));
            rememberMe.setChecked(true);
        } else  {
            Toast.makeText(this, "Please Sign In", Toast.LENGTH_SHORT).show();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(LogInActivity.this, Homepage.class);
            startActivity(i);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signupNow);
        progressbar = findViewById(R.id.progressBar);
        doctorbtn = findViewById(R.id.doctorbtn);
        rememberMe = findViewById(R.id.rememberMe);

        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rememberMe.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("email", email.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putBoolean("remember", true);
                    editor.apply();
                }
            }
        });
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= password.getRight() - password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = password.getSelectionEnd();
                        if (passwordVisible) {
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.baseline_visibility_off_24, 0);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;

                        } else {

                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24, 0, R.drawable.baseline_remove_red_eye_24, 0);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String Password = password.getText().toString();
                String validationMessage = validateInputs(userEmail, Password);


                if (!validationMessage.isEmpty()) {
                    Toast.makeText(LogInActivity.this, validationMessage, Toast.LENGTH_SHORT).show();
                } else {
                    progressbar.setVisibility(View.VISIBLE);
                    loginBtn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mAuth.signInWithEmailAndPassword(userEmail, Password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LogInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(LogInActivity.this, Homepage.class);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }
                                            progressbar.setVisibility(View.INVISIBLE);
                                            loginBtn.setBackgroundColor(Color.parseColor("#FF000000"));
                                        }
                                    });

                        }
                    }, 2000);
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });
        doctorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, DoctorSignUp.class));
            }
        });


    }

    private String validateInputs(String email, String password) {
        boolean isEmailValid = email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        boolean isPasswordValid = password.matches("^(?=.*[a-zA-Z])(?=.*\\d).{6,}$");
        boolean empty = password.isEmpty() || email.isEmpty();
        if (empty) {
            return "You have to fill all the inputs correctly.";
        }
        if (!isEmailValid) {
            return "Invalid Email.";
        } else if (!isPasswordValid) {
            return "Invalid Password.";
        }

        return "";
    }

}
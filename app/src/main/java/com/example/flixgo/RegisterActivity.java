package com.example.flixgo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnRegister;
    TextView tvLogin;

    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Enter email");
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Enter password");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        startActivity(
                                new Intent(
                                        RegisterActivity.this,
                                        LoginActivity.class
                                )
                        );

                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    RegisterActivity.this,
                                    e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show());
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    )
            );
            finish();
        });
    }
}
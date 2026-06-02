package com.example.flixgo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnRegister);
        tvRegister = findViewById(R.id.tvLogin);

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {

                        Toast.makeText(this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        startActivity(
                                new Intent(
                                        LoginActivity.this,
                                        MainActivity.class));

                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        });

        tvRegister.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                LoginActivity.this,
                                RegisterActivity.class)));
    }
}
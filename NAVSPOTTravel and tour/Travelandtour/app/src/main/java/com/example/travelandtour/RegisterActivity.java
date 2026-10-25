package com.example.travelandtour;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    EditText nameInput, emailInput, passwordInput;
    Button registerButton;
    TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.name);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        Button backButton = findViewById(R.id.backButton);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString()
                    .trim().toLowerCase();
            String password = passwordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty()
                    || password.isEmpty()) {
                Toast.makeText(this, "Complete all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
                return;
            }

            if (password.length() < 6) {
                passwordInput.setError(
                        "Password must be at least 6 characters");
                return;
            }

            SharedPreferences prefs =
                    getSharedPreferences("TouristApp", MODE_PRIVATE);

            if (prefs.contains("password_" + email)) {
                emailInput.setError("Email is already registered");
                return;
            }

            prefs.edit()
                    .putString("name_" + email, name)
                    .putString("password_" + email, password)
                    .apply();

            Toast.makeText(this, "Registration successful!",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    RegisterActivity.this, MainActivity.class));
            finish();
        });

        loginLink.setOnClickListener(v -> finish());
    }
}

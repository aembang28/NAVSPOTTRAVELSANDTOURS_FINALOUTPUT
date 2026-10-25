
package com.example.travelandtour;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends Activity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        Button backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        SharedPreferences prefs =
                getSharedPreferences("TouristApp", MODE_PRIVATE);

        if (!prefs.contains("password_admin@gmail.com")) {
            prefs.edit()
                    .putString("name_admin@gmail.com", "Admin")
                    .putString("password_admin@gmail.com", "admin123")
                    .putBoolean("isAdmin_admin@gmail.com", true)
                    .apply();
        }

        if (!prefs.contains("password_admin@travelandtour.com")) {
            prefs.edit()
                    .putString("name_admin@travelandtour.com", "Admin")
                    .putString("password_admin@travelandtour.com", "admin123")
                    .putBoolean("isAdmin_admin@travelandtour.com", true)
                    .apply();
        }

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString()
                    .trim().toLowerCase();
            String password = passwordInput.getText().toString();

            String savedPassword =
                    prefs.getString("password_" + email, null);

            if (Objects.equals(savedPassword, password)) {

                String name = prefs.getString("name_" + email, "Traveler");
                boolean isAdmin = prefs.getBoolean("isAdmin_" + email, false);

                Intent intent = new Intent(
                        MainActivity.this, DashboardActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("isAdmin", isAdmin);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button adminLoginButton = findViewById(R.id.adminLoginButton);
        if (adminLoginButton != null) {
            adminLoginButton.setOnClickListener(v -> {
                emailInput.setText("admin@gmail.com");
                passwordInput.setText("");
                passwordInput.requestFocus();
                Toast.makeText(MainActivity.this,
                        "Admin email loaded. Enter password to log in.",
                        Toast.LENGTH_SHORT).show();
            });
        }

        registerLink.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this, RegisterActivity.class)));
    }
}
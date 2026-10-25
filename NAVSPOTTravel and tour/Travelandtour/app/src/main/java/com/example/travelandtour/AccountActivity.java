package com.example.travelandtour;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AccountActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Button backToDashboardBtn = findViewById(R.id.backToDashboardBtn);
        Button logoutBtn = findViewById(R.id.logoutBtn);
        TextView avatarEmoji = findViewById(R.id.avatarEmoji);
        TextView accountNameText = findViewById(R.id.accountNameText);
        TextView accountEmailText = findViewById(R.id.accountEmailText);
        TextView accountRoleText = findViewById(R.id.accountRoleText);
        TextView accountStatusText = findViewById(R.id.accountStatusText);
        TextView totalBookingsStatText = findViewById(R.id.totalBookingsStatText);
        TextView totalSpentStatText = findViewById(R.id.totalSpentStatText);
        LinearLayout accountBookingsContainer = findViewById(R.id.accountBookingsContainer);
        Button returnDashboardBtn = findViewById(R.id.returnDashboardBtn);
        Button switchAccountBtn = findViewById(R.id.switchAccountBtn);
        Button registerAccBtn = findViewById(R.id.registerAccBtn);

        SharedPreferences prefs = getSharedPreferences("TouristApp", MODE_PRIVATE);

        String intentName = getIntent().getStringExtra("name");
        String intentEmail = getIntent().getStringExtra("email");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        final String currentName = (intentName != null && !intentName.trim().isEmpty()) ? intentName : "Traveler";
        final String userEmail = (intentEmail != null && !intentEmail.trim().isEmpty()) ? intentEmail : "traveler@app.com";
        final String name = currentName;

        final boolean isGuestUser = !isAdmin && (userEmail.toLowerCase().startsWith("guest@")
                || userEmail.equalsIgnoreCase("traveler@app.com")
                || name.equalsIgnoreCase("Guest Traveler")
                || !prefs.contains("password_" + userEmail));

        accountNameText.setText(name);
        accountEmailText.setText(userEmail);

        if (avatarEmoji != null) {
            avatarEmoji.setVisibility(View.GONE);
        }

        if (isAdmin) {
            accountRoleText.setText("Administrator Account");
            accountRoleText.setTextColor(0xFF087F5B);
            accountStatusText.setText("System Administrator • Full Records Access");
        } else if (isGuestUser) {
            accountRoleText.setText("Guest Browsing Mode");
            accountRoleText.setTextColor(0xFFD9534F);
            accountStatusText.setText("Guest Account • Registration Required to Book");
        } else {
            accountRoleText.setText("Standard Traveler Account");
            accountRoleText.setTextColor(0xFF28A745);
            accountStatusText.setText("Registered Traveler • Active Account");
        }

        // Load Bookings for this Account
        Map<String, ?> allEntries = prefs.getAll();
        List<String> accountBookingKeys = new ArrayList<>();
        for (String k : allEntries.keySet()) {
            if (k.startsWith("booking_")) {
                accountBookingKeys.add(k);
            }
        }
        accountBookingKeys.sort(Collections.reverseOrder());

        int bookingCount = 0;
        int totalSpent = 0;

        for (String key : accountBookingKeys) {
            String bookingData = String.valueOf(allEntries.get(key));
            String[] parts = bookingData.split("\\|");

            String bookerEmail = parts.length > 0 ? parts[0] : "";
            if (isAdmin || bookerEmail.equalsIgnoreCase(userEmail)) {
                bookingCount++;

                String bookerName = parts.length > 1 ? parts[1] : "Traveler";
                String destinationName = parts.length > 2 ? parts[2] : "Destination";
                String destinationLoc = parts.length > 3 ? parts[3] : "";
                String pricePaid = parts.length > 4 ? parts[4] : "₱5,000.00";
                String payMethod = parts.length > 5 ? parts[5] : "Online Payment";
                String receiptId = parts.length > 6 ? parts[6] : "REC-UNKNOWN";
                String dateTime = parts.length > 7 ? parts[7] : "";
                String ticketsQtyStr = parts.length > 8 ? parts[8] : "1";

                int qty = 1;
                try {
                    qty = Integer.parseInt(ticketsQtyStr);
                } catch (Exception ignored) {
                }
                totalSpent += (qty * 5000);

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 16, 20, 16);
                card.setBackgroundColor(0xFFFFFFFF);

                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 12);
                card.setLayoutParams(cardParams);

                TextView destView = new TextView(this);
                destView.setText(destinationName + " (" + destinationLoc + ")");
                destView.setTextSize(16);
                destView.setTextColor(0xFF087F5B);
                destView.setTypeface(null, Typeface.BOLD);

                TextView bookerInfo = new TextView(this);
                bookerInfo.setText("Booked By: " + bookerName + " (" + bookerEmail + ")");
                bookerInfo.setTextSize(13);
                bookerInfo.setTextColor(0xFF333333);

                TextView qtyInfo = new TextView(this);
                qtyInfo.setText("Tickets / Guests: " + qty + " Person(s)");
                qtyInfo.setTextSize(13);
                qtyInfo.setTextColor(0xFF555555);

                TextView paymentInfo = new TextView(this);
                paymentInfo.setText("Paid: " + pricePaid + " via " + payMethod);
                paymentInfo.setTextSize(14);
                paymentInfo.setTextColor(0xFF28A745);
                paymentInfo.setTypeface(null, Typeface.BOLD);

                TextView receiptInfo = new TextView(this);
                receiptInfo.setText("Receipt No: " + receiptId + (dateTime.isEmpty() ? "" : " [" + dateTime + "]"));
                receiptInfo.setTextSize(12);
                receiptInfo.setTextColor(0xFF777777);

                TextView statusView = new TextView(this);
                statusView.setText("STATUS: CONFIRMED BOOKING");
                statusView.setTextSize(12);
                statusView.setTextColor(0xFF28A745);
                statusView.setTypeface(null, Typeface.BOLD);

                card.addView(destView);
                if (isAdmin) {
                    card.addView(bookerInfo);
                }
                card.addView(qtyInfo);
                card.addView(paymentInfo);
                card.addView(receiptInfo);
                card.addView(statusView);

                accountBookingsContainer.addView(card);
            }
        }

        totalBookingsStatText.setText(bookingCount + " Tour(s)");
        totalSpentStatText.setText("₱" + String.format(Locale.US, "%,d", totalSpent) + ".00");

        if (bookingCount == 0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No active tour bookings or receipts found for this account.");
            emptyText.setTextSize(14);
            emptyText.setTextColor(0xFF777777);
            emptyText.setPadding(0, 0, 0, 12);
            accountBookingsContainer.addView(emptyText);
        }

        // Click listeners
        final boolean currentIsAdmin = isAdmin;

        Runnable openDashboard = () -> {
            Intent intent = new Intent(AccountActivity.this, DashboardActivity.class);
            intent.putExtra("name", currentName);
            intent.putExtra("email", userEmail);
            intent.putExtra("isAdmin", currentIsAdmin);
            startActivity(intent);
            finish();
        };

        backToDashboardBtn.setOnClickListener(v -> openDashboard.run());
        returnDashboardBtn.setOnClickListener(v -> openDashboard.run());

        switchAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, MainActivity.class);
            startActivity(intent);
        });

        registerAccBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        if (logoutBtn != null) {
            if (isGuestUser) {
                logoutBtn.setText("LOGIN");
                logoutBtn.setBackgroundTintList(ColorStateList.valueOf(0xFF087F5B));
                logoutBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            } else {
                logoutBtn.setText("LOGOUT");
                logoutBtn.setBackgroundTintList(ColorStateList.valueOf(0xFFD9534F));
                logoutBtn.setOnClickListener(v -> {
                    Toast.makeText(AccountActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }
    }
}

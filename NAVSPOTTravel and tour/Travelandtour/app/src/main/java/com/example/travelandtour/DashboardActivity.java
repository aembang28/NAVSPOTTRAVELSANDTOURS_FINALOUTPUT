package com.example.travelandtour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView welcomeText = findViewById(R.id.welcomeText);
        LinearLayout mainLayout = findViewById(R.id.destinationList);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button backButton = findViewById(R.id.backButton);

        SharedPreferences prefs = getSharedPreferences("TouristApp", MODE_PRIVATE);

        String intentName = getIntent().getStringExtra("name");
        String intentEmail = getIntent().getStringExtra("email");
        final boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        final String name = (intentName != null && !intentName.trim().isEmpty()) ? intentName : "Traveler";
        final String userEmail = (intentEmail != null && !intentEmail.trim().isEmpty()) ? intentEmail : "traveler@app.com";
        final String currentName = name;
        final boolean currentIsAdmin = isAdmin;

        if (backButton != null) {
            backButton.setText("ACCOUNT");
            backButton.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, AccountActivity.class);
                intent.putExtra("name", currentName);
                intent.putExtra("email", userEmail);
                intent.putExtra("isAdmin", currentIsAdmin);
                startActivity(intent);
            });
        }

        final boolean isGuestUser = !isAdmin && (userEmail.toLowerCase().startsWith("guest@")
                || userEmail.equalsIgnoreCase("traveler@app.com")
                || name.equalsIgnoreCase("Guest Traveler")
                || !prefs.contains("password_" + userEmail));

        if (logoutButton != null) {
            if (isGuestUser) {
                logoutButton.setText("LOGIN");
                logoutButton.setBackgroundTintList(ColorStateList.valueOf(0xFF087F5B));
                logoutButton.setOnClickListener(v -> {
                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    startActivity(intent);
                });
            } else {
                logoutButton.setText("LOGOUT");
                logoutButton.setBackgroundTintList(ColorStateList.valueOf(0xFFD9534F));
                logoutButton.setOnClickListener(v -> {
                    Toast.makeText(DashboardActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        }

        if (isAdmin) {
            welcomeText.setText("Welcome, " + name + " (Admin)!");
        } else if (isGuestUser) {
            welcomeText.setText("Welcome, " + name + " (Guest)!");
        } else {
            welcomeText.setText("Welcome, " + name + "!");
        }

        boolean newBookingConfirmed = getIntent().getBooleanExtra("newBookingConfirmed", false);
        if (newBookingConfirmed) {
            String receiptId = getIntent().getStringExtra("receiptId");
            String destination = getIntent().getStringExtra("destination");
            String price = getIntent().getStringExtra("price");
            int quantity = getIntent().getIntExtra("quantity", 1);

            LinearLayout successCard = new LinearLayout(this);
            successCard.setOrientation(LinearLayout.VERTICAL);
            successCard.setPadding(24, 20, 24, 20);
            successCard.setBackgroundColor(0xFFE6F4EA);
            successCard.setElevation(3.0f);

            LinearLayout.LayoutParams successParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            successParams.setMargins(0, 0, 0, 16);
            successCard.setLayoutParams(successParams);

            TextView successTitle = new TextView(this);
            successTitle.setText("BOOKING CONFIRMED & RECORDED!");
            successTitle.setTextSize(18);
            successTitle.setTextColor(0xFF28A745);
            successTitle.setTypeface(null, Typeface.BOLD);

            TextView successDetails = new TextView(this);
            successDetails.setText("Destination: " + destination + "\n" +
                    "Tickets/Guests: " + quantity + " Person(s)\n" +
                    "Total Amount: " + price + "\n" +
                    "Receipt ID: " + receiptId + "\n" +
                    "Status: CONFIRMED & PAID");
            successDetails.setTextSize(14);
            successDetails.setTextColor(0xFF333333);
            successDetails.setPadding(0, 6, 0, 0);

            successCard.addView(successTitle);
            successCard.addView(successDetails);
            mainLayout.addView(successCard);

            Toast.makeText(this, "Tour Booking Confirmed & Recorded!", Toast.LENGTH_LONG).show();
            getIntent().removeExtra("newBookingConfirmed");
        }

        if (isAdmin) {
            // ADMIN SECTION 1: Registered User Records
            TextView userRecordsHeader = new TextView(this);
            userRecordsHeader.setText("Registered Users Record");
            userRecordsHeader.setTextSize(20);
            userRecordsHeader.setTextColor(0xFF087F5B);
            userRecordsHeader.setTypeface(null, Typeface.BOLD);
            userRecordsHeader.setPadding(0, 0, 0, 12);
            mainLayout.addView(userRecordsHeader);

            Map<String, ?> allEntries = prefs.getAll();
            int userCount = 0;
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("name_")) {
                    userCount++;
                    String uEmail = key.substring(5);
                    String uName = entry.getValue().toString();
                    boolean userIsAdmin = prefs.getBoolean("isAdmin_" + uEmail, false);

                    LinearLayout userCard = new LinearLayout(this);
                    userCard.setOrientation(LinearLayout.VERTICAL);
                    userCard.setPadding(20, 16, 20, 16);
                    userCard.setBackgroundColor(0xFFFFFFFF);
                    userCard.setElevation(2.0f);

                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, 12);
                    userCard.setLayoutParams(cardParams);

                    TextView nameView = new TextView(this);
                    nameView.setText("Name: " + uName);
                    nameView.setTextSize(16);
                    nameView.setTextColor(0xFF333333);
                    nameView.setTypeface(null, Typeface.BOLD);

                    TextView emailView = new TextView(this);
                    emailView.setText("Email: " + uEmail);
                    emailView.setTextSize(14);
                    emailView.setTextColor(0xFF555555);

                    TextView roleView = new TextView(this);
                    roleView.setText("Role: " + (userIsAdmin ? "Administrator" : "Standard User"));
                    roleView.setTextSize(14);
                    roleView.setTextColor(userIsAdmin ? 0xFF087F5B : 0xFF777777);

                    userCard.addView(nameView);
                    userCard.addView(emailView);
                    userCard.addView(roleView);

                    mainLayout.addView(userCard);
                }
            }

            if (userCount == 0) {
                TextView noUsersView = new TextView(this);
                noUsersView.setText("No registered users found.");
                noUsersView.setTextSize(14);
                noUsersView.setTextColor(0xFF777777);
                noUsersView.setPadding(0, 0, 0, 16);
                mainLayout.addView(noUsersView);
            }

            // ADMIN SECTION 2: Tour Booking Records
            TextView bookingsHeader = new TextView(this);
            bookingsHeader.setText("All Tour Booking & Payment Records");
            bookingsHeader.setTextSize(20);
            bookingsHeader.setTextColor(0xFF087F5B);
            bookingsHeader.setTypeface(null, Typeface.BOLD);
            bookingsHeader.setPadding(0, 16, 0, 12);
            mainLayout.addView(bookingsHeader);

            List<String> adminBookingKeys = new ArrayList<>();
            for (String k : allEntries.keySet()) {
                if (k.startsWith("booking_")) {
                    adminBookingKeys.add(k);
                }
            }
            adminBookingKeys.sort(Collections.reverseOrder());

            int bookingCount = 0;
            for (String key : adminBookingKeys) {
                bookingCount++;
                String bookingData = String.valueOf(allEntries.get(key));
                String[] parts = bookingData.split("\\|");

                String bookerEmail = parts.length > 0 ? parts[0] : "Unknown";
                String bookerName = parts.length > 1 ? parts[1] : "Traveler";
                String destinationName = parts.length > 2 ? parts[2] : "Destination";
                String destinationLoc = parts.length > 3 ? parts[3] : "";
                String pricePaid = parts.length > 4 ? parts[4] : "₱5,000.00";
                String payMethod = parts.length > 5 ? parts[5] : "Online Payment";
                String receiptId = parts.length > 6 ? parts[6] : "REC-UNKNOWN";
                String ticketsQty = parts.length > 8 ? parts[8] : "1";

                LinearLayout bookingCard = new LinearLayout(this);
                bookingCard.setOrientation(LinearLayout.VERTICAL);
                bookingCard.setPadding(20, 16, 20, 16);
                bookingCard.setBackgroundColor(0xFFFFFFFF);
                bookingCard.setElevation(2.0f);

                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardParams.setMargins(0, 0, 0, 12);
                bookingCard.setLayoutParams(cardParams);

                TextView destView = new TextView(this);
                destView.setText("Destination: " + destinationName + " (" + destinationLoc + ")");
                destView.setTextSize(16);
                destView.setTextColor(0xFF087F5B);
                destView.setTypeface(null, Typeface.BOLD);

                TextView bookerInfo = new TextView(this);
                bookerInfo.setText("Booked By: " + bookerName + " (" + bookerEmail + ")");
                bookerInfo.setTextSize(14);
                bookerInfo.setTextColor(0xFF333333);

                TextView qtyInfo = new TextView(this);
                qtyInfo.setText("Quantity: " + ticketsQty + " Ticket(s) / Guest(s) [Max 100 Limit]");
                qtyInfo.setTextSize(13);
                qtyInfo.setTextColor(0xFF555555);

                TextView paymentInfo = new TextView(this);
                paymentInfo.setText("Paid: " + pricePaid + " via " + payMethod + " [Receipt: " + receiptId + "]");
                paymentInfo.setTextSize(14);
                paymentInfo.setTextColor(0xFF28A745);
                paymentInfo.setTypeface(null, Typeface.BOLD);

                TextView statusView = new TextView(this);
                statusView.setText("STATUS: PAID & CONFIRMED");
                statusView.setTextSize(13);
                statusView.setTextColor(0xFF087F5B);
                statusView.setTypeface(null, Typeface.BOLD);
                statusView.setPadding(0, 0, 0, 8);

                Button deleteBtn = new Button(this);
                deleteBtn.setText("DELETE RECORD");
                deleteBtn.setTextColor(0xFFFFFFFF);
                deleteBtn.setBackgroundTintList(
                        ColorStateList.valueOf(0xFFD9534F));

                final String bookingKey = key;
                deleteBtn.setOnClickListener(v -> {
                    prefs.edit().remove(bookingKey).apply();
                    Toast.makeText(DashboardActivity.this,
                            "Booking record removed.",
                            Toast.LENGTH_SHORT).show();
                    recreate();
                });

                bookingCard.addView(destView);
                bookingCard.addView(bookerInfo);
                bookingCard.addView(qtyInfo);
                bookingCard.addView(paymentInfo);
                bookingCard.addView(statusView);
                bookingCard.addView(deleteBtn);

                mainLayout.addView(bookingCard);
            }

            if (bookingCount == 0) {
                TextView noBookingsView = new TextView(this);
                noBookingsView.setText("No tour bookings recorded yet.");
                noBookingsView.setTextSize(14);
                noBookingsView.setTextColor(0xFF777777);
                noBookingsView.setPadding(0, 0, 0, 16);
                mainLayout.addView(noBookingsView);
            }

            // ADMIN SECTION 3: Destinations Header
            TextView destinationsHeader = new TextView(this);
            destinationsHeader.setText("Tourist Destinations");
            destinationsHeader.setTextSize(20);
            destinationsHeader.setTextColor(0xFF087F5B);
            destinationsHeader.setTypeface(null, Typeface.BOLD);
            destinationsHeader.setPadding(0, 16, 0, 12);
            mainLayout.addView(destinationsHeader);
        } else {
            // USER SECTION 1: My Profile Record
            TextView profileHeader = new TextView(this);
            profileHeader.setText("My User Profile Record");
            profileHeader.setTextSize(20);
            profileHeader.setTextColor(0xFF087F5B);
            profileHeader.setTypeface(null, Typeface.BOLD);
            profileHeader.setPadding(0, 0, 0, 12);
            mainLayout.addView(profileHeader);

            LinearLayout profileCard = new LinearLayout(this);
            profileCard.setOrientation(LinearLayout.VERTICAL);
            profileCard.setPadding(20, 16, 20, 16);
            profileCard.setBackgroundColor(0xFFFFFFFF);
            profileCard.setElevation(2.0f);

            LinearLayout.LayoutParams profileParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            profileParams.setMargins(0, 0, 0, 16);
            profileCard.setLayoutParams(profileParams);

            TextView profileNameView = new TextView(this);
            profileNameView.setText("Full Name: " + name);
            profileNameView.setTextSize(16);
            profileNameView.setTextColor(0xFF333333);
            profileNameView.setTypeface(null, Typeface.BOLD);

            TextView profileEmailView = new TextView(this);
            profileEmailView.setText("Email: " + userEmail);
            profileEmailView.setTextSize(14);
            profileEmailView.setTextColor(0xFF555555);

            TextView profileTypeView = new TextView(this);
            profileTypeView.setTextSize(14);

            if (isGuestUser) {
                profileTypeView.setText("Account Type: Guest Browsing Mode");
                profileTypeView.setTextColor(0xFFD9534F);

                TextView noticeView = new TextView(this);
                noticeView.setText("Notice: Please create an account or log in before booking tours.");
                noticeView.setTextSize(13);
                noticeView.setTextColor(0xFFD9534F);
                noticeView.setTypeface(null, Typeface.BOLD);
                noticeView.setPadding(0, 6, 0, 8);

                Button createAccBtn = new Button(this);
                createAccBtn.setText("CREATE ACCOUNT TO BOOK");
                createAccBtn.setTextColor(0xFFFFFFFF);
                createAccBtn.setBackgroundTintList(ColorStateList.valueOf(0xFF087F5B));
                createAccBtn.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, RegisterActivity.class)));

                profileCard.addView(profileNameView);
                profileCard.addView(profileEmailView);
                profileCard.addView(profileTypeView);
                profileCard.addView(noticeView);
                profileCard.addView(createAccBtn);
            } else {
                profileTypeView.setText("Account Type: Standard Traveler Account");
                profileTypeView.setTextColor(0xFF087F5B);

                profileCard.addView(profileNameView);
                profileCard.addView(profileEmailView);
                profileCard.addView(profileTypeView);
            }
            mainLayout.addView(profileCard);

            // USER SECTION 2: My Tour Bookings & Payment Receipts
            TextView userBookingsHeader = new TextView(this);
            userBookingsHeader.setText("My Tour Bookings & Receipts");
            userBookingsHeader.setTextSize(20);
            userBookingsHeader.setTextColor(0xFF087F5B);
            userBookingsHeader.setTypeface(null, Typeface.BOLD);
            userBookingsHeader.setPadding(0, 8, 0, 12);
            mainLayout.addView(userBookingsHeader);

            Map<String, ?> allEntries = prefs.getAll();
            List<String> userBookingKeys = new ArrayList<>();
            for (String k : allEntries.keySet()) {
                if (k.startsWith("booking_")) {
                    userBookingKeys.add(k);
                }
            }
            userBookingKeys.sort(Collections.reverseOrder());

            int userBookingCount = 0;
            for (String key : userBookingKeys) {
                String bookingData = String.valueOf(allEntries.get(key));
                String[] parts = bookingData.split("\\|");

                String bookerEmail = parts.length > 0 ? parts[0] : "";
                if (bookerEmail.equalsIgnoreCase(userEmail)) {
                    userBookingCount++;

                    String destinationName = parts.length > 2 ? parts[2] : "Destination";
                    String destinationLoc = parts.length > 3 ? parts[3] : "";
                    String pricePaid = parts.length > 4 ? parts[4] : "₱5,000.00";
                    String payMethod = parts.length > 5 ? parts[5] : "Online Payment";
                    String receiptId = parts.length > 6 ? parts[6] : "REC-UNKNOWN";
                    String dateTime = parts.length > 7 ? parts[7] : "";
                    String ticketsQty = parts.length > 8 ? parts[8] : "1";

                    LinearLayout uBookingCard = new LinearLayout(this);
                    uBookingCard.setOrientation(LinearLayout.VERTICAL);
                    uBookingCard.setPadding(20, 16, 20, 16);
                    uBookingCard.setBackgroundColor(0xFFFFFFFF);
                    uBookingCard.setElevation(2.0f);

                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, 12);
                    uBookingCard.setLayoutParams(cardParams);

                    TextView destView = new TextView(this);
                    destView.setText(destinationName + " (" + destinationLoc + ")");
                    destView.setTextSize(16);
                    destView.setTextColor(0xFF087F5B);
                    destView.setTypeface(null, Typeface.BOLD);

                    TextView qtyInfo = new TextView(this);
                    qtyInfo.setText("Tickets / Guests: " + ticketsQty + " Person(s)");
                    qtyInfo.setTextSize(14);
                    qtyInfo.setTextColor(0xFF333333);

                    TextView paymentInfo = new TextView(this);
                    paymentInfo.setText("Total Amount Paid: " + pricePaid + " via " + payMethod);
                    paymentInfo.setTextSize(14);
                    paymentInfo.setTextColor(0xFF28A745);
                    paymentInfo.setTypeface(null, Typeface.BOLD);

                    TextView receiptInfo = new TextView(this);
                    receiptInfo.setText("Receipt No: " + receiptId + (dateTime.isEmpty() ? "" : " [" + dateTime + "]"));
                    receiptInfo.setTextSize(13);
                    receiptInfo.setTextColor(0xFF555555);

                    TextView statusView = new TextView(this);
                    statusView.setText("STATUS: CONFIRMED BOOKING");
                    statusView.setTextSize(13);
                    statusView.setTextColor(0xFF28A745);
                    statusView.setTypeface(null, Typeface.BOLD);

                    uBookingCard.addView(destView);
                    uBookingCard.addView(qtyInfo);
                    uBookingCard.addView(paymentInfo);
                    uBookingCard.addView(receiptInfo);
                    uBookingCard.addView(statusView);

                    mainLayout.addView(uBookingCard);
                }
            }

            if (userBookingCount == 0) {
                TextView noUserBookingsView = new TextView(this);
                noUserBookingsView.setText("You have no active tour bookings yet. Select a destination below to book!");
                noUserBookingsView.setTextSize(14);
                noUserBookingsView.setTextColor(0xFF777777);
                noUserBookingsView.setPadding(0, 0, 0, 16);
                mainLayout.addView(noUserBookingsView);
            }

            // USER SECTION 3: Destinations Header
            TextView userDestHeader = new TextView(this);
            userDestHeader.setText("Available Tourist Destinations");
            userDestHeader.setTextSize(20);
            userDestHeader.setTextColor(0xFF087F5B);
            userDestHeader.setTypeface(null, Typeface.BOLD);
            userDestHeader.setPadding(0, 12, 0, 12);
            mainLayout.addView(userDestHeader);
        }

        String[][] destinations = {
                {"Boracay Island", "Aklan",
                        "Enjoy world-famous white sand beaches, water sports, and sunset cruises."},
                {"Chocolate Hills", "Bohol",
                        "Explore 1,200+ symmetrical grass-covered limestone hills and tarsier sanctuaries."},
                {"Mayon Volcano", "Albay",
                        "Marvel at the world's most perfectly symmetrical cone volcano and ATV adventure tours."},
                {"Banaue Rice Terraces", "Ifugao",
                        "Discover 2,000-year-old mountain terraces carved by ancestors."},
                {"El Nido Lagoons", "Palawan",
                        "Explore limestone cliffs, hidden beaches, and emerald green island lagoons."},
                {"Siargao Island", "Surigao del Norte",
                        "Surfing capital of the Philippines with island hopping, tide pools, and palm forests."},
                {"Puerto Princesa Underground River", "Palawan",
                        "Experience the UNESCO World Heritage underground river cave tour."},
                {"Calle Crisologo Vigan", "Ilocos Sur",
                        "Walk through historic cobblestone streets and Spanish colonial architecture."},
                {"Intramuros Historic Walled City", "Manila",
                        "Tour ancient stone fortresses, San Agustin Church, and Fort Santiago."},
                {"Coron Kayangan Lake", "Palawan",
                        "Swim in pristine crystal lakes, coral gardens, and WWII shipwreck dive sites."},
                {"Batanes Rolling Hills", "Batanes",
                        "Experience breathtaking lighthouses, stone houses, and panoramic ocean cliffs."},
                {"Camiguin Island", "Camiguin",
                        "Discover Sunken Cemetery, White Island sandbar, and natural hot springs."}
        };

        for (String[] destination : destinations) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 18, 20, 18);
            card.setBackgroundColor(0xFFFFFFFF);
            card.setElevation(2.0f);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);

            TextView title = new TextView(this);
            title.setText(destination[0]);
            title.setTextSize(19);
            title.setTextColor(0xFF087F5B);
            title.setTypeface(null, Typeface.BOLD);

            TextView location = new TextView(this);
            location.setText(destination[1]);
            location.setTextSize(14);
            location.setTextColor(0xFF777777);

            TextView description = new TextView(this);
            description.setText(destination[2]);
            description.setTextSize(15);
            description.setTextColor(0xFF333333);
            description.setPadding(0, 6, 0, 6);

            int bookedSlots = countBookedTicketsForDestination(prefs, destination[0]);
            int availableSlots = Math.max(0, 100 - bookedSlots);

            TextView limitView = new TextView(this);
            limitView.setText("Booking Capacity: 100 Limit | Available: " + availableSlots + " / 100 Slots");
            limitView.setTextSize(13);
            limitView.setTextColor(availableSlots > 0 ? 0xFF087F5B : 0xFFD9534F);
            limitView.setTypeface(null, Typeface.BOLD);
            limitView.setPadding(0, 0, 0, 12);

            card.addView(title);
            card.addView(location);
            card.addView(description);
            card.addView(limitView);

            if (!isAdmin) {
                Button payBookBtn = new Button(this);
                if (availableSlots > 0) {
                    payBookBtn.setText("PROCEED TO PAYMENT & BOOK");
                    payBookBtn.setTextColor(0xFFFFFFFF);
                    payBookBtn.setBackgroundTintList(
                            ColorStateList.valueOf(0xFF087F5B));

                    payBookBtn.setOnClickListener(v -> {
                        if (isGuestUser) {
                            new AlertDialog.Builder(DashboardActivity.this)
                                    .setTitle("Account Required to Book")
                                    .setMessage("Please create an account or log in before proceeding to book tours and payment.")
                                    .setPositiveButton("CREATE ACCOUNT", (dialog, which) ->
                                            startActivity(new Intent(DashboardActivity.this, RegisterActivity.class)))
                                    .setNeutralButton("LOG IN", (dialog, which) ->
                                            startActivity(new Intent(DashboardActivity.this, MainActivity.class)))
                                    .setNegativeButton("CANCEL", null)
                                    .show();

                            Toast.makeText(DashboardActivity.this,
                                    "Please create an account or log in before booking!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        Intent intent = new Intent(DashboardActivity.this, PaymentActivity.class);
                        intent.putExtra("name", currentName);
                        intent.putExtra("email", userEmail);
                        intent.putExtra("destination", destination[0]);
                        intent.putExtra("location", destination[1]);
                        intent.putExtra("price", "₱5,000.00");
                        startActivity(intent);
                    });
                } else {
                    payBookBtn.setText("FULLY BOOKED (100 LIMIT REACHED)");
                    payBookBtn.setEnabled(false);
                    payBookBtn.setTextColor(0xFFFFFFFF);
                    payBookBtn.setBackgroundTintList(
                            ColorStateList.valueOf(0xFF888888));
                }

                card.addView(payBookBtn);
            }

            mainLayout.addView(card);
        }
    }

    private int countBookedTicketsForDestination(SharedPreferences prefs, String destName) {
        int count = 0;
        Map<String, ?> all = prefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (entry.getKey().startsWith("booking_")) {
                String[] parts = entry.getValue().toString().split("\\|");
                if (parts.length > 2 && parts[2].equalsIgnoreCase(destName)) {
                    int qty = 1;
                    if (parts.length > 8) {
                        try {
                            qty = Integer.parseInt(parts[8]);
                        } catch (Exception ignored) {
                        }
                    }
                    count += qty;
                }
            }
        }
        return count;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DashboardActivity.this, AccountActivity.class);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("isAdmin", getIntent().getBooleanExtra("isAdmin", false));
        startActivity(intent);
        finish();
    }
}

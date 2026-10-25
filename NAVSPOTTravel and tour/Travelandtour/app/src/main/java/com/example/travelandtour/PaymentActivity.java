package com.example.travelandtour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PaymentActivity extends Activity {

    private ImageView qrCodeImageView;
    private TextView qrMethodTitle, qrAccountNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Button backButton = findViewById(R.id.backButton);
        TextView destinationSummary = findViewById(R.id.destinationSummary);
        TextView customerSummary = findViewById(R.id.customerSummary);
        TextView priceSummary = findViewById(R.id.priceSummary);
        EditText ticketQuantityInput = findViewById(R.id.ticketQuantityInput);
        Spinner paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner);
        EditText accountNameInput = findViewById(R.id.accountNameInput);
        EditText accountNumberInput = findViewById(R.id.accountNumberInput);
        Button payButton = findViewById(R.id.payButton);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        qrMethodTitle = findViewById(R.id.qrMethodTitle);
        qrAccountNumber = findViewById(R.id.qrAccountNumber);

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String destination = getIntent().getStringExtra("destination");
        String location = getIntent().getStringExtra("location");

        if (name == null || name.trim().isEmpty()) name = "Traveler";
        if (email == null || email.trim().isEmpty()) email = "traveler@app.com";
        if (destination == null) destination = "Boracay Island";
        if (location == null) location = "Aklan";

        destinationSummary.setText("Destination: " + destination + " (" + location + ")");
        customerSummary.setText("Customer: " + name + " (" + email + ")");
        priceSummary.setText("Price per Ticket: ₱5,000.00 (Max 100 Limit)");

        String[] paymentMethods = {
                "GCash E-Wallet",
                "Maya E-Wallet (PayMaya)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, paymentMethods);
        paymentMethodSpinner.setAdapter(adapter);

        paymentMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String method = paymentMethods[position];
                String accountRef;

                if (method.contains("GCash")) {
                    accountNameInput.setHint("GCash Account Holder Name");
                    accountNumberInput.setHint("GCash Mobile Number (11 Digits)");
                    accountRef = "0917-888-9999 (GCash NAVSPOT)";
                } else {
                    accountNameInput.setHint("PayMaya Account Holder Name");
                    accountNumberInput.setHint("PayMaya Mobile Number (11 Digits)");
                    accountRef = "0918-777-6666 (PayMaya NAVSPOT)";
                }

                if (qrMethodTitle != null) {
                    qrMethodTitle.setText(method + " Payment QR Code");
                }
                if (qrAccountNumber != null) {
                    qrAccountNumber.setText("Account / Ref No: " + accountRef);
                }
                if (qrCodeImageView != null) {
                    Bitmap qrBitmap = generateQRCodeBitmap("PAYMENT|" + method + "|" + accountRef + "|NAVSPOT");
                    qrCodeImageView.setImageBitmap(qrBitmap);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final String finalName = name;
        final String finalEmail = email;
        final String finalDestination = destination;
        final String finalLocation = location;

        payButton.setOnClickListener(v -> {
            String quantityStr = ticketQuantityInput.getText().toString().trim();
            int ticketQuantity = 1;
            try {
                ticketQuantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                ticketQuantity = 1;
            }

            // Enforce 100 limit per booking
            if (ticketQuantity < 1 || ticketQuantity > 100) {
                Toast.makeText(this, "Booking limit is up to 100 tickets/guests per booking!", Toast.LENGTH_LONG).show();
                ticketQuantityInput.setError("Limit: 1 to 100 tickets");
                return;
            }

            String accountName = accountNameInput.getText().toString().trim();
            String accountNumber = accountNumberInput.getText().toString().trim();

            if (accountName.isEmpty() || accountNumber.isEmpty()) {
                Toast.makeText(this, "Please enter online bank/account payment details", Toast.LENGTH_SHORT).show();
                return;
            }

            int totalPrice = ticketQuantity * 5000;
            String calculatedPrice = "₱" + String.format(Locale.US, "%,d", totalPrice) + ".00";

            String selectedMethod = paymentMethodSpinner.getSelectedItem().toString();
            String receiptId = "REC-" + (100000 + new Random().nextInt(900000));
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // Save booking + payment record into SharedPreferences
            SharedPreferences prefs = getSharedPreferences("TouristApp", MODE_PRIVATE);
            String bookingKey = "booking_" + System.currentTimeMillis();
            String bookingValue = finalEmail + "|" + finalName + "|" + finalDestination + "|" + finalLocation + "|" + calculatedPrice + "|" + selectedMethod + "|" + receiptId + "|" + timeStamp + "|" + ticketQuantity;
            prefs.edit().putString(bookingKey, bookingValue).apply();

            // Display Receipt Dialog
            showReceiptDialog(receiptId, finalName, finalEmail, finalDestination, finalLocation, calculatedPrice, selectedMethod, timeStamp, ticketQuantity);
        });
    }

    private Bitmap generateQRCodeBitmap(String data) {
        int matrixSize = 25;
        int imageSize = 500;
        int moduleSize = imageSize / matrixSize;

        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, imageSize, imageSize, bgPaint);

        Paint darkPaint = new Paint();
        darkPaint.setColor(Color.parseColor("#087F5B"));

        boolean[][] modules = new boolean[matrixSize][matrixSize];

        drawFinderPattern(modules, 0, 0);
        drawFinderPattern(modules, matrixSize - 7, 0);
        drawFinderPattern(modules, 0, matrixSize - 7);

        int hash = data.hashCode();
        Random random = new Random(hash);

        for (int r = 0; r < matrixSize; r++) {
            for (int c = 0; c < matrixSize; c++) {
                if ((r < 8 && c < 8) || (r < 8 && c >= matrixSize - 8) || (r >= matrixSize - 8 && c < 8)) {
                    continue;
                }
                if (r >= matrixSize - 9 && r <= matrixSize - 5 && c >= matrixSize - 9 && c <= matrixSize - 5) {
                    int dr = r - (matrixSize - 7);
                    int dc = c - (matrixSize - 7);
                    modules[r][c] = (Math.abs(dr) == 2 || Math.abs(dc) == 2 || (dr == 0 && dc == 0));
                    continue;
                }
                if (r == 6 || c == 6) {
                    modules[r][c] = ((r + c) % 2 == 0);
                    continue;
                }
                modules[r][c] = random.nextBoolean();
            }
        }

        for (int r = 0; r < matrixSize; r++) {
            for (int c = 0; c < matrixSize; c++) {
                if (modules[r][c]) {
                    float left = c * moduleSize;
                    float top = r * moduleSize;
                    float right = left + moduleSize;
                    float bottom = top + moduleSize;
                    canvas.drawRect(left, top, right, bottom, darkPaint);
                }
            }
        }

        return bitmap;
    }

    private void drawFinderPattern(boolean[][] modules, int startRow, int startCol) {
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                int row = startRow + r;
                int col = startCol + c;
                if (r == 0 || r == 6 || c == 0 || c == 6) {
                    modules[row][col] = true;
                } else if (r >= 2 && r <= 4 && c >= 2 && c <= 4) {
                    modules[row][col] = true;
                } else {
                    modules[row][col] = false;
                }
            }
        }
    }

    private void showReceiptDialog(String receiptId, String name, String email, String destination, String location, String price, String paymentMethod, String timeStamp, int quantity) {
        StringBuilder receiptText = new StringBuilder();
        receiptText.append("OFFICIAL PAYMENT RECEIPT\n");
        receiptText.append("------------------------------------\n");
        receiptText.append("Receipt ID: ").append(receiptId).append("\n");
        receiptText.append("Date/Time: ").append(timeStamp).append("\n");
        receiptText.append("------------------------------------\n");
        receiptText.append("Customer: ").append(name).append("\n");
        receiptText.append("Email: ").append(email).append("\n");
        receiptText.append("Destination: ").append(destination).append(" (").append(location).append(")\n");
        receiptText.append("Tickets/Guests: ").append(quantity).append(" Person(s) [Max 100 Limit]\n");
        receiptText.append("Total Paid: ").append(price).append("\n");
        receiptText.append("Payment Channel: ").append(paymentMethod).append("\n");
        receiptText.append("------------------------------------\n");
        receiptText.append("STATUS: PAID & CONFIRMED\n");
        receiptText.append("\nThank you for booking with NAVSPOT Travels and Tours!");

        new AlertDialog.Builder(this)
                .setTitle("Online Payment Confirmed!")
                .setMessage(receiptText.toString())
                .setCancelable(false)
                .setPositiveButton("DONE / RETURN TO DASHBOARD", (dialog, which) -> {
                    Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("newBookingConfirmed", true);
                    intent.putExtra("receiptId", receiptId);
                    intent.putExtra("destination", destination);
                    intent.putExtra("price", price);
                    intent.putExtra("quantity", quantity);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}

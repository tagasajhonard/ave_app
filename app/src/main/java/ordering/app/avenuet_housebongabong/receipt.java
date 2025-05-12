package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.Manifest;


public class receipt extends AppCompatActivity {
    private TextView divider, tvOrderId, tvOrderTime, tvFullName, tvContact, tvAddress, tvDeliveryMethod, tvPaymentMethod, tvTotal;
    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_receipt);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderTime = findViewById(R.id.tvOrderTime);
        tvFullName = findViewById(R.id.tvFullName);
        tvAddress = findViewById(R.id.tvAddress);
        tvDeliveryMethod = findViewById(R.id.tvDeliveryMethod);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTotal = findViewById(R.id.tvTotal);
        itemsContainer = findViewById(R.id.itemsContainer);

        Button saveReceiptButton = findViewById(R.id.saveReceipt);
        LinearLayout receiptLayout = findViewById(R.id.receiptLayout);

        saveReceiptButton.setOnClickListener(view -> saveReceiptAsImage(receiptLayout));

        // Retrieve orderId and custName from Intent
        String orderId = getIntent().getStringExtra("orderId");
        String custName = getIntent().getStringExtra("custName");

        if (orderId != null && custName != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(custName).child(orderId);
            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, Object> orderData = (Map<String, Object>) dataSnapshot.getValue();
                        displayOrderData(orderData);
                    } else {
                        Toast.makeText(receipt.this, "Order not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(receipt.this, "Failed to load order", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Function to display order data
    private void displayOrderData(Map<String, Object> orderData) {
        tvOrderId.setText("Order ID: " + orderData.get("orderId"));
        tvOrderTime.setText("DATE: " + orderData.get("orderTime"));
        tvFullName.setText("Customer Name: " + orderData.get("fullName"));
        tvAddress.setText("Address: " + orderData.get("address"));
        tvDeliveryMethod.setText("Delivery Method: " + orderData.get("deliveryMethod"));
        tvPaymentMethod.setText("Payment Method: " + orderData.get("paymentMethod"));
        tvTotal.setText("Total: " + orderData.get("total"));

        // Display each item in the order
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        for (Map<String, Object> item : items) {
            addItemToContainer(item);
        }
    }

    @SuppressLint("DefaultLocale")
    private void addItemToContainer(Map<String, Object> item) {
        TextView itemDetails = new TextView(this);
        itemDetails.setText(String.format(
                "%s\nSize: %s\nQuantity: %d\nPrice: %s\nSugar Level: %d%%\nAddons: %s\n\n",
                item.get("productName"),
                item.get("size"),
                item.get("quantity"),
                item.get("productPrice"),
                item.get("sugarLevel"),
                getAddons((List<Map<String, Object>>) item.get("addons"))
        ));
        itemsContainer.addView(itemDetails);
    }

    private String getAddons(List<Map<String, Object>> addons) {
        if (addons == null || addons.isEmpty()) {
            return "None";  // Return "None" if there are no addons
        }

        StringBuilder addonsText = new StringBuilder();
        for (Map<String, Object> addon : addons) {
            addonsText.append(addon.get("name")).append(" (₱").append(addon.get("price")).append("), ");
        }
        return addonsText.length() > 0 ? addonsText.substring(0, addonsText.length() - 2) : "None";
    }
    private void saveReceiptAsImage(View view) {
        // Create a bitmap from the view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10+ (Scoped Storage)
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, "Receipt_" + System.currentTimeMillis() + ".png");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Receipts");

                // Insert the image and get an output stream
                fos = getContentResolver().openOutputStream(getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
            } else {
                // For Android 9 and below, save to external storage directory
                File storageDir = new File(Environment.getExternalStorageDirectory() + "/Receipts");
                if (!storageDir.exists()) storageDir.mkdirs();

                File file = new File(storageDir, "Receipt_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png");
                fos = new FileOutputStream(file);
            }

            // Compress and write the bitmap to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            Toast.makeText(this, "Receipt saved successfully!", Toast.LENGTH_SHORT).show();
            Intent intents = new Intent(receipt.this, OrdersListActivity.class);
            startActivity(intents);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save receipt", Toast.LENGTH_SHORT).show();
        }
    }
}

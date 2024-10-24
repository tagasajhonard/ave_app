package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class order_summary extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderSummaryAdapter adapter;
    List<CartItem> selectedItems;

    private static final long DOUBLE_PRESS_INTERVAL = 2000; // 2 seconds
    private boolean isBackPressedOnce = false;
    private Handler handler;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView gcashImageView;
    private TextView uploadgcash;

    private String orderId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        gcashImageView = findViewById(R.id.gcashImage);
        uploadgcash = findViewById(R.id.uploadgcash);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            boolean isLightBackground = true;
            window.setStatusBarColor(Color.TRANSPARENT);
            if (isLightBackground) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        uploadgcash.setOnClickListener(v -> openFileChooser());

        handler = new Handler();

        TextView subTotalTextView = findViewById(R.id.subTotalTextView);
        TextView placeOrder = findViewById(R.id.placeorder);


        recyclerView = findViewById(R.id.recyclerViewOrderSummary);
        selectedItems = new ArrayList<>();

        double subTotal = getIntent().getDoubleExtra("subTotal", 0.0);
        subTotalTextView.setText(String.format(Locale.getDefault(), " %.2f", subTotal));

//        if (selectedItems != null) {
//
//        }


        ArrayList<CartItem> selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");

        if (selectedItems != null && !selectedItems.isEmpty()) {
            OrderSummaryAdapter adapter = new OrderSummaryAdapter(this, selectedItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            Log.d("OrderSummary", "No items selected.");
        }


        RadioButton radioCOD = findViewById(R.id.cod);
        RadioButton radioGcash = findViewById(R.id.gcash);
        RadioButton radioCash = findViewById(R.id.cash);

        LinearLayout gcashInfo = findViewById(R.id.gcash_info);

// When Cash on Delivery (COD) is selected
        radioCOD.setOnClickListener(v -> {
            radioCOD.setChecked(true);
            radioGcash.setChecked(false);
            radioCash.setChecked(false);
            gcashInfo.setVisibility(View.GONE);
        });


        radioGcash.setOnClickListener(v -> {
            radioGcash.setChecked(true);
            radioCOD.setChecked(false);
            radioCash.setChecked(false);
            gcashInfo.setVisibility(View.VISIBLE);
        });

        radioCash.setOnClickListener(v -> {
            radioCash.setChecked(true);
            radioCOD.setChecked(false);
            radioGcash.setChecked(false);
            gcashInfo.setVisibility(View.GONE);
        });


        // Retrieve full name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String custname = sharedPreferences.getString("fullName", "No name found");
        String fullName = sharedPreferences.getString("username", "No name found");
        // Fetch address based on the name
        if (!fullName.equals("No name found")) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(fullName);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        String contact = snapshot.child("phoneNumber").getValue(String.class);

                        String town = snapshot.child("town").getValue(String.class);
                        String street = snapshot.child("street").getValue(String.class);
                        String sitioStreet = snapshot.child("sitioStreet").getValue(String.class);

                        String city = "Oriental Mindoro";
                        String fullAddress = sitioStreet + ", " + street + ", " + town + ", " + city;

                        // Display the address in the TextView
                        TextView addressTextView = findViewById(R.id.address);
                        TextView contactView = findViewById(R.id.contactNo);
                        TextView name = findViewById(R.id.Fname);

                        contactView.setText("(+63)" + contact);
                        addressTextView.setText(fullAddress);
                        name.setText(custname + " ");

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phoneNumber", contact);  // Save the contact
                        editor.apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle possible errors
                    Toast.makeText(order_summary.this, "Failed to load address", Toast.LENGTH_SHORT).show();
                }
            });
        }


        placeOrder.setOnClickListener(v -> {

            if (!radioCOD.isChecked() && !radioGcash.isChecked() && !radioCash.isChecked()) {
                Toast.makeText(order_summary.this, "Please choose a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedItems != null && !selectedItems.isEmpty()) {
                LayoutInflater inflater = LayoutInflater.from(order_summary.this);
                View passwordDialogView = inflater.inflate(R.layout.password_dialog, null);

                EditText inputPassword = passwordDialogView.findViewById(R.id.inputPassword);

                AlertDialog.Builder builder = new AlertDialog.Builder(order_summary.this);
                builder.setView(passwordDialogView);

                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    String enteredPassword = inputPassword.getText().toString();

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(fullName);
                    userRef.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String storedPassword = dataSnapshot.getValue(String.class);

                            if (storedPassword != null && storedPassword.equals(enteredPassword)) {
                                String contact = sharedPreferences.getString("phoneNumber", "No contact found");
                                String address = ((TextView) findViewById(R.id.address)).getText().toString();
                                String total = ((TextView) findViewById(R.id.subTotalTextView)).getText().toString();

                                String paymentMethod;
                                if (radioCOD.isChecked()) {
                                    paymentMethod = "Cash on Delivery";
                                } else if (radioCash.isChecked()) {
                                    paymentMethod = "Cash";
                                } else {
                                    paymentMethod = "GCash";
                                }

                                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
                                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("UserCart").child(custname);
                                DatabaseReference counterRef = FirebaseDatabase.getInstance().getReference("orderCounter/currentOrderId");
                                counterRef.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData currentData) {
                                        Long currentOrderId = currentData.getValue(Long.class);
                                        if (currentOrderId == null) {
                                            currentOrderId = 1000L;
                                        }
                                        currentOrderId++;
                                        currentData.setValue(currentOrderId);
                                        return Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                        if (committed) {

                                            Long incrementingOrderId = dataSnapshot.getValue(Long.class);
                                            String orderId = "Order" + incrementingOrderId;

                                            List<Map<String, Object>> itemsList = new ArrayList<>();
                                            for (CartItem item : selectedItems) {
                                                Map<String, Object> itemMap = new HashMap<>();
                                                itemMap.put("cartItemId", item.getCartItemId());
                                                itemMap.put("productImageUrl", item.getProductImageUrl());
                                                itemMap.put("productName", item.getProductName());
                                                itemMap.put("productPrice", item.getProductPrice());
                                                itemMap.put("quantity", item.getQuantity());
                                                itemMap.put("size", item.getSize());
                                                itemMap.put("sugarLevel", item.getSugarLevel());
                                                itemMap.put("orderId", orderId);
                                                itemMap.put("status", "pending");

                                                CartActivity cartActivity = new CartActivity();
                                                double itemTotal = cartActivity.calculateItemTotal(item);
                                                itemMap.put("itemTotal", itemTotal);

                                                List<Map<String, Object>> selectedAddons = new ArrayList<>();
                                                if (item.getAddons() != null) { // Ensure add-ons exist
                                                    for (AddonItem addon : item.getAddons()) {
                                                        if (addon.getSelectedQuantity() > 0) {
                                                            Map<String, Object> addonMap = new HashMap<>();
                                                            addonMap.put("name", addon.getName());
                                                            addonMap.put("imageUrl", addon.getImageUrl());
                                                            addonMap.put("quantity", addon.getSelectedQuantity());
                                                            addonMap.put("price", addon.getRegular());
                                                            selectedAddons.add(addonMap);
                                                        }
                                                    }
                                                }
                                                itemMap.put("addons", selectedAddons);
                                                itemsList.add(itemMap);
                                            }
                                            Map<String, Object> orderData = new HashMap<>();
                                            orderData.put("fullName", custname);
                                            orderData.put("contact", "(+63)" + contact);
                                            orderData.put("address", address);
                                            orderData.put("paymentMethod", paymentMethod);
                                            orderData.put("items", itemsList);
                                            orderData.put("status", "pending");
                                            orderData.put("orderId", orderId);
                                            orderData.put("total", total);

                                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("M/d/yyyy, h:mm:ss a", Locale.getDefault());
                                            String orderTime = dateTimeFormat.format(new Date());
                                            orderData.put("orderTime", orderTime);

                                            ordersRef.child(custname).child(orderId).setValue(orderData)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            for (CartItem item : selectedItems) {
                                                                String cartItemId = item.getCartItemId();
                                                                cartRef.child(cartItemId).removeValue();
                                                            }
                                                            MotionToast.Companion.createColorToast(order_summary.this,
                                                                    "Hurray success 😍",
                                                                    "Order Completed!",
                                                                    MotionToastStyle.SUCCESS,
                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                    MotionToast.LONG_DURATION,
                                                                    ResourcesCompat.getFont(order_summary.this, R.font.herobold));

                                                            Intent intents = new Intent(order_summary.this, purchase.class);
                                                            startActivity(intents);
                                                            finish();
                                                        } else {
                                                            // Show failure MotionToast
                                                            MotionToast.Companion.createColorToast(order_summary.this,
                                                                    "Failed ☹️",
                                                                    "Order Failed!",
                                                                    MotionToastStyle.ERROR,
                                                                    MotionToast.GRAVITY_BOTTOM,
                                                                    MotionToast.LONG_DURATION,
                                                                    ResourcesCompat.getFont(order_summary.this, R.font.herobold));
                                                        }
                                                    });
                                        } else {
                                            Log.e("OrderCreation", "Transaction failed: " + databaseError.getMessage());
                                        }
                                    }
                                });
                            } else {
                                // Password is incorrect
                                MotionToast.Companion.createColorToast(order_summary.this,
                                        "Failed ☹️",
                                        "Password is incorrect",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(order_summary.this, R.font.herobold));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("FirebaseError", "Error retrieving password: " + databaseError.getMessage());
                        }
                    });
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                Toast.makeText(order_summary.this, "No items to order", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Optionally, display selected image on the UI
            gcashImageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri, OnImageUploadListener listener) {
        // Assume you have a Firebase Storage reference set up
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("gcash_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            listener.onSuccess(uri.toString());
                        }))
                .addOnFailureListener(e -> {
                    listener.onFailure(e.getMessage());
                });
    }


    // Create an interface for the callback
    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }

}

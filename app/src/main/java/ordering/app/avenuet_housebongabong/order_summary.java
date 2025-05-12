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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
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

    private Handler handler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);


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

        handler = new Handler();

        TextView subTotalTextView = findViewById(R.id.subTotalTextView);
        TextView placeOrder = findViewById(R.id.placeorder);


        TextInputLayout holder2 = findViewById(R.id.holder2);


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


        ScrollView scrollView = findViewById(R.id.scroll);


        RadioButton radioCOD = findViewById(R.id.cod);
        RadioButton radioGcash = findViewById(R.id.gcash);
        RadioButton radioCash = findViewById(R.id.cash);
        RadioButton radioPick = findViewById(R.id.pickUp);
        RadioButton radioDeliv = findViewById(R.id.deliver);

        LinearLayout cash = findViewById(R.id.layoutCash);
        LinearLayout gcashInfo = findViewById(R.id.gcash_info);
        LinearLayout cod = findViewById(R.id.cashondelivery);
        LinearLayout payment = findViewById(R.id.paymentMethod);

        payment.setVisibility(View.GONE);

        TextInputEditText txtRefNumber = findViewById(R.id.txtRefNumber);

        radioDeliv.setOnClickListener(v ->{
            radioDeliv.setChecked(true);
            radioPick.setChecked(false);
            cash.setVisibility(View.GONE);
            cod.setVisibility(View.VISIBLE);
            payment.setVisibility(View.VISIBLE);
            scrollView.post(() -> scrollView.smoothScrollTo(0, payment.getTop()));

        });
        radioPick.setOnClickListener(v ->{
            radioDeliv.setChecked(false);
            radioPick.setChecked(true);
            cash.setVisibility(View.VISIBLE);
            cod.setVisibility(View.GONE);
            payment.setVisibility(View.VISIBLE);
            scrollView.post(() -> scrollView.smoothScrollTo(0, payment.getTop()));

        });

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
            scrollView.post(() -> scrollView.smoothScrollTo(0, gcashInfo.getTop()));
        });

        radioCash.setOnClickListener(v -> {
            radioCash.setChecked(true);
            radioCOD.setChecked(false);
            radioGcash.setChecked(false);
            gcashInfo.setVisibility(View.GONE);
        });

        String refNumber = txtRefNumber.getText().toString().trim();

        txtRefNumber.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            private final StringBuilder current = new StringBuilder();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                // Remove spaces and non-digits
                String cleanString = s.toString().replaceAll("\\D", "");

                int length = cleanString.length();

                if (length > 0) {
                    isUpdating = true;
                    current.setLength(0);

                    // Add formatting as per the 4-3-6 pattern
                    for (int i = 0; i < length && i < 15; i++) {
                        if (i == 4 || i == 7) {  // Positions where we insert a space
                            current.append(" ");
                        }
                        current.append(cleanString.charAt(i));
                    }

                    // Set the formatted text
                    txtRefNumber.setText(current.toString());
                    txtRefNumber.setSelection(current.length());  // Move cursor to end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

                                String deliveryMethod;
                                if (radioPick.isChecked()){
                                    deliveryMethod = "Pick Up";
                                }else {
                                    deliveryMethod = "Delivery";
                                }

                                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
                                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("UserCart").child(custname);
                                DatabaseReference counterRef = FirebaseDatabase.getInstance().getReference("orderCounter/currentOrderId");
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users").child(fullName);

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

                                            String refNumber = txtRefNumber.getText().toString().replaceAll("\\s+", "");

                                            if (radioGcash.isChecked()) {
                                                if (refNumber.isEmpty()) {
                                                    txtRefNumber.setError("Reference number is required");
                                                    txtRefNumber.requestFocus();
                                                    return;
                                                }
                                            }

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

                                            usersRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    String fcmToken = snapshot.getValue(String.class);

                                                    Map<String, Object> orderData = new HashMap<>();
                                                    orderData.put("fullName", custname);
                                                    orderData.put("contact", "(+63)" + contact);
                                                    orderData.put("address", address);
                                                    orderData.put("paymentMethod", paymentMethod);
                                                    orderData.put("items", itemsList);
                                                    orderData.put("status", "pending");
                                                    orderData.put("fcmToken", fcmToken);

                                                    if (!refNumber.isEmpty()) {
                                                        orderData.put("refNumber", refNumber);
                                                    } else {
                                                        orderData.put("refNumber", "null");
                                                    }
                                                    orderData.put("orderId", orderId);
                                                    orderData.put("deliveryMethod", deliveryMethod);
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

                                                                    Intent intents = new Intent(order_summary.this, receipt.class);
                                                                    intents.putExtra("orderId", orderId);
                                                                    intents.putExtra("custName", custname);
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
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                    Log.e("FirebaseError", "Error retrieving FCM token: " + error.getMessage());
                                                }
                                            });
                                        }
                                            else {
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


    // Create an interface for the callback
    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(String errorMessage);
    }

}

package ordering.app.avenuet_housebongabong;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import android.widget.SeekBar;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class DetailProductActivity extends AppCompatActivity {

    private int sugarLevel = 50;

    private ImageView detailProductImage, cupLarge, cupSmall;
    private TextView detailProductName, txtDescription, showPrice, cartBtn;

    private int number = 1;
    private TextView numberTextView, decrementButton, incrementButton;

    private boolean isCupLargeFilled = false;
    private boolean isCupSmallFilled = false;

    private String preloadedSize; // Add this at the top of your activity class

    private String largePrice;
    private String smallPrice;
    private DatabaseReference mDatabase;
    private String cartItemId;
    private SeekBar seekBar;
    private TextView marker0, marker25, marker50, marker75, marker100;
    private int defaultColor, selectedColor;
    private Product product;
    private ImageView heartImageView;
    private boolean isFavorite = false;
    private DatabaseReference databaseReference;



    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            // Check the background color of your layout to decide text color
            boolean isLightBackground = true; // Determine based on your layout

            // Set the status bar color
            window.setStatusBarColor(Color.TRANSPARENT);

            // Set light or dark status bar icons based on background color
            if (isLightBackground) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        String productName = getIntent().getStringExtra("productName");


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "No name found");


        RecyclerView addonsRecyclerView = findViewById(R.id.addonsRecyclerView);

        // Set up the LayoutManager for horizontal scrolling
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        addonsRecyclerView.setLayoutManager(layoutManager);

        // Set item view cache size
        addonsRecyclerView.setItemViewCacheSize(20); // Cache 20 items for smoother scrolling

        // Set up the SnapHelper
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(addonsRecyclerView);

        List<AddonItem> addonsList = new ArrayList<>(); // Your add-ons list
        AddonsAdapter adapter = new AddonsAdapter(addonsList); // Initialize the adapter with the list

        addonsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int centerX = recyclerView.getWidth() / 2;
                int childCount = recyclerView.getChildCount();
                int recyclerViewCenter = recyclerView.getWidth() / 2;

                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    int childCenterX = (int) (child.getX() + child.getWidth() / 2);
                    float distanceFromCenter = Math.abs(recyclerViewCenter - childCenterX);
                    float scale = 1 - (distanceFromCenter / recyclerViewCenter); // Scale factor

                    // Clamp the scale between 0.5 and 1.0
                    scale = Math.max(0.9f, scale);
                    scale = Math.min(1.0f, scale);

                    child.setScaleX(scale);
                    child.setScaleY(scale);
                }
            }
        });


        addonsRecyclerView.setAdapter(adapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");

// Fetch the data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the list before adding new data
                addonsList.clear();

                // Loop through all products in the Products node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.child("Category").getValue(String.class);

                    // Check if the product belongs to the "Add-Ons" category
                    if ("Add-Ons".equals(category)) {
                        // Extract the required fields
                        String productName = snapshot.child("ProductName").getValue(String.class);
                        String imageUrl = snapshot.child("ImageURL").getValue(String.class);
                        double price = Double.parseDouble(snapshot.child("Regular").getValue(String.class));

                        // Create an AddonItem object and add it to the list
                        AddonItem addonItem = new AddonItem(productName, imageUrl, price);
                        addonsList.add(addonItem);
                    }
                }

                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors

                Toast.makeText(DetailProductActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });



        TextView addToCartTextView = findViewById(R.id.addToCart);


        String productImageURL = getIntent().getStringExtra("productImageURL");
//        String productName = getIntent().getStringExtra("productName");
        Intent intent = getIntent();
        boolean isEditing = intent.getBooleanExtra("isEditing", false);
        if (isEditing) {
            addToCartTextView.setText("EDIT PRODUCT");  // Change to Edit Product
        } else {
            addToCartTextView.setText("ADD TO CART");  // Default case
        }

        ImageView showQR = findViewById(R.id.showQR);
        FrameLayout qrFrameLayout = findViewById(R.id.qrFrameLayout);
        ImageView qrImage = findViewById(R.id.qrImage);

        showQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(DetailProductActivity.this, "Loading please wait...", Toast.LENGTH_SHORT).show();

                Log.d("ShowQR", "Product Name from Intent: " + productName);

                if (productName != null && !productName.isEmpty()) {
                    // Retrieve QRImageURL using the product name
                    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

                    productRef.orderByChild("ProductName").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Loop through results, though there should be only one if names are unique
                                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                    String qrImageUrl = productSnapshot.child("QRImageURL").getValue(String.class);

                                    Log.d("ShowQR", "QRImageURL of this product: " + qrImageUrl);

                                    // Load the QR image into the ImageView using Glide
                                    if (qrImageUrl != null) {
                                        qrFrameLayout.setVisibility(View.VISIBLE);  // Show the FrameLayout
                                        Glide.with(getApplicationContext())
                                                .load(qrImageUrl)
                                                .into(qrImage);  // Load the QR image into the ImageView
                                    } else {
                                        Log.d("ShowQR", "QRImageURL is null for product: " + productName);
                                    }
                                }
                            } else {
                                Log.d("ShowQR", "No product found with name: " + productName);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("Firebase", "Error fetching product by name", databaseError.toException());
                        }
                    });
                } else {
                    Log.e("ShowQR", "Product name is null or empty");
                }
            }
        });


// Optionally, handle closing the FrameLayout on click or other events
        qrFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrFrameLayout.setVisibility(View.GONE);  // Hide the FrameLayout when clicked
            }
        });


        SeekBar seekBar = findViewById(R.id.sugarSeekBar);
        seekBar.setProgress(50);

        TextView marker25 = findViewById(R.id.marker_25);
        TextView marker50 = findViewById(R.id.marker_50);
        TextView marker75 = findViewById(R.id.marker_75);
        TextView marker100 = findViewById(R.id.marker_100);

        final int[] snapPoints = {25, 50, 75, 100};

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // No need to handle this for snapping
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Snap to nearest step
                int currentProgress = seekBar.getProgress();
                int closestSnapPoint = snapPoints[0];

                for (int snapPoint : snapPoints) {
                    if (Math.abs(currentProgress - snapPoint) < Math.abs(currentProgress - closestSnapPoint)) {
                        closestSnapPoint = snapPoint;
                    }
                }

                // Set the progress to the nearest snap point
                seekBar.setProgress(closestSnapPoint);

                sugarLevel = closestSnapPoint;
            }
        });





        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()){
                            System.out.println("Error");
                            return;
                        }
                        String userFCMToken = task.getResult();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userFCMToken", userFCMToken);
                        editor.apply();
                    }
                });
        mDatabase = FirebaseDatabase.getInstance().getReference();

        cupLarge = findViewById(R.id.cupLarge);
        cupSmall = findViewById(R.id.cupSmall);
        showPrice = findViewById(R.id.showPrice);

        cartBtn = findViewById(R.id.addToCart);

        largePrice = getIntent().getStringExtra("productLargePrice");
        smallPrice = getIntent().getStringExtra("productSmallPrice");

        String itemSize = getIntent().getStringExtra("selectedSize");

        cartItemId = intent.getStringExtra("productId");

        if (productName != null) {
            databaseReference.orderByChild("ProductName").equalTo(productName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            largePrice = productSnapshot.child("Large").getValue(String.class);
                            smallPrice = productSnapshot.child("Small").getValue(String.class);

                            if ("large".equalsIgnoreCase(itemSize)) {
                                cupLarge.setBackgroundResource(R.drawable.cuplfill);
                                cupSmall.setBackgroundResource(R.drawable.cups_gray);
                                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                String formattedLargePrice = formatter.format(Double.parseDouble(largePrice));
                                isCupLargeFilled = true;
                                isCupSmallFilled = false;
                                showPrice.setText(formattedLargePrice);
                            } else if ("small".equalsIgnoreCase(itemSize)) {
                                cupSmall.setBackgroundResource(R.drawable.cupsfill);
                                cupLarge.setBackgroundResource(R.drawable.cupl_gray);
                                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                String formattedSmallPrice = formatter.format(Double.parseDouble(smallPrice));
                                isCupSmallFilled = true;
                                isCupLargeFilled = false;
                                showPrice.setText(formattedSmallPrice);
                            } else {
                                // If product name does not specify size, clear the price
                                cupLarge.setBackgroundResource(R.drawable.cupl_gray);  // Unfill large cup
                                cupSmall.setBackgroundResource(R.drawable.cups_gray);  // Unfill small cup
                                showPrice.setText("");  // Clear price if no size is found
                            }
                        }

                        // Set OnClickListener for cup sizes after retrieving prices
                        cupLarge.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cupLarge.setBackgroundResource(R.drawable.cuplfill);  // Highlight large cup
                                cupSmall.setBackgroundResource(R.drawable.cups_gray);  // Unfill small cup

                                // Show the large cup price
                                if (largePrice != null) {
                                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                    String formattedLargePrice = formatter.format(Double.parseDouble(largePrice));
                                    showPrice.setText(formattedLargePrice);
                                    isCupLargeFilled = true;
                                    isCupSmallFilled = false;
                                }
                            }
                        });

                        cupSmall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cupSmall.setBackgroundResource(R.drawable.cupsfill);  // Highlight small cup
                                cupLarge.setBackgroundResource(R.drawable.cupl_gray);  // Unfill large cup

                                // Show the small cup price
                                if (smallPrice != null) {
                                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
                                    String formattedSmallPrice = formatter.format(Double.parseDouble(smallPrice));
                                    showPrice.setText(formattedSmallPrice);

                                    isCupSmallFilled = true;
                                    isCupLargeFilled = false;
                                }
                            }
                        });

                    } else {
                        // Handle case where product is not found
                        showPrice.setText("Product not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    showPrice.setText("Error retrieving product data.");
                }
            });
        }


        txtDescription = findViewById(R.id.txtDescription);
        detailProductImage = findViewById(R.id.detailProductImage);
        detailProductName = findViewById(R.id.detailProductName);

        numberTextView = findViewById(R.id.numberTextView);
        incrementButton = findViewById(R.id.incrementButton);
        decrementButton = findViewById(R.id.decrementButton);

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number < 9) {
                    number++;
                    numberTextView.setText(String.valueOf(number));
                }
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number > 1) {
                    number--;
                    numberTextView.setText(String.valueOf(number));
                }
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String productName = detailProductName.getText().toString();
                int quantity = Integer.parseInt(numberTextView.getText().toString());
                String price = showPrice.getText().toString();
//                String size = isCupLargeFilled ? "Large" : isCupSmallFilled ? "Small" : null;
                String size = (isCupLargeFilled || (cartItemId != null && "Large".equals(preloadedSize))) ? "Large"
                        : (isCupSmallFilled || (cartItemId != null && "Small".equals(preloadedSize))) ? "Small"
                        : null;

                if (quantity == 0) {
                    MotionToast.Companion.createColorToast(DetailProductActivity.this, // Change here
                            "Input Size",
                            "Please input quantity",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));

                } else if (size == null) {
                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                            "Select size",
                            "Please select size!",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
                } else {
                    if (cartItemId != null) {
                        // If in edit mode, update the existing item
                        updateProductInFirebase(productName, quantity, price, size, cartItemId);
                    } else {
                        // Otherwise, save as a new product
                        saveToFirebase( productName, quantity, price, size, addonsList);
                    }
                }
            }
        });




        // Retrieve product details from intent extras


        String descrip = getIntent().getStringExtra("productDescription");

        // Load product image and name into views
        Glide.with(this)
                .load(productImageURL)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(detailProductImage);

        detailProductName.setText(productName);
        txtDescription.setText(descrip);
    }

    private void updateProductInFirebase(String productName, int quantity, String productPrice, String size, String cartItemId) {
        // Get shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "No name found");

        DatabaseReference userCartRef = mDatabase.child("UserCart").child(fullName);
        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String matchingItemId = null;
                int existingQuantity = 0;


                // Iterate through the cart items
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String existingProductName = snapshot.child("productName").getValue(String.class);
                    String existingSize = snapshot.child("size").getValue(String.class);
                    String existingCartItemId = snapshot.getKey(); // Get the cart item ID

                    // Check if there is an item with the same product name and size as the updated item (except for the current item)
                    if (existingProductName.equals(productName) && existingSize.equals(size) && !existingCartItemId.equals(cartItemId)) {
                        matchingItemId = existingCartItemId;
                        existingQuantity = snapshot.child("quantity").getValue(Integer.class);

                        break; // No need to continue searching once a match is found
                    }
                }

                if (matchingItemId != null) {
                    // Update the matching item with the new quantity and sugar level
                    int newQuantity = existingQuantity + quantity;
                    Map<String, Object> updatedCartItem = new HashMap<>();
                    updatedCartItem.put("quantity", newQuantity);
                    updatedCartItem.put("productPrice", productPrice); // Update price if necessary
                    updatedCartItem.put("sugarLevel", sugarLevel); // Update sugar level if necessary

                    userCartRef.child(matchingItemId).updateChildren(updatedCartItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Remove the original item (item 2) after updating the matching item
                                    userCartRef.child(cartItemId).removeValue()
                                            .addOnCompleteListener(removeTask -> {
                                                if (removeTask.isSuccessful()) {
                                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                                            "Hurray success 😍",
                                                            "Cart updated and duplicate item removed!",
                                                            MotionToastStyle.SUCCESS,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
                                                } else {
                                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                                            "Failed ☹️",
                                                            "Removing duplicate item failed!",
                                                            MotionToastStyle.ERROR,
                                                            MotionToast.GRAVITY_BOTTOM,
                                                            MotionToast.LONG_DURATION,
                                                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
                                                }
                                            });
                                } else {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Failed ☹️",
                                            "Updating product failed!",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
                                }
                            });
                } else {
                    // No matching item found, just update the current item
                    Map<String, Object> updatedCartItem = new HashMap<>();
                    updatedCartItem.put("quantity", quantity);
                    updatedCartItem.put("productPrice", productPrice);
                    updatedCartItem.put("size", size);
                    updatedCartItem.put("sugarLevel", sugarLevel);

                    userCartRef.child(cartItemId).updateChildren(updatedCartItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Hurray success 😍",
                                            "Cart updated!",
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
                                } else {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Failed ☹️",
                                            "Updating product failed!",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                MotionToast.Companion.createColorToast(DetailProductActivity.this,
                        "Error ☹️",
                        "Failed to retrieve cart items!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(DetailProductActivity.this, R.font.herobold));
            }
        });
    }


//    private void saveToFirebase(String productName, int quantity, String productPrice, String size) {
//        // Get shared preferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        String fullName = sharedPreferences.getString("fullName", "No name found");
//        String imageUrl = getIntent().getStringExtra("productImageURL");
//
//
//        // Reference to the user's cart
//        DatabaseReference userCartRef = mDatabase.child("UserCart").child(fullName);
//
//        // Check if the item already exists in the cart
//        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                boolean itemExists = false;
//                String existingCartItemId = null;
//                int existingQuantity = 0;
//
//                // Iterate through cart items to find matching product and size
//                for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
//                    String existingProductName = cartItemSnapshot.child("productName").getValue(String.class);
//                    String existingSize = cartItemSnapshot.child("size").getValue(String.class);
//                    if (productName.equals(existingProductName) && size.equals(existingSize)) {
//                        itemExists = true;
//                        existingCartItemId = cartItemSnapshot.getKey();
//                        existingQuantity = cartItemSnapshot.child("quantity").getValue(Integer.class);
//                        break;
//                    }
//                }
//
//                if (itemExists) {
//                    // Update the quantity of the existing cart item
//                    int newQuantity = existingQuantity + quantity;
//                    userCartRef.child(existingCartItemId).child("quantity").setValue(newQuantity)
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
//                                            "Hurray success 😍",
//                                            "Cart updated!",
//                                            MotionToastStyle.SUCCESS,
//                                            MotionToast.GRAVITY_BOTTOM,
//                                            MotionToast.LONG_DURATION,
//                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
//                                } else {
//                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
//                                            "Failed ☹️",
//                                            "Updating product failed!",
//                                            MotionToastStyle.ERROR,
//                                            MotionToast.GRAVITY_BOTTOM,
//                                            MotionToast.LONG_DURATION,
//                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
//                                }
//                            });
//                } else {
//                    // Add a new cart item
//                    String cartItemId = userCartRef.push().getKey();
//
//
//                    // Create a map to hold the product details
//                    Map<String, Object> cartItem = new HashMap<>();
//                    cartItem.put("productImageUrl", imageUrl);
//                    cartItem.put("productName", productName);
//                    cartItem.put("quantity", quantity);
//                    cartItem.put("productPrice", productPrice);
//                    cartItem.put("size", size);
//                    cartItem.put("sugarLevel", sugarLevel);
//
//                    // Save the cart item to Firebase
//                    userCartRef.child(cartItemId).setValue(cartItem)
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
//                                            "Hurray success 😍",
//                                            "Added to Cart!",
//                                            MotionToastStyle.SUCCESS,
//                                            MotionToast.GRAVITY_BOTTOM,
//                                            MotionToast.LONG_DURATION,
//                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
//                                } else {
//                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
//                                            "Failed ☹️",
//                                            "Adding product failed!",
//                                            MotionToastStyle.ERROR,
//                                            MotionToast.GRAVITY_BOTTOM,
//                                            MotionToast.LONG_DURATION,
//                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle database error
//                Toast.makeText(DetailProductActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void saveToFirebase(String productName, int quantity, String productPrice, String size, List<AddonItem> addonsList) {
        // Get shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "No name found");
        String imageUrl = getIntent().getStringExtra("productImageURL");

        // Reference to the user's cart
        DatabaseReference userCartRef = mDatabase.child("UserCart").child(fullName);

        // Check if the item already exists in the cart
        userCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean itemExists = false;
                String existingCartItemId = null;
                int existingQuantity = 0;

                // Iterate through cart items to find matching product and size
                for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                    String existingProductName = cartItemSnapshot.child("productName").getValue(String.class);
                    String existingSize = cartItemSnapshot.child("size").getValue(String.class);
                    if (productName.equals(existingProductName) && size.equals(existingSize)) {
                        itemExists = true;
                        existingCartItemId = cartItemSnapshot.getKey();
                        existingQuantity = cartItemSnapshot.child("quantity").getValue(Integer.class);
                        break;
                    }
                }

                if (itemExists) {
                    // Update the quantity of the existing cart item
                    int newQuantity = existingQuantity + quantity;
                    userCartRef.child(existingCartItemId).child("quantity").setValue(newQuantity)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Hurray success 😍",
                                            "Cart updated!",
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
                                } else {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Failed ☹️",
                                            "Updating product failed!",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
                                }
                            });
                } else {
                    // Add a new cart item
                    String cartItemId = userCartRef.push().getKey();

                    // Create a map to hold the product details
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("productImageUrl", imageUrl);
                    cartItem.put("productName", productName);
                    cartItem.put("quantity", quantity);
                    cartItem.put("productPrice", productPrice);
                    cartItem.put("size", size);
                    cartItem.put("sugarLevel", sugarLevel);

                    // Prepare to save selected addons
                    List<Map<String, Object>> selectedAddons = new ArrayList<>();
                    for (AddonItem addon : addonsList) {
                        if (addon.getSelectedQuantity() > 0) {
                            Map<String, Object> addonMap = new HashMap<>();
                            addonMap.put("name", addon.getName());
                            addonMap.put("imageUrl", addon.getImageUrl());
                            addonMap.put("quantity", addon.getSelectedQuantity());
                            addonMap.put("price", addon.getRegular()); // Optional: add price if needed
                            selectedAddons.add(addonMap);
                        }
                    }

                    // Save selected addons to the cart item
                    cartItem.put("addons", selectedAddons);

                    // Save the cart item to Firebase
                    userCartRef.child(cartItemId).setValue(cartItem)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Hurray success 😍",
                                            "Added to Cart!",
                                            MotionToastStyle.SUCCESS,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
                                } else {
                                    MotionToast.Companion.createColorToast(DetailProductActivity.this,
                                            "Failed ☹️",
                                            "Adding product failed!",
                                            MotionToastStyle.ERROR,
                                            MotionToast.GRAVITY_BOTTOM,
                                            MotionToast.LONG_DURATION,
                                            ResourcesCompat.getFont(DetailProductActivity.this,R.font.herobold));
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(DetailProductActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateCartWithAddons(String cartItemId, List<AddonItem> addonsList) {
        for (AddonItem addon : addonsList) {
            if (addon.getSelectedQuantity() > 0) { // Ensure we have selected quantity
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String fullName = sharedPreferences.getString("fullName", "No name found");
                DatabaseReference addonRef = mDatabase.child("UserCart").child(fullName).child(cartItemId).child("addons").push();

                Map<String, Object> addonMap = new HashMap<>();
                addonMap.put("addonName", addon.getName());
                addonMap.put("addonQuantity", addon.getSelectedQuantity());
                addonMap.put("addonPrice", addon.getRegular());

                // Save each add-on to Firebase with error checking
                addonRef.setValue(addonMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AddonSuccess", "Successfully added addon: " + addon.getName());
                    } else {
                        Log.e("AddonError", "Failed to add addon: " + addon.getName());
                    }
                });
            } else {
                Log.d("AddonQuantity", "Addon: " + addon.getName() + " not added due to zero quantity.");
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String source = getIntent().getStringExtra("source");

        if ("search".equals(source)) {
            Intent intent = new Intent(DetailProductActivity.this, scanner.class);
            startActivity(intent);
        }else if("scanner".equals(source)) {
            Intent intent = new Intent(DetailProductActivity.this, homeActivity.class);
            startActivity(intent);
        }
        finish();
    }


    private int getLarge() {
        // Replace with actual database call to get large cup price
        return getLarge(); // Placeholder value
    }

    private int getSmall() {
        // Replace with actual database call to get small cup price
        return getSmall(); // Placeholder value
    }
}
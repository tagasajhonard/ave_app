package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private DatabaseReference mDatabase;
    private String fullName;
    private LinearLayout emptyCartTextView;
    private TextView removeButton;
    private TextView subTotalTextView;
    private double subTotal = 0.0;
    private ImageView tutorial;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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



        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        emptyCartTextView = findViewById(R.id.emptyCartTextView);
        tutorial = findViewById(R.id.tutorial);

        CheckBox selectAllCheckBox = findViewById(R.id.select_all_checkbox);

// Handle "Select All" functionality
        selectAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem cartItem : cartItemList) {
                cartItem.setSelected(isChecked); // Update all items' selection state
            }
            cartAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
            updateSubTotal(); // Update subtotal based on selection
        });



        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("How to Place an Order")
                            .setMessage("1. Select your favorite product.\n2. Choose the size and add-ons.\n3. Add to cart.\n4. Proceed to checkout to place your order.")
                            .setPositiveButton("Got it", null)
                            .show();
            }
        });


        cartItemList = new ArrayList<>();

        cartAdapter = new CartAdapter(this, cartItemList, (cartItemId, position) -> {
            removeItemFromFirebase(cartItemId, cartItemList.get(position), position);
            updateSubTotal();
        }, (cartItem, position) -> {

            cartItem.setSelected(!cartItem.isSelected());
            cartAdapter.notifyItemChanged(position);
            updateSubTotal();
        });

        recyclerViewCart.setAdapter(cartAdapter);
        removeButton = findViewById(R.id.removeButton);
        subTotalTextView = findViewById(R.id.subTotal);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        fullName = sharedPreferences.getString("FullName", "No name found");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadCartItems();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final float SWIPE_THRESHOLD_RATIO = 0.5f;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                CartItem cartItem = cartItemList.get(position);
                String cartItemId = cartItem.getCartItemId();
                removeItemFromFirebase(cartItemId, cartItem, position);
                cartItemList.remove(position);
                recyclerViewCart.getAdapter().notifyItemRemoved(position);
            }

            
@Override
public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    View itemView = viewHolder.itemView;

    // Define paint attributes
    Paint paint = new Paint();
    paint.setColor(Color.TRANSPARENT); // Background color for swipe area

    // Threshold for limiting swipe distance
    float itemWidth = itemView.getWidth();
    float threshold = itemWidth * SWIPE_THRESHOLD_RATIO;

    if (dX < -threshold) {
        dX = -threshold;
    }

    // Draw background for swipe
    if (dX < 0) {
        c.drawRect(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom(), paint);

        // Load and position the drawable
        Drawable binIcon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.bin);
        if (binIcon != null) {
            // Convert 40dp to pixels
            int iconSize = (int) (40 * recyclerView.getContext().getResources().getDisplayMetrics().density);

            // Calculate the swipe area width
            float swipeWidth = -dX; // Convert dX to positive for width calculation

            // Center the icon within the swipe area
            int iconLeft = itemView.getRight() - (int) ((swipeWidth - iconSize) / 2) - iconSize;
            int iconRight = iconLeft + iconSize;
            int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
            int iconBottom = iconTop + iconSize;

            // Set bounds and draw the icon
            binIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            binIcon.draw(c);
        }
    }

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
}


        });

        itemTouchHelper.attachToRecyclerView(recyclerViewCart);

        TextView buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(v -> {
            List<CartItem> selectedItems = new ArrayList<>();

            for (CartItem item : cartItemList) {
                if (item.isSelected()) {
                    Log.d("CartItem", "Selected Item: " + item.getProductName() + ", Price: " + item.getProductPrice() + ", Quantity: " + item.getQuantity());
                    selectedItems.add(item);
                }
            }

            if (!selectedItems.isEmpty()) {

                double subTotal = updateSubTotal();

                Intent intent = new Intent(this, order_summary.class);
                intent.putParcelableArrayListExtra("selectedItems", (ArrayList<? extends Parcelable>) selectedItems);

                intent.putExtra("subTotal", subTotal);
                startActivity(intent);

            } else {
                MotionToast.Companion.createColorToast(this,
                        "Failed ☹️",
                        "No selected item!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.herobold));
            }
        });

        loadCartItems();
    }

    private double updateSubTotal() {
        double subTotal = 0.0; // Reset subtotal

        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                String priceString = item.getProductPrice().replaceAll("[^\\d.]", "");
                double price = Double.parseDouble(priceString);
                int quantity = item.getQuantity();

                // Calculate subtotal for the product including the quantity
                subTotal += price * quantity;

                // Check if the item has add-ons
                if (item.getAddons() != null && !item.getAddons().isEmpty()) {
                    // Calculate the total for add-ons, considering the quantity of the product
                    for (AddonItem addon : item.getAddons()) {
                        subTotal += addon.getTotalPrice() * quantity; // Add add-ons total multiplied by quantity
                    }
                }
            }
        }

        // Update the subtotal text view
        subTotalTextView.setText(String.format(Locale.getDefault(), " %.2f", subTotal));
        return subTotal;
    }

        double calculateItemTotal(CartItem item) {
        double itemTotal = 0.0;

        // Get the product price
        String priceString = item.getProductPrice().replaceAll("[^\\d.]", ""); // Remove non-numeric characters
        double price = Double.parseDouble(priceString);
        int quantity = item.getQuantity();

        // Calculate the item total considering the product price and quantity
        itemTotal = price * quantity;

        // Check if the item has add-ons
        if (item.getAddons() != null && !item.getAddons().isEmpty()) {
            // Calculate the total for add-ons, considering the quantity of the product
            for (AddonItem addon : item.getAddons()) {
                itemTotal += addon.getTotalPrice() * quantity; // Add add-ons total multiplied by quantity
            }
        }

        return itemTotal;
    }



    private void removeItemFromFirebase(String cartItemId, CartItem cartItem, int position) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("FullName", "No name found");

        mDatabase.child("UserCart").child(fullName).child(cartItemId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
//                Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                // If failed, add the item back to the list and notify adapter
                cartItemList.add(position, cartItem);
                recyclerViewCart.getAdapter().notifyItemInserted(position);
            }
        });
    }

    private void loadCartItems() {
        DatabaseReference userCartRef = mDatabase.child("UserCart").child(fullName);

        userCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, CartItem> cartItemMap = new HashMap<>();
                cartItemList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem newCartItem = snapshot.getValue(CartItem.class);
                    if (newCartItem != null) {
                        newCartItem.setCartItemId(snapshot.getKey());

                        String key = newCartItem.getProductName() + newCartItem.getSize();
                        if (cartItemMap.containsKey(key)) {
                            CartItem existingItem = cartItemMap.get(key);
                            if (existingItem != null) {
                                existingItem.setQuantity(Integer.valueOf(existingItem.getQuantity() + newCartItem.getQuantity()));
                            }
                        } else {
                            cartItemMap.put(key, newCartItem);
                        }
                    } else {
                        Log.e("CartActivity", "CartItem is null for snapshot: " + snapshot.getKey());
                    }
                }

                cartItemList.addAll(cartItemMap.values());

                cartAdapter.notifyDataSetChanged();
                toggleEmptyCartMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void toggleEmptyCartMessage() {
        if (cartItemList.isEmpty()) {
            emptyCartTextView.setVisibility(View.VISIBLE);
            recyclerViewCart.setVisibility(View.GONE);
        } else {
            emptyCartTextView.setVisibility(View.GONE);
            recyclerViewCart.setVisibility(View.VISIBLE);
        }
    }
}

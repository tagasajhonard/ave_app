package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private OnItemRemovedListener onItemRemovedListener;
    private OnItemClickListener onItemClickListener;
    private String fullName;
    private Context context;

    public CartAdapter(Context context, List<CartItem> cartItemList, OnItemRemovedListener onItemRemovedListener, OnItemClickListener onItemClickListener) {
        this.cartItemList = cartItemList;
        this.onItemRemovedListener = onItemRemovedListener;
        this.onItemClickListener = onItemClickListener;
        this.context = context;


        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.fullName = sharedPreferences.getString("fullName", "No name found");
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(String.valueOf(cartItem.getProductPrice()));
        holder.productQuantity.setText("x" + cartItem.getQuantity());
        holder.productSugar.setText("Sugar Level : " + cartItem.getSugarLevel() + "%");
        holder.productSize.setText("Size : " + cartItem.getSize());

        holder.number = cartItem.getQuantity();

        Glide.with(holder.itemView.getContext())
                .load(cartItem.getProductImageUrl())
                .into(holder.productImage);

        if (cartItem.isSelected()) {
            holder.cardView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cart_item_border));

        } else {
            holder.cardView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.cart_border));
        }

        holder.productQuantity.setText(String.valueOf(holder.number));

        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("UserCart")
                .child(fullName)
                .child(cartItem.getCartItemId());

        // Increment button listener
        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.number < 9) {
                    holder.number++;
                    holder.productQuantity.setText(String.valueOf(holder.number));

                    userCartRef.child("quantity").setValue(holder.number);
                }
            }
        });

        // Decrement button listener
        holder.decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.number > 1) {
                    holder.number--;
                    holder.productQuantity.setText(String.valueOf(holder.number));

                    userCartRef.child("quantity").setValue(holder.number);
                }
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(cartItem, position);
            }
        });

        populateAddons(holder.addonsContainer, cartItem.getAddons());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserCart").child(fullName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                holder.addonsContainer.removeAllViews();

                String itemId = cartItem.getCartItemId();
                if (dataSnapshot.hasChild(itemId)) {
                    DataSnapshot cartItemSnapshot = dataSnapshot.child(itemId);

                    if (cartItemSnapshot.hasChild("addons")) {
                        if (cartItem.getAddons() == null) {
                            cartItem.setAddons(new ArrayList<>());
                        }

                        cartItem.getAddons().clear();

                        for (DataSnapshot addonSnapshot : cartItemSnapshot.child("addons").getChildren()) {
                            String addonName = addonSnapshot.child("name").getValue(String.class);
                            String addonImageUrl = addonSnapshot.child("imageUrl").getValue(String.class);
                            double addonPrice = addonSnapshot.child("price").getValue(Double.class);
                            int addonQuantity = addonSnapshot.child("quantity").getValue(Integer.class);

                            AddonItem addonItem = new AddonItem(addonName, addonImageUrl, addonPrice);
                            addonItem.setSelectedQuantity(addonQuantity);
                            cartItem.addAddon(addonItem);
                        }
                    }else {
                        TextView noAddonsText = new TextView(context);
                        noAddonsText.setText("No add-ons selected");
                        holder.addonsContainer.addView(noAddonsText);
                    }

                    populateAddons(holder.addonsContainer, cartItem.getAddons());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });


        holder.editButton.setOnClickListener(v -> {

            Intent intent = new Intent(context, DetailProductActivity.class);
            intent.putExtra("isEditing", true);
            intent.putExtra("productId", cartItem.getCartItemId());
            intent.putExtra("productName", cartItem.getProductName());
            intent.putExtra("productImageURL", cartItem.getProductImageUrl());
            intent.putExtra("productDescription", cartItem.getDescription());
            intent.putExtra("productSmallPrice", cartItem.getSmallPrice());
            intent.putExtra("regularPrice", cartItem.getRegularPrice());
            intent.putExtra("productLargePrice", cartItem.getLargePrice());
            intent.putExtra("selectedSize", cartItem.getSize());

            // Start the DetailedProductActivity
            context.startActivity(intent);
        });
    }

    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }


    public void removeItem(int position) {
        cartItemList.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemRemovedListener {
        void onItemRemoved(String cartItemId, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(CartItem cartItem, int position);
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, productPrice, productQuantity, productSugar, productSize, editButton;
        public ImageView productImage;
        public CardView cardView;
        public int number = 1;
        public LinearLayout addonsContainer;
        public TextView decrementButton, incrementButton;

        public CartViewHolder(View view) {
            super(view);

            incrementButton = view.findViewById(R.id.incrementButton);
            decrementButton = view.findViewById(R.id.decrementButton);
            productSugar = view.findViewById(R.id.productSugar);
            productSize = view.findViewById(R.id.productSize);
            productName = view.findViewById(R.id.productName);
            productPrice = view.findViewById(R.id.productPrice);
            productImage = view.findViewById(R.id.productImage);
            productQuantity = view.findViewById(R.id.quantity);
            cardView = view.findViewById(R.id.cardView);

            editButton = itemView.findViewById(R.id.editButton);

            addonsContainer = view.findViewById(R.id.addonsContainer);
        }
    }

        @SuppressLint("SetTextI18n")
    private void populateAddons(LinearLayout addonsContainer, List<AddonItem> addons) {
        // Clear any previous add-on views
        addonsContainer.removeAllViews();

        if (addons == null || addons.isEmpty()) {
            return;
        }

        for (AddonItem addon : addons) {
            Log.d("AddonItem", "Addon: " + addon.getName() + " Quantity: " + addon.getSelectedQuantity());

            TextView addonTextView = new TextView(context);
            addonTextView.setText(addon.getName() + " x " + addon.getSelectedQuantity());
            addonTextView.setTextSize(16);
            addonTextView.setTypeface(ResourcesCompat.getFont(context, R.font.herobold));

            // Optionally, set layout parameters
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            addonsContainer.addView(addonTextView); // Add the TextView to the container
        }
    }
}

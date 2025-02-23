package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder> {
    private List<CartItem> cartItemList;
    private String fullName;
    private Context context;

    public OrderSummaryAdapter(Context context, List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        this.context = context;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.fullName = sharedPreferences.getString("fullName", "No name found");

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        if (cartItem != null) {
            String x = "x";
            holder.productName.setText(cartItem.getProductName());
            holder.productPrice.setText("Price : " + cartItem.getProductPrice());
            holder.quantity.setText(x + String.valueOf(cartItem.getQuantity()));

            holder.productSugar.setText("Sugar Level : " + String.valueOf(cartItem.getSugarLevel())+ "%");

            Glide.with(context)
                    .load(cartItem.getProductImageUrl())
                    .into(holder.productImage);
        } else {
            Log.e("OrderSummaryAdapter", "CartItem at position " + position + " is null.");
        }

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

    }

    @SuppressLint("SetTextI18n")
    private void populateAddons(LinearLayout addonsContainer, List<AddonItem> addons) {
        // Clear any previous add-on views
        addonsContainer.removeAllViews();

        if (addons == null || addons.isEmpty()) {
            TextView noAddonsText = new TextView(context);
            noAddonsText.setText("No add-ons selected");
            addonsContainer.addView(noAddonsText);
            return;
        }

        for (AddonItem addon : addons) {
            Log.d("AddonItem", "Addon: " + addon.getName() + " Quantity: " + addon.getSelectedQuantity());

            TextView addonTextView = new TextView(context);
            addonTextView.setText(addon.getName() + " x " + addon.getSelectedQuantity());
            addonTextView.setTextSize(16);
            addonTextView.setTypeface(ResourcesCompat.getFont(context, R.font.herobold));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            addonsContainer.addView(addonTextView);
        }
    }

//    @Override
//    public int getItemCount() {
//        return cartItemList.size();
//    }
    @Override
    public int getItemCount() {
        return cartItemList != null ? cartItemList.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productPrice;
        TextView quantity;
        ImageView productImage;
        TextView productAddOns;
        LinearLayout addonsContainer;
        TextView productSugar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
//            productAddOns = itemView.findViewById(R.id.productAddOns);
            addonsContainer = itemView.findViewById(R.id.addonsContainer);
            productSugar = itemView.findViewById(R.id.productSugar);
        }
    }
}

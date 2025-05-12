package ordering.app.avenuet_housebongabong;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private Context context;
    private List<Product> favoritesList;

    // Constructor
    public FavoritesAdapter(Context context, List<Product> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the product_card layout
        View view = LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        // Get the current product
        Product product = favoritesList.get(position);

        // Bind product data to views
        holder.productNameTextView.setText(product.getProductName());

        String allPrice = "";

        if (product.getSmall() != null && !product.getSmall().isEmpty()) {
            allPrice = "₱" + product.getSmall();
        }

        if (product.getRegular() != null && !product.getRegular().isEmpty()) {
            if (!allPrice.isEmpty()) {
                allPrice += "-";
            }
            allPrice += "₱" + product.getRegular();
        }

        if (product.getLarge() != null && !product.getLarge().isEmpty()) {
            if (!allPrice.isEmpty()) {
                allPrice += "-";
            }
            allPrice += "₱" + product.getLarge();
        }

        holder.productPriceTextView.setText(allPrice);

        // Load the image using Glide or any image loading library
        Glide.with(context)
                .load(product.getImageURL())
                .into(holder.productImageView);

        if ("unavailable".equals(product.getStatus())) {
            holder.overlayImage.setVisibility(View.VISIBLE);  // Show the overlay
            holder.itemView.setClickable(false);  // Disable click on the item
//                itemView.setAlpha(0.5f); // Optionally set opacity to visually indicate unavailability
        } else {
            holder.overlayImage.setVisibility(View.GONE);  // Hide the overlay
            holder.itemView.setOnClickListener(v -> {
                // Create an Intent to start the ProductDetailActivity
                Intent intent = new Intent(context, DetailProductActivity.class);

                // Pass product details to the new activity
                intent.putExtra("productName", product.getProductName());
                intent.putExtra("productImageURL", product.getImageURL());
                intent.putExtra("productDescription", product.getDescription()); // Add more fields as needed
                intent.putExtra("productSmallPrice", product.getSmall());
                intent.putExtra("regularPrice", product.getRegular());
                intent.putExtra("productLargePrice", product.getLarge());

                // Start the ProductDetailActivity
                context.startActivity(intent);
            });
        }

        // Update the heart icon based on product's liked state
        updateHeartIcon(product, holder.heartImageView);

        // Toggle heart icon when clicked
        holder.heartImageView.setOnClickListener(v -> {
            String userName = getUserNameFromSharedPreferences();
            if (product.isLiked()) {
                removeProductFromFavorites(product, userName);
                holder.heartImageView.setImageResource(R.drawable.heart_outline);
                product.setLiked(false);
            } else {
                // If you want to allow re-adding favorites, use this
                saveProductToFavorites(product, userName);
                holder.heartImageView.setImageResource(R.drawable.heart_fill);
                product.setLiked(true);
            }
        });




    }
    private String getUserNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("fullName", "DefaultUserName");
    }


    private void removeProductFromFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        MotionToast.Companion.createColorToast((Activity) context,
                                "Hurray success 😍",
                                "Item removed from Favorites",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context, R.font.herobold));
                    } else {
                        // Show error Toast message
                        MotionToast.Companion.createColorToast((Activity) context,
                                "Failed ☹️",
                                "Failed to remove the item!",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(context, R.font.herobold));
                    }
                });
    }


    // Method to save a product to the user's favorites
    private void saveProductToFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).setValue(product);
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    // Update heart icon based on the product's liked status
    private void updateHeartIcon(Product product, ImageView heartImageView) {
        if (product.isLiked()) {
            heartImageView.setImageResource(R.drawable.heart_fill); // Filled heart icon
        } else {
            heartImageView.setImageResource(R.drawable.heart_outline); // Outlined heart icon
        }
    }

    // ViewHolder class
    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView;
        ImageView heartImageView;
        FrameLayout overlayImage;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.prodPrice);
            productImageView = itemView.findViewById(R.id.productImage);
            heartImageView = itemView.findViewById(R.id.heartImageView);
            overlayImage = itemView.findViewById(R.id.overlayImage);
        }
    }
}

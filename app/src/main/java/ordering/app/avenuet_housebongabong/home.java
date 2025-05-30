package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class home extends Fragment {

    private RecyclerView bestSellerRecyclerView;
    private DatabaseReference productsRef;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    private OnProductClickListener onProductClickListener;
    private ViewPager2 imageSlider;
    private List<String> imageUrls;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProductClickListener) {
            onProductClickListener = (OnProductClickListener) context;
        }else {
            throw new RuntimeException(context.toString() + " must implement OnProductClickListener");
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView for best seller products
        bestSellerRecyclerView = rootView.findViewById(R.id.bestSeller);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        bestSellerRecyclerView.setLayoutManager(layoutManager);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(bestSellerRecyclerView);




        // Initialize Firebase reference
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // Query products with best seller flag
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productsRef.orderByChild("bestSeller").equalTo(true), Product.class)  // Assuming "isBestSeller" is a boolean field in your database
                .build();

        // Initialize adapter
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                holder.setProductDetails(model);

                String userName = getUserNameFromSharedPreferences();
                isProductInFavorites(model, userName, isFavorite -> updateHeartIcon(model, isFavorite, holder.heartImageView));

                holder.itemView.setOnClickListener(v -> {
                    if (!"unavailable".equals(model.getStatus())) {
                        onProductClickListener.onProductClick(model);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bestseller, parent, false);
                return new ProductViewHolder(view);
            }
        };

        // Set the adapter to the RecyclerView
        bestSellerRecyclerView.setAdapter(adapter);

        ViewPager2 imageSlider = rootView.findViewById(R.id.imageSlider);

        int[] images = { R.drawable.promotion, R.drawable.promotion1, R.drawable.banner3 };
        imageSlider image = new imageSlider(requireContext(), images, imageSlider);
        imageSlider.setAdapter(image);

        image.startAutoSlide();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

        // Listen for changes in the favorites node
        String userName = getUserNameFromSharedPreferences();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged(); // Refresh adapter based on new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening(); // Start listening for data changes
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onProductClickListener = null; // Avoid memory leaks
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productName, price;
        private ImageView heartImageView;
        private FrameLayout overlayImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.prodPrice);
            heartImageView = itemView.findViewById(R.id.heartImageView);
            overlayImage = itemView.findViewById(R.id.overlayImage);
        }

        // Method to set product details to the views
        public void setProductDetails(Product product) {
            productName.setText(product.getProductName());

            String allPrice = "₱" + product.getSmall() + "-" + product.getLarge();
            price.setText(allPrice);

            Glide.with(itemView.getContext())
                    .load(product.getImageURL())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(productImage);

            updateHeartIcon(product, product.isLiked(), heartImageView);

            // Check if the product is unavailable and show the overlay
            if ("unavailable".equals(product.getStatus())) {
                overlayImage.setVisibility(View.VISIBLE);
                itemView.setClickable(false);
            } else {
                overlayImage.setVisibility(View.GONE);
                itemView.setClickable(true);
            }

            // Toggle heart icon when clicked
            heartImageView.setOnClickListener(v -> {
                String userName = getUserNameFromSharedPreferences();
                if (product.isLiked()) {
                    removeProductFromFavorites(product, userName);
                    heartImageView.setImageResource(R.drawable.heart_outline);
                } else {
                    saveProductToFavorites(product, userName);
                    heartImageView.setImageResource(R.drawable.heart_fill);
                }
                product.setLiked(!product.isLiked());
            });
        }
    }

    private String getUserNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("fullName", "DefaultUserName");
    }

    private void isProductInFavorites(Product product, String userName, OnProductCheckListener listener) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onProductCheck(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onProductCheck(false);
            }
        });
    }

    public interface OnProductCheckListener {
        void onProductCheck(boolean isFavorite);
    }

    private void updateHeartIcon(Product product, boolean isFavorite, ImageView heartImageView) {
        if (isFavorite) {
            product.setLiked(true);
            heartImageView.setImageResource(R.drawable.heart_fill);
        } else {
            product.setLiked(false);
            heartImageView.setImageResource(R.drawable.heart_outline);
        }
    }

    private void removeProductFromFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).removeValue();
    }

    private void saveProductToFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).setValue(product);
    }
}

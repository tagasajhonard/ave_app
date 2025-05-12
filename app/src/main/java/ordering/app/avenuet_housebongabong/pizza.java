package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;

public class pizza extends Fragment {

    private RecyclerView productRecyclerView;
    private DatabaseReference productsRef;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the context implements the interface
        if (context instanceof OnProductClickListener) {
            onProductClickListener = (OnProductClickListener) context;
        } else {
//            throw new RuntimeException(context.toString() + " must implement OnProductClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pizza, container, false);

        productRecyclerView = rootView.findViewById(R.id.productRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        productRecyclerView.setLayoutManager(layoutManager);

        // Initialize Firebase
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        // Query products with category "Milktea"
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(productsRef.orderByChild("Category").equalTo("Pizza"), Product.class)
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
                return new ProductViewHolder(view);

            }
        };

        // Set adapter to RecyclerView
        productRecyclerView.setAdapter(adapter);

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
        private TextView productName ,price ;
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

            price.setText(allPrice);

            Glide.with(itemView.getContext())
                    .load(product.getImageURL())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(productImage);

            updateHeartIcon(product, product.isLiked(), heartImageView);
            if ("unavailable".equals(product.getStatus())) {
                overlayImage.setVisibility(View.VISIBLE);  // Show the overlay
                itemView.setClickable(false);  // Disable click on the item
//                itemView.setAlpha(0.5f); // Optionally set opacity to visually indicate unavailability
            } else {
                overlayImage.setVisibility(View.GONE);  // Hide the overlay
                itemView.setClickable(true);  // Enable click on the item
//                itemView.setAlpha(1.0f); // Restore opacity
            }
            // Toggle heart icon when clicked
            heartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userName = getUserNameFromSharedPreferences();
                    if (product.isLiked()) {
                        removeProductFromFavorites(product, userName);
                        heartImageView.setImageResource(R.drawable.heart_outline);
                    } else {
                        saveProductToFavorites(product, userName);
                        heartImageView.setImageResource(R.drawable.heart_fill);
                    }
                    product.setLiked(!product.isLiked());
                }
            });
        }
    }

    private String getUserNameFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("fullName", "DefaultUserName");
    }

    // Method to check if the product is in the user's favorites
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

    // Callback interface for product check
    public interface OnProductCheckListener {
        void onProductCheck(boolean isFavorite);
    }

    // Method to update the heart icon
    private void updateHeartIcon(Product product, boolean isFavorite, ImageView heartImageView) {
        if (isFavorite) {
            product.setLiked(true);
            heartImageView.setImageResource(R.drawable.heart_fill);
        } else {
            product.setLiked(false);
            heartImageView.setImageResource(R.drawable.heart_outline);
        }
    }

    // Method to remove a product from the user's favorites
    private void removeProductFromFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).removeValue();
    }

    // Method to save a product to the user's favorites
    private void saveProductToFavorites(Product product, String userName) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Favorites").child(userName);
        favoritesRef.child(product.getProductName()).setValue(product);
    }
}

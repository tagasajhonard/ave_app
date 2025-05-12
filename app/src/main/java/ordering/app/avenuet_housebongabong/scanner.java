package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;


import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseArray;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class scanner extends AppCompatActivity {

    private androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    private TextView noResultsText;
    private ImageView backArrow;
    private LottieAnimationView lottieAnimationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);  // Your layout file



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

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.searchRecyclerView);
        noResultsText = findViewById(R.id.noResultsTextView);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(scanner.this, homeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initially hide RecyclerView
        recyclerView.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);

        // Check if the app has permission to use the camera




        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search operation here
                Toast.makeText(scanner.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    // Ensure the first letter is uppercase
                    String capitalizedText = capitalizeFirstLetter(newText);
                    Log.d("SearchQuery", "Query: " + capitalizedText);  // Check the transformed input

                    recyclerView.setVisibility(View.VISIBLE);
                    noResultsText.setVisibility(View.GONE);
                    loadProducts(capitalizedText);  // Use the transformed text for searching
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noResultsText.setVisibility(View.GONE);
                }
                return true;
            }

        });
    }




    private String capitalizeFirstLetter(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    private void loadProducts(String query) {

        Query firebaseSearchQuery = mDatabase.orderByChild("ProductName")
                .startAt(query).endAt(query + "\uf8ff");  // Use query to match product names

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(firebaseSearchQuery, Product.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
//                Log.d("Product", "Binding product: " + model.getProductName());
//                holder.bind(model);
                if (model.getCategory().equals("Add-Ons")) {
                    // Hide the holder for this product if it's "Add-ons"
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));  // Remove from layout
                } else {
                    // Otherwise, bind the product normally
                    Log.d("Product", "Binding product: " + model.getProductName());
                    holder.itemView.setVisibility(View.VISIBLE);  // Ensure visibility for valid items
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    holder.bind(model);
                }
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                // Show or hide RecyclerView based on data availability
                if (adapter.getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    noResultsText.setVisibility(View.VISIBLE);// No results, hide RecyclerView
                    Log.d("SearchResult", "No products found");
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noResultsText.setVisibility(View.GONE);// Results found, show RecyclerView
                    Log.d("SearchResult", "Products found: " + adapter.getItemCount());
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }




    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
        }

        public void bind(Product product) {
            productName.setText(product.getProductName());

            // Set click listener on the product name TextView
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), DetailProductActivity.class); // Replace with your activity
                intent.putExtra("productName", product.getProductName()); // Pass product name
                intent.putExtra("productDescription", product.getDescription());
                intent.putExtra("productImageURL", product.getImageURL());
                intent.putExtra("source", "search");
                v.getContext().startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();  // Start listening when activity starts
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (adapter != null) {
//            adapter.stopListening();  // Stop listening when activity stops
//        }
    }

}

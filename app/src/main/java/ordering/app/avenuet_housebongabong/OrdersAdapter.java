package ordering.app.avenuet_housebongabong;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Orders.Item> orderList;  // List of Orders, not Items
    private boolean isPendingFragment;
    private String fragmentType;
    private OnRateClickListener rateClickListener;



    public interface OnRateClickListener {
        void onRateClick(Orders.Item item);  // Pass the clicked item back to the fragment
    }

    public OrdersAdapter(Context context, List<Orders.Item> orderList, String fragmentType, OnRateClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.fragmentType = fragmentType;
        this.rateClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_to_ship, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Get the current item (not order, since this is a list of items)
        Orders.Item item = orderList.get(position);

        // Set item data to the corresponding views
        holder.productName.setText(item.getProductName());
        holder.productPrice.setText("Price : " + item.getItemTotal());
        holder.quantity.setText("x" + item.getQuantity());
        holder.sugar.setText("Sugar Level : " + String.valueOf(item.getSugarLevel())+ "%");
        holder.size.setText("Product Size : " +item.getSize());

        Glide.with(context).load(item.getProductImageUrl()).into(holder.productImage);
        holder.addonsTextView.setText(item.getAddonsAsString());


        if ("Rate".equals(fragmentType)) {

            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String custname = sharedPreferences.getString("fullName", "No name found");

//            holder.cancelOrder.setText("Rate");
//            holder.cancelOrder.setVisibility(View.VISIBLE);
//
//            holder.cancelOrder.setOnClickListener(v -> rateClickListener.onRateClick(item));

            String cartItemId = orderList.get(position).getCartItemId();
            DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(custname);

            // Check if a rating for the cartItemId already exists
            ratingsRef.orderByChild("cartItemId").equalTo(cartItemId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        holder.cancelOrder.setText("Rated");
                        holder.cancelOrder.setEnabled(false);
                        holder.cancelOrder.setBackgroundColor(ContextCompat.getColor(context, R.color.topbrown)); // Optional: change background color

                    } else {
                        // If no rating exists, show the "Rate" button
                        holder.cancelOrder.setText("Rate");
                        holder.cancelOrder.setVisibility(View.VISIBLE);

                        holder.cancelOrder.setOnClickListener(v -> rateClickListener.onRateClick(item));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("DatabaseError", "onCancelled: " + error.getMessage());
                }
            });

        } else if ("Pending".equals(fragmentType)) {
            holder.cancelOrder.setText("Cancel Order");
            holder.cancelOrder.setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String custname = sharedPreferences.getString("fullName", "No name found");

            holder.cancelOrder.setOnClickListener(v -> {

                String orderId = orderList.get(position).getOrderId();

                Toast.makeText(context, "Order " + orderId + " cancelled", Toast.LENGTH_SHORT).show();

                new AlertDialog.Builder(context)
                        .setTitle("Cancel Order")
                        .setMessage("Are you sure you want to cancel this order?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Get the item position
                            holder.getAdapterPosition();

                            // Get the cartItemId of the clicked item
                            String cartItemId = orderList.get(position).getCartItemId();

                            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders")
                                    .child(custname)
                                    .child(orderId)
                                    .child("items");

                            ordersRef.orderByChild("cartItemId").equalTo(cartItemId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                        // Update the status field to "cancelled"
                                        itemSnapshot.getRef().child("status").setValue("cancelled");
                                    }

                                    // Optionally, show a toast to confirm the update
                                    Toast.makeText(context, "Order cancelled", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("DatabaseError", "onCancelled: " + error.getMessage());
                                }
                            });

                        })
                        .setNegativeButton("No", null)
                        .show();
            });

        } else {
            holder.cancelOrder.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return orderList.size();  // Return the total number of orders
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantity;
        TextView size, sugar;
        TextView addonsTextView;
        TextView cancelOrder;

        // Constructor initializes views from the item layout
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
            size = itemView.findViewById(R.id.size);
            addonsTextView = itemView.findViewById(R.id.addonsTextView);
            sugar = itemView.findViewById(R.id.sugar);
            cancelOrder = itemView.findViewById(R.id.cancelOrder);
        }
    }



}

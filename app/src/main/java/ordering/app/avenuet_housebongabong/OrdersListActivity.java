package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orders_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView currentOrdersRecycler = findViewById(R.id.currentOrdersRecyclerView);
        RecyclerView pastOrdersRecycler = findViewById(R.id.pastOrdersRecyclerView);

        currentOrdersRecycler.setLayoutManager(new LinearLayoutManager(this));
        pastOrdersRecycler.setLayoutManager(new LinearLayoutManager(this));

        List<Orders> currentOrders = new ArrayList<>();
        List<Orders> pastOrders = new ArrayList<>();

        OrderIdAdapter currentAdapter = new OrderIdAdapter(this, currentOrders);
        OrderIdAdapter pastAdapter = new OrderIdAdapter(this, pastOrders);

        currentOrdersRecycler.setAdapter(currentAdapter);
        pastOrdersRecycler.setAdapter(pastAdapter);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "No name");


        currentAdapter.setOnItemClickListener(orderId -> {

            Intent intent = new Intent(OrdersListActivity.this, purchase.class);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fullName", userName);
            intent.putExtra("orderId", orderId);
            intent.putExtra("fragmentIndex", 0);

            editor.apply();

            startActivity(intent);
        });

        pastAdapter.setOnItemClickListener(orderId -> {

            Intent intent = new Intent(OrdersListActivity.this, purchase.class);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fullName", userName);
            intent.putExtra("orderId", orderId);
            intent.putExtra("fragmentIndex", 3);

            editor.apply();

            startActivity(intent);
        });





        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userName);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentOrders.clear();
                pastOrders.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    String status = orderSnapshot.child("status").getValue(String.class);
                    String date = orderSnapshot.child("orderTime").getValue(String.class);

                    if (orderId != null && status != null && date != null) {
                        Orders order = new Orders();
                        order.setOrderId(orderId);
                        order.setStatus(status);
                        order.setOrderTime(date);

                        if (status.equalsIgnoreCase("pending")) {
                            currentOrders.add(order); // Add to currentOrders only if status is "pending"
                        } else if (status.equalsIgnoreCase("delivered")) {
                            pastOrders.add(order); // Add to pastOrders only if status is "delivered"
                        }
                    }
                }

                currentAdapter.notifyDataSetChanged();
                pastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });

    }

}
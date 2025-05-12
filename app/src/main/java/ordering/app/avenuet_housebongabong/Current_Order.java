package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Current_Order extends Fragment {

    private RecyclerView orderRecyclerView;
    private List<Orders> ordersList = new ArrayList<>();
    private OrderIdAdapter orderAdapter;
    private SharedPreferences sharedPreferences;
    private String userName;

    public Current_Order() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current__order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        orderRecyclerView = view.findViewById(R.id.currentOrdersRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderAdapter = new OrderIdAdapter(getContext(), ordersList);
        orderRecyclerView.setAdapter(orderAdapter);

        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("fullName", "No name");

        orderAdapter.setOnItemClickListener((orderId, status) -> {
            Intent intent = new Intent(getContext(), purchase.class);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fullName", userName);
            intent.putExtra("orderId", orderId);

            // Set fragmentIndex based on status
            int fragmentIndex = 0;
            if ("accepted".equalsIgnoreCase(status)) {
                fragmentIndex = 1;
            } else if ("in transit".equalsIgnoreCase(status)) {
                fragmentIndex = 2;
            }

            intent.putExtra("fragmentIndex", fragmentIndex);
            editor.apply();
            startActivity(intent);
        });


        loadOrders();
    }

    private void loadOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference("Orders")
                .child(userName);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderId = orderSnapshot.getKey();
                    String status = orderSnapshot.child("status").getValue(String.class);
                    String date = orderSnapshot.child("orderTime").getValue(String.class);

                    if (orderId != null && status != null && date != null) {
                        if (status.equalsIgnoreCase("pending") ||
                                status.equalsIgnoreCase("accepted") ||
                                status.equalsIgnoreCase("in transit")) {

                            Orders order = new Orders();
                            order.setOrderId(orderId);
                            order.setStatus(status);
                            order.setOrderTime(date);
                            ordersList.add(order);
                        }

                    }
                }

                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}

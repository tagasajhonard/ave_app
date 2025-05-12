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


public class Past_Order extends Fragment {

    private RecyclerView orderRecyclerView;
    private List<Orders> ordersList = new ArrayList<>();
    private OrderIdAdapter orderAdapter;
    private SharedPreferences sharedPreferences;
    private String userName;


    public Past_Order() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        orderRecyclerView = view.findViewById(R.id.pastOrdersRecyclerView);
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

            intent.putExtra("fragmentIndex", 3);
            editor.apply();
            startActivity(intent);
        });

        loadOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past__order, container, false);
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
                        if (status.equalsIgnoreCase("delivered")) {
                            Orders order = new Orders();
                            order.setOrderId(orderId);
                            order.setStatus(status); // e.g., "pending"
                            order.setOrderTime(date);
                            ordersList.add(order); // Add only pending orders
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
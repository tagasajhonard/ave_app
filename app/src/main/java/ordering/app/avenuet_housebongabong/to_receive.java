package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class to_receive extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<Orders.Item> orderList;
    private List<Orders> orderLists;
    private DatabaseReference ordersRef;
    private LinearLayout noOrdersLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_receive, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewToShip);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noOrdersLayout = view.findViewById(R.id.noOrdersLayout);

        orderList = new ArrayList<>();
        adapter = new OrdersAdapter(getContext(), orderList,  "in transit",null);
        recyclerView.setAdapter(adapter);

        // Get reference to 'Orders' node in Firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Fetch data from Firebase
        fetchOrdersFromFirebase();

        return view;
    }
    private void fetchOrdersFromFirebase() {
        // Retrieve customer name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String custname = sharedPreferences.getString("fullName", "No name found");

        // Get reference to 'Orders' node in Firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        ordersRef.child(custname).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear(); // Clear previous data

                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Orders order = orderSnapshot.getValue(Orders.class);
                    if (order != null && "in transit".equals(order.getStatus())) {
                        List<Orders.Item> items = order.getItems();
                        if (items != null) {
                            for (DataSnapshot cartItemSnapshot : orderSnapshot.child("items").getChildren()) {
                                Orders.Item cartItem = cartItemSnapshot.getValue(Orders.Item.class);

                                cartItem.clearAddons();  // Assuming this method clears the add-on list


                                // Check if the item has add-ons
                                if (cartItemSnapshot.hasChild("addons")) {
                                    Log.d("AddOnFetch", "Fetching add-ons for item: " + cartItem.getProductName());

                                    // Fetch add-ons from Firebase
                                    for (DataSnapshot addonSnapshot : cartItemSnapshot.child("addons").getChildren()) {
                                        String addonName = addonSnapshot.child("name").getValue(String.class);
                                        String addonImageUrl = addonSnapshot.child("imageUrl").getValue(String.class);
                                        double addonPrice = addonSnapshot.child("price").getValue(Double.class);
                                        int addonQuantity = addonSnapshot.child("quantity").getValue(Integer.class);

                                        // Log the add-on details
                                        Log.d("AddOnFetch", "Addon Name: " + addonName);
                                        Log.d("AddOnFetch", "Addon Image URL: " + addonImageUrl);
                                        Log.d("AddOnFetch", "Addon Price: " + addonPrice);
                                        Log.d("AddOnFetch", "Addon Quantity: " + addonQuantity);

                                        // Create AddonItem and add to cartItem
                                        AddonItem addonItem = new AddonItem(addonName, addonImageUrl, addonPrice);
                                        addonItem.setSelectedQuantity(addonQuantity);
                                        cartItem.addAddon(addonItem);  // Assuming addAddon() method exists in Orders.Item
                                    }
                                } else {
                                    Log.d("AddOnFetch", "No add-ons found for item: " + cartItem.getProductName());
                                }

                                // Add the cart item to the orderList
                                orderList.add(cartItem);

                                // Log the cart item being added
                                Log.d("CartItem", "Added Cart Item: " + cartItem.getProductName());
                            }
                        }
                    }
                }

                // Notify adapter about data changes

                adapter.notifyDataSetChanged();

                if (orderList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noOrdersLayout.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noOrdersLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.e("DatabaseError", "onCancelled: " + error.getMessage());
            }
        });

    }
}

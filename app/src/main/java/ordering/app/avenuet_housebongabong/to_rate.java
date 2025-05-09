package ordering.app.avenuet_housebongabong;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class to_rate extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private Map<String, List<Orders.Item>> orderMap;
    private List<Orders.Item> orderList;
    private DatabaseReference ordersRef;
    private LinearLayout noOrdersLayout;
    private LinearLayout recyclerHold;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_rate, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewToShip);

        recyclerHold = view.findViewById(R.id.recyclerHolder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noOrdersLayout = view.findViewById(R.id.noOrdersLayout);

        orderList = new ArrayList<>();
        orderMap = new HashMap<>(); // Initialize orderMap to avoid NullPointerException
//        adapter = new OrdersAdapter(getContext(), orderList, "Rate", null);
        adapter = new OrdersAdapter(getContext(), orderList, "Rate", item -> {
            // Handle the "Rate" button click here
            showRatingDialog(item);  // Pass the selected item to your rating dialog method
        });
        recyclerView.setAdapter(adapter);

        // Get reference to 'Orders' node in Firebase
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Fetch data from Firebase
        fetchOrdersFromFirebase();

        return view;


    }


    private void showRatingDialog(Orders.Item item) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rate, null);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String custname = sharedPreferences.getString("fullName", "No name found");

        final RatingBar ratingTaste = dialogView.findViewById(R.id.rating_taste);
        final RatingBar ratingDelivery = dialogView.findViewById(R.id.rating_delivery);
        final RatingBar ratingQuality = dialogView.findViewById(R.id.rating_quality);
        final RatingBar ratingService = dialogView.findViewById(R.id.rating_service);
        final EditText feedbackEditText = dialogView.findViewById(R.id.feedback_text); // New feedback field

        // Create the AlertDialog to show the rating bars
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float tasteRating = ratingTaste.getRating();
                        float deliveryRating = ratingDelivery.getRating();
                        float qualityRating = ratingQuality.getRating();
                        float serviceRating = ratingService.getRating();

                        String feedback = feedbackEditText.getText().toString();

                        Map<String, Object> ratingData = new HashMap<>();
                        ratingData.put("tasteRating", tasteRating);
                        ratingData.put("deliveryRating", deliveryRating);
                        ratingData.put("qualityRating", qualityRating);
                        ratingData.put("serviceRating", serviceRating);
                        ratingData.put("customerName", custname);
                        ratingData.put("cartItemId", item.getCartItemId());
                        ratingData.put("productName", item.getProductName());


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String currentDateAndTime = sdf.format(new Date());
                        ratingData.put("timestamp", currentDateAndTime); // Store the formatted date and time


                        if (!feedback.isEmpty()) {
                            ratingData.put("feedback", feedback);
                        }else{
                            feedback = "No feedback text";
                        }
                        // Get Firebase reference for the separate 'Ratings' node
                        DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                                .getReference("Ratings")
                                .child(custname)
                                .child(item.getCartItemId());

                        ratingsRef.setValue(ratingData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            MotionToast.Companion.createColorToast((Activity) getContext(),
                                                    "Hurray success 😍",
                                                    "Thank you for your ratings",
                                                    MotionToastStyle.SUCCESS,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(getContext(), R.font.herobold));
                                        } else {
                                            MotionToast.Companion.createColorToast((Activity) getContext(),
                                                    "Failed ☹️",
                                                    "Rating Failed!",
                                                    MotionToastStyle.ERROR,
                                                    MotionToast.GRAVITY_BOTTOM,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(getContext(), R.font.herobold));
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void fetchOrdersFromFirebase() {
        // Retrieve customer name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String custname = sharedPreferences.getString("fullName", "No name found");

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.child(custname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                // Fetch rated cartItemIds first
                DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                        .getReference("Ratings")
                        .child(custname);

                ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ratingSnapshot) {
                        Set<String> ratedIds = new HashSet<>();
                        for (DataSnapshot snap : ratingSnapshot.getChildren()) {
                            String id = snap.child("cartItemId").getValue(String.class);
                            if (id != null) ratedIds.add(id);
                        }

                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Orders order = orderSnapshot.getValue(Orders.class);
                            if (order != null && "delivered".equals(order.getStatus())) {
                                for (DataSnapshot cartItemSnapshot : orderSnapshot.child("items").getChildren()) {
                                    Orders.Item cartItem = cartItemSnapshot.getValue(Orders.Item.class);
                                    if (cartItem != null) {
                                        cartItem.clearAddons();

                                        if (cartItemSnapshot.hasChild("addons")) {
                                            for (DataSnapshot addonSnapshot : cartItemSnapshot.child("addons").getChildren()) {
                                                String addonName = addonSnapshot.child("name").getValue(String.class);
                                                String addonImageUrl = addonSnapshot.child("imageUrl").getValue(String.class);
                                                double addonPrice = addonSnapshot.child("price").getValue(Double.class);
                                                int addonQuantity = addonSnapshot.child("quantity").getValue(Integer.class);

                                                AddonItem addonItem = new AddonItem(addonName, addonImageUrl, addonPrice);
                                                addonItem.setSelectedQuantity(addonQuantity);
                                                cartItem.addAddon(addonItem);
                                            }
                                        }

                                        // Set rated status
                                        boolean isRated = ratedIds.contains(cartItem.getCartItemId());
                                        cartItem.setRated(isRated);
                                        orderList.add(cartItem);
                                    }
                                }
                            }
                        }


                        // Sort: unrated first
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            orderList.sort((item1, item2) -> Boolean.compare(item1.isRated(), item2.isRated()));
                        }

                        adapter.notifyDataSetChanged();

                        if (orderList.isEmpty()) {
                            recyclerHold.setVisibility(View.GONE);
                            noOrdersLayout.setVisibility(View.VISIBLE);
                        } else {
                            recyclerHold.setVisibility(View.VISIBLE);
                            noOrdersLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("RatingsError", "onCancelled: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrdersError", "onCancelled: " + error.getMessage());
            }
        });


    }


}

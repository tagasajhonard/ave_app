package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ordering.app.avenuet_housebongabong.Orders;

public class purchase extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private static final long DOUBLE_PRESS_INTERVAL = 2000; // 2 seconds
    private boolean isBackPressedOnce = false;
    private Handler handler;
    private RecyclerView recyclerView;
    private OrderTrackingAdapter adapter;
    private String acceptedTime;
    private String orderTime;
    private String shipTime;
    private String receivedTime;


    private Button showMoreButton;
    private Button hideButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        showMoreButton = findViewById(R.id.show_more_button);
        hideButton = findViewById(R.id.hide_button);

        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show all steps
                adapter.toggleStepsVisibility(true);
                showMoreButton.setVisibility(View.GONE);
                hideButton.setVisibility(View.VISIBLE);
            }
        });

        // Hide button click listener
        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show only 1 or 2 steps
                adapter.toggleStepsVisibility(false);
                hideButton.setVisibility(View.GONE);
                showMoreButton.setVisibility(View.VISIBLE);
            }
        });


        recyclerView = findViewById(R.id.recycler_view_tracking);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OrderTrackingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "User");
//        String savedUsername = sharedPreferences.getString("username", "");

        getOrderFromFirebase(fullName);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            boolean isLightBackground = true;

            window.setStatusBarColor(Color.TRANSPARENT);

            if (isLightBackground) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }


        // Initialize TabLayout and ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set up ViewPager2 with an adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        handler = new Handler();


        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                // Set the text for each tab
                switch (position) {
                    case 0:
                        tab.setText("Pending");
                        tab.setIcon(R.drawable.pending); // Set your custom truck icon
                        break;
                    case 1:
                        tab.setText("To Ship");
                        tab.setIcon(R.drawable.toship); // Set your custom truck icon
                        break;
                    case 2:
                        tab.setText("To Receive");
                        tab.setIcon(R.drawable.toreceived); // Set your custom open box icon
                        break;
                    case 3:
                        tab.setText("Completed");
                        tab.setIcon(R.drawable.torate); // Set your custom checkmark icon
                        break;
                    case 4:
                        tab.setText("Cancelled");
                        tab.setIcon(R.drawable.cancelled); // Set your custom checkmark icon
                        break;
                }
            }
        }).attach();
    }



    private void getOrderFromFirebase(String userId) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TrackingStep> allSteps = new ArrayList<>(); // Hold all tracking steps

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Orders order = orderSnapshot.getValue(Orders.class);
                    if (order != null) {
                        // Generate tracking steps for each order
                        acceptedTime = order.getAcceptedTime();
                        orderTime = order.getOrderTime();
                        shipTime = order.getShipTime();
                        receivedTime = order.getReceivedTime();

                        List<TrackingStep> trackingSteps = generateTrackingSteps(order, orderTime ,acceptedTime,shipTime,receivedTime);
                        allSteps.addAll(trackingSteps); // Combine all steps from different orders
                    }
                }

                // Update adapter with all combined tracking steps
                adapter = new OrderTrackingAdapter(allSteps);
                recyclerView.setAdapter(adapter);

                if (allSteps.isEmpty()) {
                    recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                } else {
                    recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                }

                if (allSteps.size() <= 2) {
                    showMoreButton.setVisibility(View.GONE); // Hide "Show More" button if 2 or fewer steps
                    hideButton.setVisibility(View.GONE);     // Hide "Hide" button if 2 or fewer steps
                } else {
                    showMoreButton.setVisibility(View.VISIBLE); // Show "Show More" button if there are 3 or more steps
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    public List<TrackingStep> generateTrackingSteps(Orders order, String orderTime, String acceptedTime, String shipTime, String receivedTime) {
        List<TrackingStep> trackingSteps = new ArrayList<>();

        if ("history".equals(order.getStatus())) {
            return trackingSteps; // Empty list
        }

        // Add steps
        trackingSteps.add(new TrackingStep("Order Placed", "Your order has been placed.", orderTime, null, null,null));

        if ("accepted".equals(order.getStatus())) {
            trackingSteps.add(new TrackingStep("Order Accepted", "Your order has been accepted by the admin.",null, acceptedTime ,null,null));
        }
        if ("in transit".equals(order.getStatus())) {
            trackingSteps.add(new TrackingStep("Order Accepted", "Your order has been accepted by the admin.",null, acceptedTime, null,null));
            trackingSteps.add(new TrackingStep("In Transit", "Your order is on the way.",null, null, shipTime,null));
        }
        if ("delivered".equals(order.getStatus())) {
            trackingSteps.add(new TrackingStep("Order Accepted", "Your order has been accepted by the admin.",null, acceptedTime,null,null));
            trackingSteps.add(new TrackingStep("In Transit", "Your order is on the way.",null, null,shipTime,null));
            trackingSteps.add(new TrackingStep("Delivered", "Your order has been delivered.",null, null,null,receivedTime));
        }

        return trackingSteps;
    }





    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    // Adapter class for ViewPager2
    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return a different fragment based on the position
            switch (position) {
                case 0:
                    return new pending();
                case 1:
                    return new to_ship();
                case 2:
                    return new to_receive();
                case 3:
                    return new to_rate();
                case 4:
                    return new cancelled();
                default:
                    return new pending();
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Number of tabs
        }
    }
}

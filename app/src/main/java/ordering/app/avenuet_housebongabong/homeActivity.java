package ordering.app.avenuet_housebongabong;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class homeActivity extends AppCompatActivity implements Milktea.OnProductClickListener, fruit_tea.OnProductClickListener, frappe.OnProductClickListener, milkshake.OnProductClickListener, coolers.OnProductClickListener, sandwich.OnProductClickListener, pasta.OnProductClickListener, pizza.OnProductClickListener, mozzarella.OnProductClickListener, fries.OnProductClickListener, cheese_sticks.OnProductClickListener {
    private androidx.appcompat.widget.SearchView searchView;
    private LinearLayout chat;
    private FrameLayout cart;
    private TextView cartItemCountTextView;
    private ImageView detailProductImage, showdp;
    private TextView detailProductName, txttoken;
    private FrameLayout fragment_container;
    private LinearLayout homeUI, favorites, faq;
    private ImageView profileMenu;
    private boolean isMenuOpen = false;
    private DatabaseReference mDatabase;
    private RelativeLayout main;
    private TextView showname;
    private ImageView scan;

    private boolean isBackPressedOnce = false;
    private Handler handler;
    private static final long DOUBLE_PRESS_INTERVAL = 2000;
    private RecyclerView recyclerView;

    private ValueEventListener statusListener;
    private DatabaseReference userRef;
    private boolean isFaqVisible = false;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


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

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (!username.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);
        }

        LinearLayout aboutClick = findViewById(R.id.aboutClick);
        LinearLayout subMenu = findViewById(R.id.subMenu);
        faq = findViewById(R.id.faq);

        for (int i = 0; i < subMenu.getChildCount(); i++) {
            subMenu.getChildAt(i).setVisibility(View.GONE); // Hide all child views
        }

        aboutClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subMenu.getChildAt(0).getVisibility() == View.VISIBLE) {

                    for (int i = 0; i < subMenu.getChildCount(); i++) {
                        final View child = subMenu.getChildAt(i);
                        child.animate()
                                .alpha(0f)
                                .setDuration(500)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        child.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                    }
                } else {

                    for (int i = 0; i < subMenu.getChildCount(); i++) {
                        final View child = subMenu.getChildAt(i);
                        child.setVisibility(View.VISIBLE);
                        child.setAlpha(0f);
                        child.animate()
                                .alpha(1f)
                                .setStartDelay(i * 100)
                                .setDuration(500)
                                .start();
                    }
                }
            }
        });

        LinearLayout show_profile = findViewById(R.id.show_profile);
        show_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this, user_info.class);
                startActivity(intent);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, faqs.class);
                startActivity(intent);
            }
        });

        favorites = findViewById(R.id.favorites);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this, Favorites.class);
                startActivity(intent);
            }
        });

        scan = findViewById(R.id.scanner);
        searchView = findViewById(R.id.searchView);
//        recyclerView = findViewById(R.id.searchRecyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayout purchase = findViewById(R.id.wallet);
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this, purchase.class);
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                scanner
                    Intent intent = new Intent(homeActivity.this, ScannerActivity.class);
                    startActivity(intent);
                    finish();
            }
        });


        txttoken = findViewById(R.id.tokentext);
        showname = findViewById(R.id.showName);

        main = findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileMenu = findViewById(R.id.profileMenu);
        homeUI = findViewById(R.id.homeUI);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMenu();
            }
        });

        ImageView closeButton = findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int translationX = 0;
                int newWidth = ViewGroup.LayoutParams.MATCH_PARENT;
                int newHeight = ViewGroup.LayoutParams.MATCH_PARENT;
                float cornerRadius = 0f;
                float targetScale = 1f;

                homeUI.animate()
                        .translationX(translationX)
                        .translationY(0)  // Reset translationY animation
                        .scaleX(targetScale)  // Reset scaleX animation
                        .scaleY(targetScale)  // Reset scaleY animation
                        .setDuration(800)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                GradientDrawable background = new GradientDrawable();
                                background.setColor(getResources().getColor(R.color.semi_gray));
                                background.setCornerRadius(cornerRadius);
                                homeUI.setBackground(background);
                            }
                        }).start();

                // Update the menu state flag
                isMenuOpen = false;
            }
        });


        showdp = findViewById(R.id.showdp);

        if (!username.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        if (userImageUrl != null && !userImageUrl.isEmpty()) {
                            // Load the user's profile image
                            Glide.with(homeActivity.this)
                                    .load(userImageUrl)
                                    .placeholder(R.drawable.loading)
                                    .error(R.drawable.error)
                                    .centerCrop()
                                    .into(showdp);
                        } else {
                            // Load a default placeholder image if no URL is found
                            Glide.with(homeActivity.this)
                                    .load(R.drawable.placeholder) // Load placeholder directly
                                    .centerCrop()
                                    .into(showdp);
                        }
                    } else {
                        // Handle case where user does not exist
                        Glide.with(homeActivity.this)
                                .load(R.drawable.placeholder) // Load placeholder for non-existing user
                                .centerCrop()
                                .into(showdp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle possible errors here (log the error, show a message, etc.)
                    Toast.makeText(homeActivity.this, "Failed to load user image.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle case when username is empty, if needed
            Glide.with(homeActivity.this)
                    .load(R.drawable.placeholder) // Load a default image if username is empty
                    .centerCrop()
                    .into(showdp);
        }



        searchView.setIconifiedByDefault(true);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(homeActivity.this, scanner.class);
                startActivity(intent);
            }
        });



        chat = findViewById(R.id.chat);
        cart = findViewById(R.id.cart);

        cartItemCountTextView = findViewById(R.id.cartItemCountTextView);

        detailProductImage = findViewById(R.id.detailProductImage);
        detailProductName = findViewById(R.id.detailProductName);

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "User");
        String savedUsername = sharedPreferences.getString("username", "");
        Log.d("MyAppTag", "hoyyyyyy ito yungggg Saved Username: " + savedUsername);

        loadCartItemCount(fullName);

        TextView textViewFullName = findViewById(R.id.textViewUserName);


        if (!savedUsername.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(savedUsername);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        if (fullName != null) {
                            textViewFullName.setText(fullName); // Display the full name
                            showname.setText(fullName);
                        } else {
                            textViewFullName.setText("Full Name not found");
                            showname.setText("Full Name not found");
                        }
                    } else {
                        textViewFullName.setText("User does not exist");
                        showname.setText("Full Name not found");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle possible errors.
                }
            });
        } else {
            textViewFullName.setText("No username saved");
        }

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = textViewFullName.getText().toString();
                saveUserNameToSharedPreferences(userName);
            }
        });


        saveToSharedPreferences(fullName);

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passName();
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog for confirmation
                MaterialDialog mDialog = new MaterialDialog.Builder(homeActivity.this)
                        .setTitle("Logout?")
                        .setMessage("Are you sure want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Logout", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                logoutUser();
                                clearSharedPreferences();
                            }


                        })
                        .setNegativeButton("Cancel", new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }


                        })
                        .build();

                // Show Dialog
                mDialog.show();
            }
        });



        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        tabAdapter.addFragment(new home(), "Home");
        tabAdapter.addFragment(new Milktea(), "Milktea");
        tabAdapter.addFragment(new fruit_tea(), "Fruit Tea");
        tabAdapter.addFragment(new frappe(), "Frappe");
        tabAdapter.addFragment(new milkshake(), "Milkshake");
        tabAdapter.addFragment(new coolers(), "Coolers");
        tabAdapter.addFragment(new sandwich(), "Sandwich");
        tabAdapter.addFragment(new pasta(), "Pasta");
        tabAdapter.addFragment(new pizza(), "Pizza");
        tabAdapter.addFragment(new mozzarella(), "Mozzarella");
        tabAdapter.addFragment(new fries(), "Fries");
        tabAdapter.addFragment(new cheese_sticks(), "Chesse Sticks");

        viewPager.setAdapter(tabAdapter);

        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);
        int tabMarginHorizontal = (int) getResources().getDimension(R.dimen.tab_margin_horizontal);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View tabView = tab.view;

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                if (i > 0) {
                    layoutParams.leftMargin = tabMarginHorizontal;
                }
                if (i < tabLayout.getTabCount() - 1) {
                    layoutParams.rightMargin = tabMarginHorizontal;
                }
                tabView.setLayoutParams(layoutParams);
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        if (userRef != null) {
            // Add a Firebase ValueEventListener to monitor status changes in real-time
            statusListener = userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String status = snapshot.child("status").getValue(String.class);
                        if ("blocked".equalsIgnoreCase(status)) {
                            // Show dialog and redirect to login
                            showBlockedUserDialog();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error if necessary
                    Toast.makeText(homeActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // Method to show dialog if the user is blocked
    private void showBlockedUserDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Blocked User")
                .setMessage("Your account has been restricted due to violations of our policies. If you believe this is a mistake, please contact support for assistance.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear SharedPreferences and redirect to login
                        clearSharedPreferences();
                        Intent intent = new Intent(homeActivity.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }



    @Override
    protected void onPause() {
        super.onPause();
        // Remove the Firebase listener when the activity is paused to avoid memory leaks
        if (userRef != null && statusListener != null) {
            userRef.removeEventListener(statusListener);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void loadCartItemCount(String fullName) {
        DatabaseReference userCartRef = mDatabase.child("UserCart").child(fullName);

        userCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long itemCount = dataSnapshot.getChildrenCount();
                updateCartIcon(itemCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
    private void updateCartIcon(long itemCount) {
        if (itemCount > 0) {
            cartItemCountTextView.setText(String.valueOf(itemCount));
            cartItemCountTextView.setVisibility(View.VISIBLE);
        } else {
            cartItemCountTextView.setVisibility(View.GONE);
        }
    }
    private void passName(){
        String userName = getIntent().getStringExtra("userName");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "User");

        Intent intent = new Intent(homeActivity.this, chat.class);
        if (fullName != null && !fullName.isEmpty()) {
            intent.putExtra("fullName", fullName);
        } else {
            intent.putExtra("userName", userName);
        }
        startActivity(intent);
    }

    private void clearSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.apply();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        if (AccessToken.getCurrentAccessToken() != null && com.facebook.Profile.getCurrentProfile() != null) {
            LoginManager.getInstance().logOut();
        }
        navigateToLoginActivity();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(homeActivity.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);
        finish();
    }

    private void saveToSharedPreferences(String fName) {
        // Get the SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FullName", fName);
        editor.apply();
    }
@Override
public void onProductClick(Product product) {

    Intent intent = new Intent(this, DetailProductActivity.class);

    intent.putExtra("productImageURL", product.getImageURL());
    intent.putExtra("productName", product.getProductName());
    intent.putExtra("productDescription", product.getDescription());
    intent.putExtra("productLargePrice", product.getLarge());
    intent.putExtra("productSmallPrice", product.getSmall());

    startActivity(intent);
}

    private void toggleMenu() {
        int translationX = homeUI.getWidth() * 3 / 4;
        int newWidth = dpToPx(400);
        int newHeight = dpToPx(700);
        float cornerRadius = getResources().getDimension(R.dimen.corner_radius);
        float targetScale = 0.9f;
        int translationY = (homeUI.getHeight() - newHeight) / 2;

        homeUI.animate()
                .translationX(translationX)
                .translationY(translationY) // Apply translationY animation
                .scaleX(targetScale)  // Apply scaleX animation
                .scaleY(targetScale)  // Apply scaleY animation
                .setDuration(800)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        GradientDrawable background = new GradientDrawable();
                        background.setColor(getResources().getColor(R.color.semi_gray));
                        background.setCornerRadius(cornerRadius);
                        homeUI.setBackground(background);
                    }
                }).start();
    }


    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void saveUserNameToSharedPreferences(String userName) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", userName);
        editor.apply();
        Log.d("SharedPreferences", "Saved UserName: " + userName);
    }


}

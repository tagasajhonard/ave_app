package ordering.app.avenuet_housebongabong;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class splash extends AppCompatActivity {

    private View expandView;
    private TextView avenueTextView;
    private ImageView logoImageView;
    private ImageSliderAdapter sliderAdapter;
    private ViewPager2 viewPager;
    private TextView textPosition;
    private Button btnNext;
    private List<SliderItem> sliderItems = new ArrayList<>();
    private static final String PREF_ONBOARDING_COMPLETED = "onboarding_completed";
    private List<Bitmap> bitmapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);


        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String fullName = sharedPreferences.getString("FullName", null);
        String user = sharedPreferences.getString("username", "");



        btnNext  = findViewById(R.id.btnNext);
        logoImageView = findViewById(R.id.logo);
        avenueTextView = findViewById(R.id.avenueText);
        textPosition = findViewById(R.id.textPosition);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setVisibility(View.INVISIBLE);

        String text1 = "We're delighted to have you here. Get ready to explore our exciting milk tea selections and other delicious treats!";
        String text2 = "Discover a unique milk tea experience with Avenue T House Bongabong. We take pride in offering high-quality beverages crafted with the finest ingredients and flavors.";
        String text3 = "Prepare yourself to browse through a delightful array of milk tea drinks, snacks, and refreshing beverages in our shop. We've got something special waiting just for you!";

        String title1 = "Welcome to Avenue T House Bongabong!";
        String title2 = "Introducing Avenue T House";
        String title3 = "Get Ready to Explore Our Products";

        List<String> titles = new ArrayList<>();
        titles.add(title1);
        titles.add(title2);
        titles.add(title3);

        List<String> descriptions = new ArrayList<>();
        descriptions.add(text1);
        descriptions.add(text2);
        descriptions.add(text3);

        sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.welcome, title1, text1));
        sliderItems.add(new SliderItem(R.drawable.promoting, title2, text2));
        sliderItems.add(new SliderItem(R.drawable.order, title3, text3));

        int[] images = {R.drawable.welcome, R.drawable.promoting, R.drawable.order};

        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, titles, descriptions, images);
        viewPager.setAdapter(sliderAdapter);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sliderItems != null && sliderItems.size() > 0) {
                    int currentItem = viewPager.getCurrentItem();
                    int lastItemIndex = sliderItems.size() - 1;

                    if (currentItem < lastItemIndex) {
                        // Navigate to the next slide
                        viewPager.setCurrentItem(currentItem + 1, true);
                    } else if (currentItem == lastItemIndex) {
                        // Last slide reached, change button text to "Get Started"
                        btnNext.setText("Get Started");

                        // Handle click action when "Get Started" is clicked
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                preferences.edit().putBoolean(PREF_ONBOARDING_COMPLETED, true).apply();

                                // Perform the action to navigate to another page/activity
                                Intent intent = new Intent(splash.this, login.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update text position (1-based index)
                textPosition.setText((position + 1) + " / " + images.length);
            }
        });


        // Scale animation for the logo
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0f, 1f);
        scaleXAnimator.setDuration(1000); // 1 second
        scaleYAnimator.setDuration(1000); // 1 second

        // Translate animation for the Avenue text (from left to position)
        TranslateAnimation avenueTranslateAnimation = new TranslateAnimation(1000f, 0f, 0f, 0f);
        avenueTranslateAnimation.setDuration(1000); // 1 second
        avenueTextView.startAnimation(avenueTranslateAnimation);

        // Start logo scale animation
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();

        expandView = findViewById(R.id.expandView);
//        expandView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                expandWhiteCircle();
//            }
//        }, 2000);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOnboardingCompleted = preferences.getBoolean(PREF_ONBOARDING_COMPLETED, false);

        if (isOnboardingCompleted) {
            // Onboarding is completed, navigate to the login activity or main activity
            expandView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dontexpandWhiteCircle();
                }
            }, 2000);
        } else {
            expandView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandWhiteCircle();
                }
            }, 2000);
        }

    }

    private void dontexpandWhiteCircle() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        if (username.isEmpty()) {
            // If no username is stored, redirect to login directly
            Intent intent = new Intent(splash.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        // Assuming you are using Firebase for user data
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(username);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);

                    if ("blocked".equalsIgnoreCase(status)) {
                        // User is blocked, show a dialog and don't proceed to home
                        new AlertDialog.Builder(splash.this)
                                .setTitle("Blocked User")
                                .setMessage("Your account has been restricted due to violations of our policies. If you believe this is a mistake, please contact support for assistance.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Close the app or redirect to login
                                        Intent intent = new Intent(splash.this, login.class);

                                        clearSharedPreferences();
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        // User is not blocked, proceed with the animation and transition to homeActivity
                        proceedWithAnimation();
                    }
                } else {
                    // User not found, redirect to login
                    Intent intent = new Intent(splash.this, login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void clearSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username");
        editor.apply();

        Log.d("SharedPreferences", "Username preference has been cleared.");
    }

    private void proceedWithAnimation() {
        expandView.setVisibility(View.VISIBLE);

        ConstraintLayout parentLayout = (ConstraintLayout) expandView.getParent();
        int parentWidth = parentLayout.getWidth();
        int parentHeight = parentLayout.getHeight();
        float finalRadius = (float) Math.sqrt(parentWidth * parentWidth + parentHeight * parentHeight) / 2;

        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0f, finalRadius);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                expandView.getLayoutParams().width = (int) (2 * value);
                expandView.getLayoutParams().height = (int) (2 * value);
                expandView.requestLayout();
            }
        });
        radiusAnimator.setDuration(1000); // 1 second
        radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        radiusAnimator.start();
        radiusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator logoScaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 0f);
                ObjectAnimator logoScaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 0f);
                logoScaleXAnimator.setDuration(500);
                logoScaleYAnimator.setDuration(500);

                AnimatorSet logoAnimatorSet = new AnimatorSet();
                logoAnimatorSet.playTogether(logoScaleXAnimator, logoScaleYAnimator);
                logoAnimatorSet.start();

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                String user = sharedPreferences.getString("username", "");

                Intent intent;
                if (user.isEmpty()) {
                    intent = new Intent(splash.this, login.class);
                } else {
                    intent = new Intent(splash.this, homeActivity.class);
                }

                // Set the flags to clear the task and start a new one
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

//    private void dontexpandWhiteCircle() {
//        expandView.setVisibility(View.VISIBLE);
//
//        ConstraintLayout parentLayout = (ConstraintLayout) expandView.getParent();
//        int parentWidth = parentLayout.getWidth();
//        int parentHeight = parentLayout.getHeight();
//        float finalRadius = (float) Math.sqrt(parentWidth * parentWidth + parentHeight * parentHeight) / 2;
//
//        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0f, finalRadius);
//        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                expandView.getLayoutParams().width = (int) (2 * value);
//                expandView.getLayoutParams().height = (int) (2 * value);
//                expandView.requestLayout();
//            }
//        });
//        radiusAnimator.setDuration(1000); // 1 second
//        radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//
//        radiusAnimator.start();
//        radiusAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                ObjectAnimator logoScaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 0f);
//                ObjectAnimator logoScaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 0f);
//                logoScaleXAnimator.setDuration(500);
//                logoScaleYAnimator.setDuration(500);
//
//                AnimatorSet logoAnimatorSet = new AnimatorSet();
//                logoAnimatorSet.playTogether(logoScaleXAnimator, logoScaleYAnimator);
//                logoAnimatorSet.start();
//
//                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//                String user = sharedPreferences.getString("username", "");
//
//                Intent intent;
//                if (user.isEmpty()) {
//                    intent = new Intent(splash.this, login.class);
//                } else {
//                    intent = new Intent(splash.this, homeActivity.class);
//                }
//
//                // Set the flags to clear the task and start a new one
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//
//            }
//        });
//
//    }

    private void expandWhiteCircle() {
        expandView.setVisibility(View.VISIBLE);

        ConstraintLayout parentLayout = (ConstraintLayout) expandView.getParent();
        int parentWidth = parentLayout.getWidth();
        int parentHeight = parentLayout.getHeight();
        float finalRadius = (float) Math.sqrt(parentWidth * parentWidth + parentHeight * parentHeight) / 2;

        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0f, finalRadius);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                expandView.getLayoutParams().width = (int) (2 * value); // Diameter = 2 * radius
                expandView.getLayoutParams().height = (int) (2 * value);
                expandView.requestLayout(); // Request layout update to reflect new size
            }
        });
        radiusAnimator.setDuration(1000); // 1 second
        radiusAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the radius animation
        radiusAnimator.start();
            radiusAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Resize the logoImageView to 0dp width and height
                    ObjectAnimator logoScaleXAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 0f);
                    ObjectAnimator logoScaleYAnimator = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 0f);
                    logoScaleXAnimator.setDuration(500);
                    logoScaleYAnimator.setDuration(500);

                    AnimatorSet logoAnimatorSet = new AnimatorSet();
                    logoAnimatorSet.playTogether(logoScaleXAnimator, logoScaleYAnimator);
                    logoAnimatorSet.start();


                    // Move avenueTextView to the top of the screen
                    moveTextViewToTopOfScreen(avenueTextView);

                    viewPager.setVisibility(View.VISIBLE); // Ensure ViewPager is visible before animation

                    // Calculate the center position of the screen
                    int centerX = parentLayout.getWidth() / 2;
                    int centerY = parentLayout.getHeight() / 2;

                    // Animate the ViewPager to scale up and move to the center
                    viewPager.setScaleX(0f);
                    viewPager.setScaleY(0f);
                    viewPager.setX(centerX - viewPager.getWidth() / 2);
                    viewPager.setY(centerY - viewPager.getHeight() / 2);

                    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(viewPager, "scaleX", 0f, 1f);
                    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(viewPager, "scaleY", 0f, 1f);
                    ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(viewPager, "translationX", 0f);
                    ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(viewPager, "translationY", 0f);

                    scaleXAnimator.setDuration(1000); // 1 second
                    scaleYAnimator.setDuration(1000); // 1 second
                    translateXAnimator.setDuration(1000); // 1 second
                    translateYAnimator.setDuration(1000); // 1 second

                    AnimatorSet viewPagerAnimatorSet = new AnimatorSet();
                    viewPagerAnimatorSet.playTogether(scaleXAnimator, scaleYAnimator, translateXAnimator, translateYAnimator);
                    viewPagerAnimatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                    viewPagerAnimatorSet.start();
                }
            });

    }

    private void moveTextViewToTopOfScreen(TextView textView) {
        int topMarginDP = 100; // Desired margin in dp
        float density = getResources().getDisplayMetrics().density;
        int topMarginPixels = (int) (topMarginDP * density + 0.5f); // Convert dp to pixels

        // Animate the textView to move to the top of the screen with the specified margin
        int[] location = new int[2];
        textView.getLocationOnScreen(location);
        int currentY = location[1]; // Current Y position of textView on screen

        int targetY = topMarginPixels - currentY; // Calculate the Y translation

        ObjectAnimator moveUpAnimator = ObjectAnimator.ofFloat(textView, "translationY", 0, targetY);
        moveUpAnimator.setDuration(1000); // 1 second
        moveUpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        moveUpAnimator.start();

        ConstraintLayout parentLayout = (ConstraintLayout) expandView.getParent();
        int centerX = parentLayout.getWidth() / 2;
        int targetX = centerX - viewPager.getWidth() / 2; // X position where ViewPager2 ends

        // Get the current X position of the button
        float currentX = btnNext.getX();

        // Animate the button to slide from off-screen to its target position
        ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(btnNext, "translationX", parentLayout.getWidth(), targetX);
        translateXAnimator.setDuration(1000); // 1 second
        translateXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translateXAnimator.start();

        btnNext.setVisibility(View.VISIBLE);
    }

}


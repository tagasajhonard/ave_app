package ordering.app.avenuet_housebongabong;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText editTextmobile, editTextVerification;
    private Button buttonVerifyOTP;

    private String verificationId;

    private boolean otpsent = false;
    private String countryCode = "+63";

    private String id ="";

    private TextView phonebtn;
    CardView btn;
    private RelativeLayout mainLayout;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btn = findViewById(R.id.layoutFB);

//        ImageView imageView = findViewById(R.id.imgID);
//        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//        imageView.setLayoutParams(layoutParams);
//
//        imageView.setScaleType(ImageView.ScaleType.FIT_START);

        mainLayout = findViewById(R.id.main);

        animateMainLayoutExpansion();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FacebookAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        phonebtn = findViewById(R.id.loginMobile);

        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, phone_login.class);
                Intent intent = new Intent(MainActivity.this, profile.class);
                startActivity(intent);
                finish();
            }
        });

//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
////            redirectToHome(currentUser);
//        } else {
//            suggestLogin();
//        }


    }
    private void animateMainLayoutExpansion() {
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // Start the animation on the main layout
        mainLayout.startAnimation(scaleAnimation);
    }

//    private void verifyOTP(String otp) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
//        signInWithPhoneAuthCredential(credential);
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        FirebaseAuth.getInstance().signInWithCredential(credential)
//                .addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            // Verification successful, proceed with your app logic
//                            Toast.makeText(MainActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
//                            // TODO: Proceed with authenticated user
//                        } else {
//                            // Verification failed
//                            Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    // Example function to trigger OTP verification (e.g., when button is clicked)
//    private void redirectToHome(FirebaseUser user) {
//        if (user != null) {
//            String userName = user.getDisplayName(); // Get user's display name
//            String profilePicUrl = user.getPhotoUrl().toString(); // Get user's profile picture URL
//
//            // Now you can pass both the name and profile picture URL to the home activity
//            Intent intent = new Intent(MainActivity.this, homeActivity.class);
//            intent.putExtra("userName", userName);
//            intent.putExtra("profilePicUrl", profilePicUrl);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//    // Method to suggest user to login
//    private void suggestLogin() {
//        // You can show a message or prompt the user to log in
//        Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
//
//        // Example: Redirect to FacebookAuthActivity for login
//        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, FacebookAuthActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
}
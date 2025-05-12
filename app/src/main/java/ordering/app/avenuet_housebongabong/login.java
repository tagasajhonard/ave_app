package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView confirmButton, forgot;
    private TextView signin;
    private DatabaseReference databaseReference;
    private View popupView;
    private Button fastLoginButton;




    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signin = findViewById(R.id.signin);

        usernameEditText = findViewById(R.id.txtUsername);
        passwordEditText = findViewById(R.id.txtPass);
        confirmButton = findViewById(R.id.confirm);
        forgot = findViewById(R.id.forgot);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,MainActivity.class);
                startActivity(intent);
            }
        });
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Username is required");
                } else if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Password is required");
                } else {
                    if (isNetworkAvailable()) {
                        validateLogin(username, password);
                    } else {
                        Toast.makeText(login.this, "No internet connection. Please check your data or Wi-Fi.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
    private void validateLogin(final String username, final String password) {
        showloading(confirmButton);
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                popupWindow.dismiss();
                if (snapshot.exists()) {

                    String status = snapshot.child("status").getValue(String.class);

                    if ("blocked".equalsIgnoreCase(status)) {
                        // User is blocked, show a dialog and don't proceed to home
                        new AlertDialog.Builder(login.this)
                                .setTitle("Blocked User")
                                .setMessage("Your account has been restricted for policy violations. If this is an error, please contact support or log in with a different account.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Close the app or redirect to login
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } else {
                        // User is not blocked, proceed with the animation and transition to homeActivity
                        String storedPassword = snapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Password matches, get full name and navigate to HomeActivity
                            String fullName = snapshot.child("fullName").getValue(String.class);

                            // Save the username in SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", username);
                            editor.putString("fullName", fullName); // save the full name as well
                            editor.apply();

                            Intent intent = new Intent(login.this, homeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Password does not match
                            passwordEditText.setError("Incorrect password");
                        }
                    }


                } else {
                    // Username does not exist
                    usernameEditText.setError("Username does not exist");
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                popupWindow.dismiss();
                Toast.makeText(login.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private PopupWindow popupWindow;

    private void showloading(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.loading, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
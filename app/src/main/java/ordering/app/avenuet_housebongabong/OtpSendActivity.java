package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpSendActivity extends AppCompatActivity {

    private EditText otpField;
    private Button btnVerify;
    private String phoneNumber;
    private String storedVerificationId;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_send);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        otpField = findViewById(R.id.otpField);  // Make sure your layout has this ID
        btnVerify = findViewById(R.id.btnVerify);


        otpField.setVisibility(View.GONE);
        btnVerify.setVisibility(View.GONE);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        phoneNumber = getIntent().getStringExtra("phone"); // Getting passed phone number
        Log.d("OtpSendActivity", "Phone number to verify: " + phoneNumber);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Optional: you can auto-sign in the user here
                Toast.makeText(OtpSendActivity.this, "Auto verification complete!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OtpSendActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                storedVerificationId = verificationId;
                otpField.setVisibility(View.VISIBLE);
                btnVerify.setVisibility(View.VISIBLE);
                Toast.makeText(OtpSendActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber("+63" + phoneNumber)
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        btnVerify.setOnClickListener(v -> {
            String code = otpField.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(OtpSendActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(storedVerificationId, code);
            signInWithPhoneAuthCredential(credential);

        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String username = getIntent().getStringExtra("username");
                        Toast.makeText(OtpSendActivity.this, "OTP Verified!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ResetPass.class);

                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(OtpSendActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

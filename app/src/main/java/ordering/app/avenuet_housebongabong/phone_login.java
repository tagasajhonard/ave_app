package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class phone_login extends MainActivity {
    private EditText editTextmobile, editTextVerification;
    private Button buttonVerifyOTP, verify;

    private boolean otpsent = false;
    private String countryCode = "+63";

    private String id ="";

    private EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6;

    private LinearLayout otpPopup;

    private FrameLayout frameforverify;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextmobile = findViewById(R.id.editTextOTP);
        buttonVerifyOTP = findViewById(R.id.buttonVerifyOTP);
        verify = findViewById(R.id.verifyCode);

        frameforverify = findViewById(R.id.frameforverify);
        otpPopup = findViewById(R.id.otpPopup);

        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

//        FirebaseApp.initializeApp(/*context=*/ this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getMobile = editTextmobile.getText().toString();
                if (getMobile.isEmpty()) {
                    Toast.makeText(phone_login.this, "Please enter a mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(countryCode + getMobile)
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(phone_login.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Toast.makeText(phone_login.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(phone_login.this, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                otpPopup.setVisibility(View.VISIBLE);
                                buttonVerifyOTP.setText("Verification Code Sent");
                                id = s;
                                otpsent = true;
                                showOtpPopup();

                                frameforverify.setVisibility(View.VISIBLE);

                            }
                        }).build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otpsent) {
                    final String getOtp = getOtp();

                    if (id.isEmpty()) {
                        Toast.makeText(phone_login.this, "Unable to verify OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, getOtp);
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            String otp = editTextmobile.getText().toString().trim();
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser userDetails = task.getResult().getUser();
                                    Intent intent = new Intent(phone_login.this, profile.class);
                                    intent.putExtra("otp", otp);
                                    intent.putExtra("source", "mobile");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    Toast.makeText(phone_login.this, "Verified", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(phone_login.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        otpDigit1 = findViewById(R.id.otp_digit_1);
        otpDigit2 = findViewById(R.id.otp_digit_2);
        otpDigit3 = findViewById(R.id.otp_digit_3);
        otpDigit4 = findViewById(R.id.otp_digit_4);
        otpDigit5 = findViewById(R.id.otp_digit_5);
        otpDigit6 = findViewById(R.id.otp_digit_6);

        setupOtpInputs();

    }

    private void setupOtpInputs() {
        otpDigit1.addTextChangedListener(new GenericTextWatcher(otpDigit1));
        otpDigit2.addTextChangedListener(new GenericTextWatcher(otpDigit2));
        otpDigit3.addTextChangedListener(new GenericTextWatcher(otpDigit3));
        otpDigit4.addTextChangedListener(new GenericTextWatcher(otpDigit4));
        otpDigit5.addTextChangedListener(new GenericTextWatcher(otpDigit5));
        otpDigit6.addTextChangedListener(new GenericTextWatcher(otpDigit6));
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;

        public GenericTextWatcher(EditText currentView) {
            this.currentView = currentView;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }


        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() == 1) {
                if (currentView.getId() == R.id.otp_digit_1) {
                    otpDigit2.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_2) {
                    otpDigit3.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_3) {
                    otpDigit4.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_4) {
                    otpDigit5.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_5) {
                    otpDigit6.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_6) {
                    // OTP input is complete
                }
            } else if (editable.length() == 0) {
                if (currentView.getId() == R.id.otp_digit_2) {
                    otpDigit1.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_3) {
                    otpDigit2.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_4) {
                    otpDigit3.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_5) {
                    otpDigit4.requestFocus();
                } else if (currentView.getId() == R.id.otp_digit_6) {
                    otpDigit5.requestFocus();
                }
            }
        }
    }
    private String getOtp() {
        return otpDigit1.getText().toString() +
                otpDigit2.getText().toString() +
                otpDigit3.getText().toString() +
                otpDigit4.getText().toString() +
                otpDigit5.getText().toString() +
                otpDigit6.getText().toString();
    }
    private void showOtpPopup() {
        otpPopup.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        otpPopup.startAnimation(slideUp);
    }
}
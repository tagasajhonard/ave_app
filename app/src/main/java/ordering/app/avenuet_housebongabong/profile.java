package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class profile extends AppCompatActivity {


    private TextInputEditText txtFname, txtUsername, txtPhone, streetView, txtPass, txtConPass;
    private TextView confirmBtn;
    private AutoCompleteTextView spinnerTown, spinnerStreet;
    private DatabaseReference databaseReference;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        txtFname = findViewById(R.id.txtFname);
        txtUsername = findViewById(R.id.txtUsername);
        txtPhone = findViewById(R.id.txtPhone);
        spinnerTown = findViewById(R.id.spinnerTown);
        spinnerStreet = findViewById(R.id.spinnerStreet);
        streetView = findViewById(R.id.streetView);
        txtPass = findViewById(R.id.txtPass);
        txtConPass = findViewById(R.id.txtConPass);
        confirmBtn = findViewById(R.id.confirmBtn);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        AutoCompleteTextView spinnerTown = findViewById(R.id.spinnerTown);
        AutoCompleteTextView spinnerStreet = findViewById(R.id.spinnerStreet);

        List<String> towns = Arrays.asList(getResources().getStringArray(R.array.spinner_items));
        dropdownAdapter townAdapter = new dropdownAdapter(this, android.R.layout.simple_list_item_1, towns);
        spinnerTown.setAdapter(townAdapter);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String mobileNumber = intent.getStringExtra("otp");


        String source = intent.getStringExtra("source");

        // Check which activity the user came from and execute the corresponding block of code
        if ("facebook".equals(source)) {
            txtFname.setText(userName);
        } else if ("mobile".equals(source)) {
            txtPhone.setText(mobileNumber);
        }



        spinnerTown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTown = (String) parent.getItemAtPosition(position);
                List<String> streets = new ArrayList<>();

                switch (selectedTown) {
                    case "Bansud":
                        streets = Arrays.asList(getResources().getStringArray(R.array.spinner_street_bansud));
                        break;
                    case "Bongabong":
                        streets = Arrays.asList(getResources().getStringArray(R.array.spinner_street_bongabong));
                        break;
                    case "Roxas":
                        streets = Arrays.asList(getResources().getStringArray(R.array.spinner_street_roxas));
                        break;
                }

                dropdownAdapter streetAdapter = new dropdownAdapter(profile.this, android.R.layout.simple_list_item_1, streets);
                spinnerStreet.setAdapter(streetAdapter);
            }
        });

        txtFname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();

                // Regex pattern to allow only letters and spaces
                String regex = "^[a-zA-Z\\s]+$";

                // Check if the input contains anything other than letters and spaces
                if (!input.matches(regex)) {
                    txtFname.setError("Full Name must contain only letters and spaces.");
                } else {
                    txtFname.setError(null); // Clear error if input is valid
                }
            }
        });


        txtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                // Regex pattern to check for at least one uppercase letter, one number, and one special character
                String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{1,}$";

                // Check if the input contains invalid characters for Firebase keys
                String restrictedChars = "[.#$\\[\\]]";
                if (input.matches(".*" + restrictedChars + ".*")) {
                    txtUsername.setError("Username cannot contain ., #, $, [ or ].");
                    return;
                }

                // Check if the input matches the regex pattern
                if (!input.matches(regex)) {
                    txtUsername.setError("Username must contain at least one uppercase letter, one number, and one special character.");
                } else {
                    txtUsername.setError(null); // Clear the error if input is valid
                }
            }
        });

        txtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                // Regex pattern to check for at least one uppercase letter, one lowercase letter,
                // one digit, one special character, and alphanumeric characters
                String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$";

                // Check if the input matches the regex pattern
                if (!input.matches(regex)) {
                    txtPass.setError("Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long.");
                } else {
                    txtPass.setError(null); // Clear the error if input is valid
                }
            }
        });

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the first character is '0'
                if (s.length() > 0 && s.charAt(0) == '0') {
                    // Remove the first character if it's '0'
                    txtPhone.setText(s.toString().substring(1));
                    txtPhone.setSelection(txtPhone.length()); // Move cursor to the end
                }
            }
        });



    }
    private void saveData() {
        // Retrieve inputted data
        String fullName = txtFname.getText().toString().trim();
        String username = txtUsername.getText().toString().trim(); // Get the username
        String sanitizedUsername = sanitizeUsername(username); // Sanitize the username
        String phoneNumber = txtPhone.getText().toString().trim();
        String town = spinnerTown.getText().toString().trim();
        String barangay = spinnerStreet.getText().toString().trim();
        String street = streetView.getText().toString().trim();
        String password = txtPass.getText().toString().trim();
        String confirmPassword = txtConPass.getText().toString().trim();

        // Validate inputs
        String fullNameRegex = "^[a-zA-Z\\s]+$";
        if (fullName.isEmpty() || !fullName.matches(fullNameRegex)) {
            txtFname.setError("Full Name must contain only letters and spaces.");
            txtFname.requestFocus();  // Ensure it gets focused
            return;
        } else {
            txtFname.setError(null);  // Clear error
        }
        // Validate Username
        String restrictedChars = "[.#$\\[\\]]";
        if (username.isEmpty()) {
            txtUsername.setError("Username is required");
            txtUsername.requestFocus();
            return;
        } else if (username.matches(".*" + restrictedChars + ".*")) {
            txtUsername.setError("Username cannot contain '.', '#', '$', '[', or ']'.");
            txtUsername.requestFocus();
            return;
        } else if (sanitizedUsername.isEmpty()) {
            txtUsername.setError("Username cannot be empty after sanitization.");
            txtUsername.requestFocus();
            return;
        } else {
            txtUsername.setError(null);  // Clear error
        }

        // Validate Phone Number
        if (phoneNumber.isEmpty()) {
            txtPhone.setError("Phone Number is required");
            txtPhone.requestFocus();
            return;
        } else if (phoneNumber.length() < 10) {
            txtPhone.setError("Phone Number must be at least 10 digits.");
            txtPhone.requestFocus();
            return;
        } else {
            txtPhone.setError(null);  // Clear error
        }

        // Validate Town
        if (town.isEmpty()) {
            spinnerTown.setError("Town is required");
            spinnerTown.requestFocus();
            return;
        }else {
            spinnerTown.setError(null);  // Clear error
        }

        // Validate Barangay/Street
        if (barangay.isEmpty()) {
            spinnerStreet.setError("Street is required");
            spinnerStreet.requestFocus();
            return;
        } else {
            spinnerStreet.setError(null);  // Clear error
        }

        if (spinnerTown.getText().toString().trim().equals("Town") &&
                spinnerStreet.getText().toString().trim().equals("Street")) {

            spinnerStreet.setError("Please select a valid town and street");
            spinnerStreet.requestFocus();
            return;
        } else {
            spinnerStreet.setError(null);  // Clear error
        }


        if (street.isEmpty()) {
            streetView.setError("Sitio/Street is required");
            streetView.requestFocus();
            return;
        } else {
            streetView.setError(null);  // Clear error
        }

        if (password.isEmpty()) {
            txtPass.setError("Password is required");
            txtPass.requestFocus();
            return;
        } else {
            txtPass.setError(null);  // Clear error
        }

        if (confirmPassword.isEmpty()) {
            txtConPass.setError("Confirm your password ");
            txtConPass.requestFocus();
            return;
        } else {
            txtConPass.setError(null);  // Clear error
        }


        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            txtConPass.setError("Password do not match");
            return;
        }


        String createdAt = String.valueOf(System.currentTimeMillis());
        // Create a user object
        User user = new User(fullName, sanitizedUsername, phoneNumber, town, barangay, street, password, createdAt);

        // Save user to Firebase Realtime Database
        databaseReference.child(sanitizedUsername).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(profile.this, login.class);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", sanitizedUsername);
                editor.apply();

                Toast.makeText(profile.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(profile.this, "Failed to create account. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public static class User {
//        public String fullName, username, phoneNumber, town, street, sitioStreet, password;
//
//        public User() {
//            // Default constructor required for calls to DataSnapshot.getValue(User.class)
//        }
//
//        public User(String fullName, String username, String phoneNumber, String town, String street, String sitioStreet, String password) {
//            this.fullName = fullName;
//            this.username = username;
//            this.phoneNumber = phoneNumber;
//            this.town = town;
//            this.street = street;
//            this.sitioStreet = sitioStreet;
//            this.password = password;
//        }
//    }

    public static class User {
        public String fullName, username, phoneNumber, town, street, sitioStreet, password, createdAt;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String fullName, String username, String phoneNumber, String town, String street, String sitioStreet, String password, String createdAt) {
            this.fullName = fullName;
            this.username = username;
            this.phoneNumber = phoneNumber;
            this.town = town;
            this.street = street;
            this.sitioStreet = sitioStreet;
            this.password = password;
            this.createdAt = createdAt; // Set the createdAt field
        }
    }

    private String sanitizeUsername(String username) {
        // Replace invalid characters with URL-encoded values
        return username.replaceAll("\\.", "%2E")
                .replaceAll("#", "%23")
                .replaceAll("\\$", "%24")
                .replaceAll("\\[", "%5B")
                .replaceAll("\\]", "%5D");
    }



}

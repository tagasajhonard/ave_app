package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private EditText etFullName, etAddress, etPassword;
    private String currentPhotoPath;
    private ImageView imageView; // Reference to the ImageView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        etFullName = findViewById(R.id.etFullName);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        imageView = findViewById(R.id.imageView); // Initialize ImageView

        Button takePhotoButton = findViewById(R.id.btnTakePhoto);
        takePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());

        Button saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(v -> onSaveClick());



    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file: " + ex.getMessage());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ordering.app.avenuet_housebongabong.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Display the captured image in the ImageView
            setCapturedImage();
        }
    }

    private void setCapturedImage() {
        // Load the captured image into the ImageView
        if (currentPhotoPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE); // Show the ImageView
            } else {
                Log.e(TAG, "Failed to load image.");
            }
        } else {
            Log.e(TAG, "Current photo path is null.");
        }
    }

    private void onSaveClick() {
        uploadUserDataWithImage();
    }
    private void saveUsernameToSharedPreferences(String username) {
        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the username to SharedPreferences
        editor.putString("userName", username);
        editor.apply(); // Apply changes asynchronously

        Log.d(TAG, "Username saved to SharedPreferences: " + username);
    }
    private void uploadUserDataWithImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String fullName = etFullName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Assuming the image URL is retrieved from Firebase Storage (you've handled this in onActivityResult)
            String imageUrl = "https://example.com/images/example.jpg"; // Replace with actual image URL

            saveUserDataToDatabase(fullName, address, password, imageUrl);

            saveUsernameToSharedPreferences(fullName);

        }
    }

    private void saveUserDataToDatabase(String fullName, String address, String password, String imageUrl) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = usersRef.child(userId);

            // Set user data in the database
            userRef.child("fullName").setValue(fullName);
            userRef.child("address").setValue(address);
            userRef.child("password").setValue(password);
            userRef.child("imageUrl").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User data saved to database.");
                        navigateToMainActivity(); // Navigate to main activity after data is saved
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving user data: " + e.getMessage());
                        // Handle failure to save user data
                    });
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the sign-in activity
    }
}

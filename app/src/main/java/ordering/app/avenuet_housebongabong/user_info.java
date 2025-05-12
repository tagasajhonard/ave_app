package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class user_info extends AppCompatActivity {
    private TextView userName, userNumber, userUsername, userEmail, userTown, userBrgy, user_street;
    private ImageView userProfileImage, toggleEdit;
    private View popupView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);
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

        userName = findViewById(R.id.user_name);
        userNumber = findViewById(R.id.user_number);
        userUsername = findViewById(R.id.user_username);
        userEmail = findViewById(R.id.user_email);
        userTown = findViewById(R.id.user_town);
        userBrgy = findViewById(R.id.user_brgy);
        user_street = findViewById(R.id.user_street);
        userProfileImage = findViewById(R.id.user_profile);
        TextView createdAt = findViewById(R.id.createdAt);
        TextView totalOrdersTextView = findViewById(R.id.totalOrder);
        TextView totalSpentTextView = findViewById(R.id.totalSpent);

        toggleEdit = findViewById(R.id.toggle_edit);
        toggleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPopup(v);
            }
        });


        TextView favoriteCountTextView = findViewById(R.id.favorite_count);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String savedUsername = sharedPreferences.getString("fullName", "");

        if (!username.isEmpty()) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        String phone = snapshot.child("phoneNumber").getValue(String.class);
                        String brgy = snapshot.child("sitioStreet").getValue(String.class);
                        String street = snapshot.child("street").getValue(String.class);
                        String town = snapshot.child("town").getValue(String.class);
                        String username = snapshot.child("username").getValue(String.class);
                        String userImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        String createdAtString = snapshot.child("createdAt").getValue(String.class);
                        Long createdAtMillis = null;

                        if (createdAtString != null) {
                            try {
                                createdAtMillis = Long.parseLong(createdAtString);
                            } catch (NumberFormatException e) {
                                e.printStackTrace(); // Handle the exception
                            }
                        }

                        userName.setText(fullName != null ? fullName : "N/A");
                        userNumber.setText(phone != null ? "(+63) " + phone : "N/A");
                        userUsername.setText(username != null ? username : "N/A");
                        userEmail.setText("N/A");
                        userTown.setText(town != null ? town : "N/A");
                        userBrgy.setText(brgy != null ? brgy : "N/A");
                        user_street.setText(street != null ? street : "N/A");

                        if (createdAtMillis != null) {
                            String formattedDate = formatTimestamp(createdAtMillis);
                            createdAt.setText(formattedDate);
                        } else {
                            createdAt.setText("N/A");
                        }

                        if (userImageUrl != null && !userImageUrl.isEmpty()) {
                            Glide.with(user_info.this)
                                    .load(userImageUrl)
                                    .placeholder(R.drawable.placeholder) // Your placeholder image
                                    .error(R.drawable.placeholder) // Fallback in case of an error
                                    .centerCrop()
                                    .into(userProfileImage);
                        } else {
                            // Load the placeholder image if no URL is found
                            Glide.with(user_info.this)
                                    .load(R.drawable.placeholder) // Load placeholder directly
                                    .centerCrop()
                                    .into(userProfileImage);
                        }

                    } else {
                        userName.setText("N/A");
                        userNumber.setText("N/A");
                        userUsername.setText("N/A");
                        userEmail.setText("N/A");
                        userTown.setText("N/A");
                        userBrgy.setText("N/A");
                        user_street.setText("N/A");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle possible errors.
                }
            });
        } else {
            userName.setText("No username saved");
        }


        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites");


        DatabaseReference userFavoritesRef = favoritesRef.child(savedUsername);

// Retrieve the list of favorites for this user
        userFavoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Count the number of favorites
                    long favoriteCount = dataSnapshot.getChildrenCount();
                    favoriteCountTextView.setText(String.valueOf(favoriteCount));
                    // Display the count (or use it as needed)

                } else {
                    Log.d("Favorites Count", "No favorites found for " + username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
                Log.e("FirebaseError", "Error retrieving favorites: " + databaseError.getMessage());
            }
        });

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        if (!savedUsername.isEmpty()) {
            ordersRef.child(savedUsername).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int totalItems = 0;
                    double totalSpent = 0;

                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        String orderStatus = orderSnapshot.child("status").getValue(String.class);

                        if ("delivered".equalsIgnoreCase(orderStatus)) {
                            DataSnapshot itemsSnapshot = orderSnapshot.child("items");

                            for (DataSnapshot item : itemsSnapshot.getChildren()) {
                                Long quantity = item.child("quantity").getValue(Long.class);
                                Long itemTotal = item.child("itemTotal").getValue(Long.class); // assuming itemTotal is stored as Long

                                if (quantity != null) {
                                    totalItems += quantity;
                                }
                                if (itemTotal != null) {
                                    totalSpent += itemTotal;
                                }
                            }
                        }
                    }

                    totalOrdersTextView.setText(String.valueOf(totalItems));
                    totalSpentTextView.setText("₱" + String.format("%.2f", totalSpent));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }






    }

    private String formatTimestamp(long timestamp) {
        // Create a Date object from the timestamp
        Date date = new Date(timestamp);

        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image picking
    private Uri imageUri; // Variable to store the image URI

    private void showEditPopup(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.popup_edit_text, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

        // Get references to the views
        ImageView imageView = popupView.findViewById(R.id.popup_image_view);
        Button uploadButton = popupView.findViewById(R.id.popup_upload_button);

        Button confirmButton = popupView.findViewById(R.id.popup_confirm_button);
        Button cancelButton = popupView.findViewById(R.id.popup_cancel_button);

        // Handle upload button click
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open gallery to pick an image
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Handle confirm button click
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadImageToFirebase(imageUri); // Call your upload function
                }
                popupWindow.dismiss(); // Dismiss the popup
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss(); // Dismiss the popup when Cancel is clicked
            }
        });

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    // Handle the result from the image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Display the selected image in the ImageView
            ImageView imageView = popupView.findViewById(R.id.popup_image_view);
            imageView.setImageURI(imageUri); // Set the image URI
        }
    }

    // Function to upload image to Firebase Storage
    private void uploadImageToFirebase(Uri uri) {
        // Assuming you have initialized Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages");
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Save the download URL to your user data in Firebase
                                String profileImageUrl = downloadUri.toString();
                                // Update user data with profileImageUrl
                                updateUserProfileImage(profileImageUrl);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(user_info.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to update user profile image in Firebase
    private void updateUserProfileImage(String profileImageUrl) {

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String savedUsername = sharedPreferences.getString("username", "");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(savedUsername);
        userRef.child("profileImageUrl").setValue(profileImageUrl);
    }


}
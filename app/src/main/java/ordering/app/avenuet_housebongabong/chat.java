package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressLint("MissingInflatedId")
public class chat extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private Button sendButton;
    private DatabaseReference messageDbRef;
    private String currentUser = "User";
    private List<Object> items;
    private ImageView selectedImage;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        Button clipButton = findViewById(R.id.clip);
        selectedImage = findViewById(R.id.selected_image);

        clipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        String userName = getIntent().getStringExtra("fullName");
        currentUser = userName;


        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        messageDbRef = FirebaseDatabase.getInstance().getReference("messages");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the image URI
            selectedImage.setImageURI(imageUri); // Display the selected image
            selectedImage.setVisibility(View.VISIBLE); // Show the ImageView
            messageInput.setText(""); // Clear the message input
        }
    }
    private void loadMessages() {
        messageDbRef.child(currentUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);

                if (message != null) {
                    messageList.add(message);  // Add new message to the list
                    messageAdapter.notifyItemInserted(messageList.size() - 1);  // Notify the adapter of the new item
                    recyclerView.scrollToPosition(messageList.size() - 1);  // Scroll to the new message
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle message updates here if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Handle message deletions here if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Handle message moves if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabaseError", "Error while reading messages", error.toException());
            }
        });
    }

//    private void loadMessages() {
//        messageDbRef.child(currentUser).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                messageList.clear();
//
//                // Iterate over each child node under the currentUser node
//                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
//                    // Get the message object
//                    Message message = messageSnapshot.getValue(Message.class);
//
//                    // Check if the message is not null
//                    if (message != null) {
//                        // Add the message to the messageList
//                        messageList.add(message);
//                    }
//                }
//
//                // Sort messages by timestamp
//                Collections.sort(messageList, new Comparator<Message>() {
//                    @Override
//                    public int compare(Message m1, Message m2) {
//                        return Long.compare(m1.getTimestamp(), m2.getTimestamp());
//                    }
//                });
//
//                // Notify the adapter of the data change
//                messageAdapter.notifyDataSetChanged();
//
//                // Scroll to the last position
//                recyclerView.scrollToPosition(messageList.size() - 1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseDatabaseError", "Error while reading messages", error.toException());
//            }
//        });
//    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText) || imageUri != null) {
            String sender = currentUser;
            long timestamp = System.currentTimeMillis();
            String formattedTime = formatTime(timestamp);

            // Create a message object depending on whether there's a text or image
            if (imageUri != null) {
                // Upload the image to Firebase Storage
                uploadImageToFirebase(imageUri, sender, formattedTime, timestamp);
                // Clear the image URI after sending
                imageUri = null;
                selectedImage.setVisibility(View.GONE); // Hide the ImageView
            } else {
                // Send text message
                Message message = new Message(messageText, sender, timestamp, formattedTime);
                DatabaseReference currentUserRef = messageDbRef.child(currentUser);
                currentUserRef.push().setValue(message);
                messageInput.setText(""); // Clear text input after sending
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri, String sender, String formattedTime, long timestamp) {
        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads/" + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString(); // Get the download URL
                            Message message = new Message(imageUrl, sender, timestamp, formattedTime); // Create message with image URL
                            DatabaseReference currentUserRef = messageDbRef.child(currentUser);
                            currentUserRef.push().setValue(message); // Push message to Firebase
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle failures
                        Log.e("FirebaseUploadError", "Failed to upload image", e);
                    });
        }
    }



    private String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.getDefault());
        return dateFormat.format(new Date(timeInMillis));
    }
}

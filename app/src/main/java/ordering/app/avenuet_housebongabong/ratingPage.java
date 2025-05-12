package ordering.app.avenuet_housebongabong;

import static java.security.AccessController.getContext;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ratingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rating_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("fullName", "");

        Log.d("MyAppTag", "hoyyyyyy ito yungggg Saved fullname: " + username);

        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("Ratings").child(username);
        ratingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Rating> ratings = new ArrayList<>();

                for (DataSnapshot ratingSnapshot : snapshot.getChildren()) {
                    Rating rating = ratingSnapshot.getValue(Rating.class);
                    ratings.add(rating);
                }

                Collections.sort(ratings, (r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()));

                RatingsAdapter ratingsAdapter = new RatingsAdapter(ratingPage.this, ratings);
                RecyclerView ratedItemRecyclerView = findViewById(R.id.ratedItem);
                ratedItemRecyclerView.setLayoutManager(new LinearLayoutManager(ratingPage.this));
                ratedItemRecyclerView.setAdapter(ratingsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DatabaseError", "onCancelled: " + error.getMessage());
            }
        });

    }
}
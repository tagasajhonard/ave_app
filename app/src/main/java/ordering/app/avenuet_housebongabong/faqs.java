package ordering.app.avenuet_housebongabong;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class faqs extends AppCompatActivity {


    private RecyclerView faqRecyclerView;
    private FaqAdapter faqAdapter;
    private List<FaqItem> faqList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faqs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        faqRecyclerView = findViewById(R.id.faq_recycler_view);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load FAQ data
        faqList = new ArrayList<>();
        faqList.add(new FaqItem("How do I place an order?",
                "To place an order, download the mobile app, log in with your account, \n" +
                        "select the desired products, and complete the checkout process."));
        faqList.add(new FaqItem("Can I see if a product is available before ordering?",
                "Yes, the system displays the real-time availability of products, \n" +
                "ensuring that you only order items that are currently in stock."));
        faqList.add(new FaqItem("What payment methods are accepted?", "The system accepts cash, GCash, and cash on delivery (COD) as payment options."));

        faqList.add(new FaqItem("Will I receive a confirmation after placing my order?",
                "Yes, after successfully placing an order, you will receive an order confirmation with the estimated preparation time."));

        faqList.add(new FaqItem("Can I track the status of my order?",
                "Yes, you can track the status of your order from preparation to delivery in real-time through the app."));

        faqList.add(new FaqItem("Does the system track inventory?",
                "No, the system does not have an inventory tracking feature but monitors product availability and order volume in real-time."));

        faqList.add(new FaqItem("How can I provide feedback about my experience?",
                "You can provide feedback directly through the app after your order is complete."));

        faqList.add(new FaqItem("Can I save my favorite orders for future use? ",
                "Yes, the system allows you to save favorite orders for quick and easy reordering in the future."));

        faqList.add(new FaqItem("Can I order without creating an account? ",
                "No, you must create an account to place an order, as it helps track your order status and ensures proper delivery and communication."));
        // Set adapter
        faqAdapter = new FaqAdapter(faqList);
        faqRecyclerView.setAdapter(faqAdapter);
    }
}
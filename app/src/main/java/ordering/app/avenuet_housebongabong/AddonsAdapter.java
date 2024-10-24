package ordering.app.avenuet_housebongabong;

import android.annotation.SuppressLint;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

public class AddonsAdapter extends RecyclerView.Adapter<AddonsAdapter.AddonsViewHolder> {

    private List<AddonItem> addonsList;


    public AddonsAdapter(List<AddonItem> addonsList) {
        this.addonsList = addonsList;
    }

    @NonNull
    @Override
    public AddonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_addon, parent, false);
        return new AddonsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddonsViewHolder holder, int position) {
        AddonItem addon = addonsList.get(position);
        holder.addonName.setText(addon.getName());
        holder.addonPrice.setText(String.format("₱%.2f", addon.getRegular()));

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(addon.getImageUrl())
                .into(holder.addOnImg);

        holder.quantity.setText(String.valueOf(addon.getSelectedQuantity())); // Show current quantity

        holder.incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.number < 9) {
                    holder.number++;
                    addon.setSelectedQuantity(holder.number); // Update selected quantity
                    holder.quantity.setText(String.valueOf(holder.number));
                }
            }
        });

        holder.decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.number > 0) {
                    holder.number--;
                    addon.setSelectedQuantity(holder.number); // Update selected quantity
                    holder.quantity.setText(String.valueOf(holder.number));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return addonsList.size();
    }

    public class AddonsViewHolder extends RecyclerView.ViewHolder {
        public TextView addonName, addonPrice;
        public ImageView addOnImg;

        TextView quantity, incrementButton, decrementButton;

        int number = 0;

        public AddonsViewHolder(View view) {
            super(view);
            addonName = view.findViewById(R.id.addonName);
            addOnImg = view.findViewById(R.id.addOnImg);
            addonPrice = view.findViewById(R.id.addonPrice);


            quantity = itemView.findViewById(R.id.quantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}

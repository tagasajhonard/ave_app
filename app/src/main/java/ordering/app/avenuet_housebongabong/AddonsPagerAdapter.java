package ordering.app.avenuet_housebongabong;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AddonsPagerAdapter extends RecyclerView.Adapter<AddonsPagerAdapter.AddonsViewHolder> {
    private List<AddonItem> addonsList;

    public AddonsPagerAdapter(List<AddonItem> addonsList) {
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
        holder.addonPrice.setText(String.format("₱%.2f", addon.getRegular())); // Use peso sign
        Glide.with(holder.itemView.getContext()).load(addon.getImageUrl()).into(holder.addOnImg);
        holder.itemView.setScaleX(0.8f); // Default size
        holder.itemView.setScaleY(0.8f);
    }

    @Override
    public int getItemCount() {
        return addonsList.size();
    }

    public static class AddonsViewHolder extends RecyclerView.ViewHolder {
        public TextView addonName, addonPrice;
        public ImageView addOnImg;

        public AddonsViewHolder(View view) {
            super(view);
            addonName = view.findViewById(R.id.addonName);
            addonPrice = view.findViewById(R.id.addonPrice);
            addOnImg = view.findViewById(R.id.addOnImg);
        }
    }
}


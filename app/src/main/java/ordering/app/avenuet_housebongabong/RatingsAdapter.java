package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {

    private List<Rating> ratingList;
    private Context context;

    public RatingsAdapter(Context context, List<Rating> ratingList) {
        this.context = context;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rating, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingList.get(position);

        holder.productName.setText(rating.getProductName());

        holder.deliveryRating.setProgress(rating.getDeliveryRating() * 1); // 5 rating maps to 100%
        holder.qualityRating.setProgress(rating.getQualityRating() * 1); // 5 rating maps to 100%
        holder.tasteRating.setProgress(rating.getTasteRating() * 1); // 5 rating maps to 100%
        holder.serviceRating.setProgress(rating.getServiceRating() * 1);

        holder.deliveryRatingValue.setText(" " + rating.getDeliveryRating());
        holder.qualityRatingValue.setText(" " + rating.getQualityRating());
        holder.tasteRatingValue.setText(" "+ rating.getTasteRating());
        holder.serviceRatingValue.setText(" " + rating.getServiceRating());

        holder.feedback.setText(rating.getFeedback());
        String fullTimestamp = rating.getTimestamp();
        String dateOnly = fullTimestamp.split(" ")[0]; // Splits at the space and keeps the date part
        holder.timestamp.setText(dateOnly);
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView productName, feedback, timestamp;
        ProgressBar  deliveryRating, qualityRating, tasteRating, serviceRating;
        TextView deliveryRatingValue, qualityRatingValue, tasteRatingValue, serviceRatingValue;
        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            deliveryRating = itemView.findViewById(R.id.delivery_progress_bar);
            qualityRating = itemView.findViewById(R.id.quality_progress_bar);
            tasteRating = itemView.findViewById(R.id.taste_progress_bar);
            serviceRating = itemView.findViewById(R.id.service_progress_bar);
            feedback = itemView.findViewById(R.id.feedback);
            timestamp = itemView.findViewById(R.id.timestamp);


            deliveryRatingValue = itemView.findViewById(R.id.delivery_rating_value);
            qualityRatingValue = itemView.findViewById(R.id.quality_rating_value);
            tasteRatingValue = itemView.findViewById(R.id.taste_rating_value);
            serviceRatingValue = itemView.findViewById(R.id.service_rating_value);
        }
    }
}

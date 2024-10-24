package ordering.app.avenuet_housebongabong;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private List<FaqItem> faqList;

    public FaqAdapter(List<FaqItem> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_item, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem faqItem = faqList.get(position);

        holder.faqTitle.setText(faqItem.getTitle());
        holder.faqDescription.setText(faqItem.getDescription());

        // Set initial state
        boolean isExpanded = faqItem.isExpanded();
        holder.faqDescription.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.expandIcon.setImageResource(isExpanded ? R.drawable.out : R.drawable.hands);

        // Handle CardView click to expand/collapse description
        holder.itemView.setOnClickListener(v -> {
            // Toggle expanded state
            faqItem.setExpanded(!isExpanded);
            notifyItemChanged(position);  // Refresh the item view
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView faqTitle, faqDescription;
        ImageView expandIcon;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            faqTitle = itemView.findViewById(R.id.faq_title);
            faqDescription = itemView.findViewById(R.id.faq_description);
            expandIcon = itemView.findViewById(R.id.expand_icon);
        }
    }
}

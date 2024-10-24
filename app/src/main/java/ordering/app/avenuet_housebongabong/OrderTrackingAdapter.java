package ordering.app.avenuet_housebongabong;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrderTrackingAdapter extends RecyclerView.Adapter<OrderTrackingAdapter.ViewHolder> {
    private List<TrackingStep> allTrackingSteps; // Store all tracking steps
    private List<TrackingStep> visibleTrackingSteps; // List of visible steps
    private boolean isExpanded = false; // Track if all steps are visible
    private static final int MAX_VISIBLE_STEPS = 2; // Set the limit for steps to show initially

    public OrderTrackingAdapter(List<TrackingStep> trackingSteps) {
        this.allTrackingSteps = trackingSteps;
        updateVisibleSteps();
    }

    // Method to update the visible steps based on the current state (expanded or collapsed)
    private void updateVisibleSteps() {

        int totalSteps = allTrackingSteps.size();

        if (isExpanded) {
            visibleTrackingSteps = new ArrayList<>(allTrackingSteps); // Show all steps when expanded
        } else {
            // Show the last 2 steps (or fewer if there are less than 2 steps)
            if (totalSteps > 2) {
                visibleTrackingSteps = allTrackingSteps.subList(totalSteps - 2, totalSteps);
            } else {
                visibleTrackingSteps = new ArrayList<>(allTrackingSteps); // If less than 2, show all
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_tracking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrackingStep step = visibleTrackingSteps.get(position);
        holder.stepTextView.setText(step.getMainText());
        holder.subTextView.setText(step.getSubText());

        Log.d("OrderTrackingAdapter", "Accepted Time: " + step.getAcceptedTime());


//        holder.dateAndTimeTextView.setText(step.getAcceptedTime());

        String displayTime;

        if (step.getReceivedTime() != null) {
            displayTime = step.getReceivedTime();
        } else if (step.getShipTime() != null) {
            displayTime = step.getShipTime();
        } else if (step.getAcceptedTime() != null) {
            displayTime = step.getAcceptedTime();
        } else {
            displayTime = step.getOrderTime();
        }

        holder.dateAndTimeTextView.setText(displayTime);

        holder.verticalLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return visibleTrackingSteps.size(); // Only return the visible steps
    }

    // Method to update steps when "Show More" or "Hide" is clicked
    public void toggleStepsVisibility(boolean expand) {
        isExpanded = expand;
        updateVisibleSteps();
        notifyDataSetChanged(); // Notify the adapter to refresh the view
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView stepTextView;
        public TextView subTextView;
        public TextView dateAndTimeTextView;
        public View verticalLine;

        public ViewHolder(View itemView) {
            super(itemView);
            dateAndTimeTextView = itemView.findViewById(R.id.dateandtime);
            stepTextView = itemView.findViewById(R.id.timeline_text);
            subTextView = itemView.findViewById(R.id.sub_text_view);
            verticalLine = itemView.findViewById(R.id.vertical_line);
        }
    }
}


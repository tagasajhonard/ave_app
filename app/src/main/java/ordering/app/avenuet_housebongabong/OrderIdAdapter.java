package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderIdAdapter extends RecyclerView.Adapter<OrderIdAdapter.OrderIdViewHolder> {

    private Context context;
    private List<Orders> orderIdList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String orderId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderIdAdapter(Context context, List<Orders> orderIdList) {
        this.context = context;
        this.orderIdList = orderIdList;
    }

    @NonNull
    @Override
    public OrderIdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderIdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderIdViewHolder holder, int position) {
        Orders order = orderIdList.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderStatusTextView.setText("Status: " + order.getStatus());
        holder.orderDateTextView.setText("Date: " + order.getOrderTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(order.getOrderId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderIdList.size();
    }

    public static class OrderIdViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderStatusTextView, orderDateTextView;

        public OrderIdViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
        }
    }
}


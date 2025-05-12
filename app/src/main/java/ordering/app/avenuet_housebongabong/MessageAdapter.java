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

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private List<Object> items;
    private List<Message> messageList;
    private String currentUser;

    public MessageAdapter(List<Message> messageList, String currentUser) {
        this.messageList = messageList;
        this.items = items;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSender().equals(currentUser)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        // Determine if the timestamp should be displayed
        boolean shouldShowTimestamp = true;
        if (position > 0) {
            Message previousMessage = messageList.get(position - 1);
            shouldShowTimestamp = !message.getFormattedTime().equals(previousMessage.getFormattedTime());
        }

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentViewHolder = (SentMessageViewHolder) holder;

            // Check if the message is an image URL
            if (message.getText() != null && message.getText().startsWith("http")) {
                sentViewHolder.messageText.setVisibility(View.GONE);
                sentViewHolder.messageImage.setVisibility(View.VISIBLE);
                Glide.with(sentViewHolder.itemView.getContext())
                        .load(message.getText())
                        .into(sentViewHolder.messageImage);
            } else {
                sentViewHolder.messageText.setText(message.getText());
                sentViewHolder.messageImage.setVisibility(View.GONE);
                sentViewHolder.messageTime.setVisibility(View.VISIBLE);
                if (shouldShowTimestamp) {
                    sentViewHolder.messageTime.setText(message.getFormattedTime());
                    sentViewHolder.messageTime.setVisibility(View.VISIBLE);
                } else {
                    sentViewHolder.messageTime.setVisibility(View.GONE);
                }
            }
        } else {
            ReceivedMessageViewHolder receivedViewHolder = (ReceivedMessageViewHolder) holder;

            // Similar logic for received messages
            if (message.getText() != null && message.getText().startsWith("http")) {
                receivedViewHolder.messageText.setVisibility(View.GONE);
            } else {
                receivedViewHolder.messageText.setText(message.getText());
                receivedViewHolder.messageTime.setVisibility(View.VISIBLE);
                if (shouldShowTimestamp) {
                    receivedViewHolder.messageTime.setText(message.getFormattedTime());
                    receivedViewHolder.messageTime.setVisibility(View.VISIBLE);
                } else {
                    receivedViewHolder.messageTime.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        ImageView messageImage;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            messageImage = itemView.findViewById(R.id.message_image);

        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        ImageView messageImage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageTime = itemView.findViewById(R.id.message_time);
            messageImage = itemView.findViewById(R.id.message_image);
        }
    }
}

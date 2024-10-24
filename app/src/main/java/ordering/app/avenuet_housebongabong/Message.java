package ordering.app.avenuet_housebongabong;

public class Message {
    private String text;
    private String sender;
    private long timestamp;
    private String formattedTime;

    // No-argument constructor required for Firebase
    public Message() {
    }

    public Message(String text, String sender, long timestamp, String formattedTime) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
        this.formattedTime = formattedTime;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }
}

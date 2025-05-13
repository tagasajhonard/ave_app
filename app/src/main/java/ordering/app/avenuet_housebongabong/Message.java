package ordering.app.avenuet_housebongabong;

public class Message {
    private String text;
    private String sender;
    private long timestamp;
    private String formattedTime;
    private String imageUrl;
    private String firebaseKey;

    // No-argument constructor required for Firebase
    public Message() {
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }



    public Message(String text, String sender, long timestamp, String formattedTime) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
        this.formattedTime = formattedTime;
        this.imageUrl = null;
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
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

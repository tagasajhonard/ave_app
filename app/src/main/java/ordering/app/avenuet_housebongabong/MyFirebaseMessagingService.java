package ordering.app.avenuet_housebongabong;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages here
        if (remoteMessage.getNotification() != null) {
            // This is a notification message
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            // You can customize notification handling here, such as showing a notification to the user
            // For example:
            sendNotification(title, body);
        }
    }

    private void sendNotification(String title, String body) {
        // Implement code to display notification to the user
        // You can use NotificationCompat.Builder to create and display notifications
    }
}

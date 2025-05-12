package ordering.app.avenuet_housebongabong;

public class Rating {
    private String cartItemId;
    private String customerName;
    private String productName;
    private int deliveryRating;
    private String feedback;
    private int qualityRating;
    private int serviceRating;
    private int tasteRating;
    private String timestamp;

    // Constructor, getters, and setters
    public Rating() {
        // Default constructor required for calls to DataSnapshot.getValue(Rating.class)
    }

    public Rating(String cartItemId, String customerName, String productName, int deliveryRating,
                  String feedback, int qualityRating, int serviceRating, int tasteRating, String timestamp) {
        this.cartItemId = cartItemId;
        this.customerName = customerName;
        this.productName = productName;
        this.deliveryRating = deliveryRating;
        this.feedback = feedback;
        this.qualityRating = qualityRating;
        this.serviceRating = serviceRating;
        this.tasteRating = tasteRating;
        this.timestamp = timestamp;
    }

    // Getters
    public String getCartItemId() { return cartItemId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public int getDeliveryRating() { return deliveryRating; }
    public String getFeedback() { return feedback; }
    public int getQualityRating() { return qualityRating; }
    public int getServiceRating() { return serviceRating; }
    public int getTasteRating() { return tasteRating; }
    public String getTimestamp() { return timestamp; }
}

package ordering.app.avenuet_housebongabong;

public class TrackingStep {
    private String mainText;
    private String subText;
    private String acceptedTime;
    private String orderTime;
    private String shipTime;
    private String receivedTime;




    public TrackingStep(String mainText, String subText, String acceptedTime, String orderTime, String shipTime, String receivedTime) {
        this.mainText = mainText;
        this.subText = subText;
        this.acceptedTime = acceptedTime;
        this.orderTime = orderTime;
        this.shipTime = shipTime;
        this.receivedTime = receivedTime;

    }

    public String getMainText() {
        return mainText;
    }

    public String getSubText() {
        return subText;
    }

    public String getAcceptedTime() {
        return acceptedTime; // Getter for accepted time
    }

    public String getOrderTime() {
        return orderTime; // Getter for order time
    }
    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }
}

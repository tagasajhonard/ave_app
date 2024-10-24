package ordering.app.avenuet_housebongabong;

import java.util.List;

//public class Order {
//    private String userName;
//    private String items;
//
//    public Order() {
//        // Default constructor required for Firebase
//    }
//
//    public Order(String userName, String items) {
//        this.userName = userName;
//        this.items = items;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getItems() {
//        return items;
//    }
//
//    public void setItems(String items) {
//        this.items = items;
//    }
//}
public class Order {
    private String fullName;
    private List<CartItem> items;

    public Order() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Order(String fullName, List<CartItem> items) {
        this.fullName = fullName;
        this.items = items;
    }

    // Getters and setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
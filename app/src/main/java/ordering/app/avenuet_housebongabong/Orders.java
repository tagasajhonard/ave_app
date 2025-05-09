package ordering.app.avenuet_housebongabong;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private String orderId;
    private String acceptedTime;
    private String orderTime;
    private String shipTime;
    private String receivedTime;
    private String status;  // Status of the order
    private List<Item> items;  // List of items in the order


    public Orders() {
        // Default constructor required for calls to DataSnapshot.getValue(Order.class)
    }

    public Orders(String orderId, String acceptedTime, String orderTime, String shipTime, String receivedTime, String status, List<Item> items) {
        this.orderId = orderId;
        this.acceptedTime = acceptedTime;
        this.orderTime = orderTime;
        this.shipTime = shipTime;
        this.receivedTime = receivedTime;
        this.status = status;
        this.items = items;

    }




    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
    public String getAcceptedTime() {
        return acceptedTime; // Add this getter method
    }

    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime; // Add this setter method
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String cartItemId;
        private String orderStatus;
        private String productImageUrl;
        private String productName;
        private String productPrice;
        private int quantity;
        private String size;
        private List<AddonItem> addons;
        private Long sugarLevel;
        private String total;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        private String orderId;

        private int itemTotal;

        public int getItemTotal() { // Getter for itemTotal
            return itemTotal;
        }

        public void setItemTotal(int itemTotal) { // Setter for itemTotal
            this.itemTotal = itemTotal;
        }


        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }



        private String acceptedTime;

        public Item() {
            addons = new ArrayList<>(); // Initialize the list of add-ons
        }

        public List<AddonItem> getAddons() {
            return addons;
        }

        public void setAddons(List<AddonItem> addons) {
            this.addons = addons;
        }


        public String getCartItemId() {
            return cartItemId;
        }

        public void setCartItemId(String cartItemId) {
            this.cartItemId = cartItemId;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String status) {
            this.orderStatus = status;
        }

        public String getProductImageUrl() {
            return productImageUrl;
        }

        public void setProductImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductPrice() {
            return productPrice;
        }

        public void setProductPrice(String productPrice) {
            this.productPrice = productPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Long getSugarLevel() {
            return sugarLevel;
        }

        public void setSugarLevel(Long sugarLevel) {
            this.sugarLevel = sugarLevel;
        }

        // Method to add an AddonItem to the add-ons list
        public void addAddon(AddonItem addon) {
            if (addons == null) {
                addons = new ArrayList<>(); // Initialize the list if it's null
            }
            addons.add(addon); // Add the addon to the list
        }

        public void clearAddons() {
            if (this.addons != null) {
                this.addons.clear();
            }
        }

        public String getAddonsAsString() {
            StringBuilder addonsString = new StringBuilder();
            if (addons != null) {
                for (AddonItem addon : addons) {
                    addonsString.append(addon.getName())
                            .append(" x")
                            .append(addon.getSelectedQuantity())
                            .append(", ₱")
                            .append(addon.getRegular())
                            .append("\n");
                }
                if (addonsString.length() > 0) {
                    addonsString.setLength(addonsString.length()); // Remove last comma and space
                }
            }
            return addonsString.toString();
        }

        private boolean rated;
        public boolean isRated() { return rated; }
        public void setRated(boolean rated) { this.rated = rated; }



    }
}

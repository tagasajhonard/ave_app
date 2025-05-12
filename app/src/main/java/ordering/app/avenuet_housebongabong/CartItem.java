package ordering.app.avenuet_housebongabong;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CartItem implements Parcelable {
    private String productName;
    private String productImageUrl;
    private String productPrice; // Can be used to hold the selected size's price
    private String smallPrice;
    private String regularPrice;
    private String largePrice;
    private int quantity;
    private String size;
    private String cartItemId;
    private boolean isSelected;

    private int sugarLevel;
    private String orderStatus;

    private String description;

    private List<AddonItem> addons;
    private int itemTotal;

    public CartItem() {

    }

    // Updated constructor for Parcelable
    protected CartItem(Parcel in) {
        productName = in.readString();


        productImageUrl = in.readString();
        productPrice = in.readString();
        smallPrice = in.readString();
        regularPrice = in.readString();
        largePrice = in.readString();
        quantity = in.readInt();
        cartItemId = in.readString();
        size = in.readString();
        description = in.readString();

        addons = new ArrayList<>();
        in.readList(addons, AddonItem.class.getClassLoader());

//        this.addons = (addons != null) ? addons : new ArrayList<>();
        this.isSelected = false;

        sugarLevel = in.readInt();
        itemTotal = in.readInt();
    }

    public void addAddon(AddonItem addon) {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        addons.add(addon);
    }
    public void clearAddons() {
        if (addons != null) {
            addons.clear();
        }
    }


    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);

        dest.writeString(productImageUrl);
        dest.writeString(productPrice);
        dest.writeString(smallPrice);
        dest.writeString(regularPrice);
        dest.writeString(largePrice);
        dest.writeInt(quantity);
        dest.writeString(cartItemId);
        dest.writeString(size);
        dest.writeString(description);
        dest.writeList(addons);
        dest.writeInt(sugarLevel);
        dest.writeInt(itemTotal);

    }

    public List<AddonItem> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonItem> addons) {
        this.addons = addons;
    }

    // New constructor with all parameters
    public CartItem(String productName, String productImageUrl, String productPrice,
                    Integer quantity,
                    String cartItemId, String size, int sugarLevel, int itemTotal) {
        this.productName = productName;

        this.productImageUrl = productImageUrl;
        this.productPrice = productPrice;
        this.smallPrice = smallPrice;
        this.regularPrice = regularPrice;
        this.largePrice = largePrice;
        this.quantity = quantity;
        this.cartItemId = cartItemId;
        this.size = size;
        this.description = description;

        this.addons = addons;
        this.sugarLevel = sugarLevel;
        this.itemTotal = itemTotal;
    }



    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(int sugarLevel) {
        this.sugarLevel = sugarLevel;

    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getSmallPrice() {
        return smallPrice;
    }

    public void setSmallPrice(String smallPrice) {
        this.smallPrice = smallPrice;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String getLargePrice() {
        return largePrice;
    }

    public void setLargePrice(String largePrice) {
        this.largePrice = largePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(int itemTotal) {
        this.itemTotal = itemTotal;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;

        if (!productName.equals(cartItem.productName)) return false;
        return size.equals(cartItem.size);
    }

    //    @Override
//    public int hashCode() {
//        int result = productName.hashCode();
//        result = 31 * result + size.hashCode();
//        return result;
//    }
    @Override
    public int hashCode() {
        int result = (productName != null) ? productName.hashCode() : 0;  // Use 0 if productName is null
        result = 31 * result + ((size != null) ? size.hashCode() : 0);    // Use 0 if size is null
        return result;
    }

}

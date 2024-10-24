package ordering.app.avenuet_housebongabong;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class AddonItem implements Parcelable {
    private String name;
    private String imageUrl; // Assuming each add-on has an image
    private double price;    // Assuming each add-on has a price
    private int selectedQuantity;


    public double getTotalPrice() {
        return price * selectedQuantity; // Calculate total price for this addon
    }

    public AddonItem() {
        // Default constructor
    }

    // Constructor
    public AddonItem(String name, String imageUrl, double price) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    protected AddonItem(Parcel in) {
        name = in.readString();
        imageUrl = in.readString(); // Add this line to read imageUrl
        price = in.readDouble();
        selectedQuantity = in.readInt(); // Read selectedQuantity
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl); // Write imageUrl
        dest.writeDouble(price);
        dest.writeInt(selectedQuantity); // Write selectedQuantity
    }

    @Override
    public int describeContents() {
        return 0; // No special objects in this case
    }

    // Parcelable Creator
    public static final Creator<AddonItem> CREATOR = new Creator<AddonItem>() {
        @Override
        public AddonItem createFromParcel(Parcel in) {
            return new AddonItem(in);
        }

        @Override
        public AddonItem[] newArray(int size) {
            return new AddonItem[size];
        }
    };


    // Getter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRegular() {
        return price;
    }

    public void setRegular(double price) {
        this.price = price;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int quantity) {
        this.selectedQuantity = quantity;
    }
}

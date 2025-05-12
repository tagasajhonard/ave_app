package ordering.app.avenuet_housebongabong;

public class Product {
    private String Category;
    private String ImageURL;
    private String QRImageURL;
    private String Large;
    private String ProductName;
    private String Regular;
    private String Small;
    private String productNameLower;
    private String Description;
    private boolean isLiked;
    private String productKey;
    private String status;
    private boolean bestSeller;


    private int tasteRating;
    private int qualityRating;
    private int serviceRating;
    private int deliveryRating;

    // Default constructor (required by Firebase)
    public Product() {
    }

    // Constructor with parameters
    public Product(String category, String imageURL, String qrImage, String large, String productName, String regular, String small, String description, String productNameLower, String status, boolean bestSeller, int tasteRating, int qualityRating, int serviceRating, int deliveryRating) {
        this.Category = category;
        this.ImageURL = imageURL;
        this.QRImageURL = qrImage;
        this.Large = large;
        this.ProductName = productName;
        this.Regular = regular;
        this.Small = small;
        this.Description = description;
        this.isLiked = false;
        this.productNameLower = productName.toLowerCase();
        this.status  = status;
        this.bestSeller = bestSeller;
        this.tasteRating = tasteRating;
        this.qualityRating = qualityRating;
        this.serviceRating = serviceRating;
        this.deliveryRating = deliveryRating;
    }

    public int getTasteRating() {
        return tasteRating;
    }

    public void setTasteRating(int tasteRating) {
        this.tasteRating = tasteRating;
    }

    public int getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(int qualityRating) {
        this.qualityRating = qualityRating;
    }

    public int getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(int serviceRating) {
        this.serviceRating = serviceRating;
    }

    public int getDeliveryRating() {
        return deliveryRating;
    }

    public void setDeliveryRating(int deliveryRating) {
        this.deliveryRating = deliveryRating;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    // Getter methods
    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return Description;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public String getLarge() {
        return Large;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getRegular() {
        return Regular;
    }

    public String getSmall() {
        return Small;
    }

    public boolean isBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(boolean bestSeller) {
        this.bestSeller = bestSeller;
    }

    public String getQrImageURL() {
        return QRImageURL;
    }

    public void setQrImageURL(String qrImageURL) {
        QRImageURL = QRImageURL;
    }


    // Setter methods (if needed)
    public void setCategory(String category) {
        this.Category = category;
    }

    public void setImageURL(String imageURL) {
        this.ImageURL = imageURL;
    }

    public void setLarge(String large) {
        this.Large = large;
    }

    public void setProductName(String productName) {
        this.ProductName = productName;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public void setRegular(String regular) {
        this.Regular = regular;
    }

    public void setSmall(String small) {
        this.Small = small;
    }
    public String getProductNameLower() {
        return productNameLower;
    }

    public void setProductNameLower(String productNameLower) {
        this.productNameLower = productNameLower;
    }

    // New getter and setter for productKey
    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

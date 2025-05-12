package ordering.app.avenuet_housebongabong;

public class FaqItem {
    private String title;
    private String description;
    private boolean isExpanded;

    public FaqItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.isExpanded = false;  // Default is collapsed
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}

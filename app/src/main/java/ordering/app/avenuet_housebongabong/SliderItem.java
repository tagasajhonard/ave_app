package ordering.app.avenuet_housebongabong;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.List;

public class SliderItem {
    private int image;
    private String title;
    private String description;

    public SliderItem(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }


    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
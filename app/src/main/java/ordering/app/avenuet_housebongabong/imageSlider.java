package ordering.app.avenuet_housebongabong;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class imageSlider extends RecyclerView.Adapter<imageSlider.SliderViewHolder> {
    private Context context;
    private int[] images;
    private ViewPager2 viewPager2;
    private Handler slideHandler;

    public imageSlider(Context context, int[] images, ViewPager2 viewPager2) {
        this.context = context;
        this.images = images;
        this.viewPager2 = viewPager2;
        this.slideHandler = new Handler(Looper.getMainLooper()); // Auto-slide handler
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_slider_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);

        // Auto-slide to the first image when reaching the end
        if (position == images.length ) {
            viewPager2.post(runnable); // Reset to the beginning when the last item is reached
        }
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImageView);
        }
    }

    // Auto-slide logic using a handler and runnable
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = viewPager2.getCurrentItem();
            if (currentItem == images.length - 1) {
                viewPager2.setCurrentItem(0, true); // Slide back to the first image
            } else {
                viewPager2.setCurrentItem(currentItem + 1, true); // Move to the next image
            }
            slideHandler.postDelayed(this, 3000); // Auto-slide every 3 seconds
        }
    };

    // Start the auto-slide
    public void startAutoSlide() {
        slideHandler.postDelayed(runnable, 3000); // Start auto-slide with a delay of 3 seconds
    }

    // Stop the auto-slide
    public void stopAutoSlide() {
        slideHandler.removeCallbacks(runnable); // Stop the auto-slide
    }
}

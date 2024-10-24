package ordering.app.avenuet_housebongabong;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ScannerActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;  // You can use any unique integer value

    private static final String TAG = "ScannerActivity";
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner2); // The layout containing the BarcodeView

        // Check if the app has permission to use the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // If permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        } else {
            // If permission is already granted, proceed to open the camera
            openCamera();
        }


        barcodeView = findViewById(R.id.barcode_scanner);

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                // Handle the scanned result here
                String scannedResult = result.getText();


                // Log the scanned result for debugging
                Log.d(TAG, "Scanned result: " + scannedResult);

                try {
                    // Find the position of the first opening curly brace '{'
                    int jsonStartIndex = scannedResult.indexOf("{");
                    if (jsonStartIndex != -1) {
                        // Extract the product ID dynamically by taking the string before the first '{'
                        String productId = scannedResult.substring(0, jsonStartIndex).trim().replace("\"", "").replace(":", "");

                        String jsonString = scannedResult.substring(jsonStartIndex);

                        JSONObject productJson = new JSONObject(jsonString);

                        Log.d(TAG, "Product ID: " + productId);

                        // Extract data
                        String productName = productJson.getString("ProductName");
                        String category = productJson.getString("Category");
                        String description = productJson.getString("Description");
                        String imageUrl = productJson.getString("ImageURL");
                        String largePrice = productJson.getString("Large");
                        String smallPrice = productJson.getString("Small");

                        // Pass this data to the DetailedProductActivity
                        Intent intent = new Intent(ScannerActivity.this, DetailProductActivity.class);

                        intent.putExtra("productName", productName);
                        intent.putExtra("productDescription", description);
                        intent.putExtra("productImageURL", imageUrl);
                        intent.putExtra("productLargePrice", largePrice);
                        intent.putExtra("productSmallPrice", smallPrice);

                        intent.putExtra("source", "scanner");
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(ScannerActivity.this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ScannerActivity.this, "Error parsing product data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // Optionally handle possible points for detection
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            // Check the background color of your layout to decide text color
            boolean isLightBackground = true; // Determine based on your layout

            // Set the status bar color
            window.setStatusBarColor(Color.TRANSPARENT);

            // Set light or dark status bar icons based on background color
            if (isLightBackground) {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

    }
    private void openCamera() {
        if (barcodeView != null) {
            barcodeView.resume();  // Resume the camera/scanner
        } 
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}



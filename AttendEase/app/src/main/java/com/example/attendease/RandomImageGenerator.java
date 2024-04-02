package com.example.attendease;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class RandomImageGenerator {
    public static Bitmap generateProfilePicture(String profileName, int imageSize) {
        // Extract initials
        String initials = getInitials(profileName);

        // Generate background color
        int backgroundColor = generateBackgroundColor(initials);

        // Generate text color
        int textColor = generateTextColor(backgroundColor);

        // Create Bitmap
        Bitmap bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw background
        canvas.drawColor(backgroundColor);

        // Draw text
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTextSize(imageSize / 2);
        paint.setTextAlign(Paint.Align.CENTER);

        float xPos = canvas.getWidth() / 2f;
        float yPos = (canvas.getHeight() / 2f) - ((paint.descent() + paint.ascent()) / 2f);
        canvas.drawText(initials, xPos, yPos, paint);

        return bitmap;
    }

    private static String getInitials(String profileName) {
        StringBuilder initials = new StringBuilder();
        String[] words = profileName.split(" ");
        for (String word : words) {
            initials.append(word.charAt(0));
        }
        return initials.toString();
    }

    private static int generateBackgroundColor(String initials) {
        // Generate a color based on initials
        // Example: Use hash function to convert initials to color
        int hash = initials.hashCode();
        return Color.rgb((hash & 0xFF0000) >> 16, (hash & 0xFF00) >> 8, hash & 0xFF);
    }

    private static int generateTextColor(int backgroundColor) {
        // Generate contrasting text color based on background color
        // Example: Use luminance to determine if black or white text is more suitable
        double luminance = (0.2126 * Color.red(backgroundColor) +
                0.7152 * Color.green(backgroundColor) +
                0.0722 * Color.blue(backgroundColor)) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
}

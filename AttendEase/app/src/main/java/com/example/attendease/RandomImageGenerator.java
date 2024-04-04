package com.example.attendease;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Represents a utility class for generating profile pictures with initials.
 */
public class RandomImageGenerator {

    /**
     * Generates a profile picture bitmap based on the given profile name and image size.
     *
     * @param profileName The name used to generate initials for the profile picture.
     * @param imageSize   The size (width and height) of the square profile picture.
     * @return A Bitmap object representing the generated profile picture.
     */
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

    /**
     * Extracts the initials from the given profile name.
     *
     * @param profileName The profile name from which to extract initials.
     * @return A String representing the extracted initials.
     */
    private static String getInitials(String profileName) {
        StringBuilder initials = new StringBuilder();
        String[] words = profileName.split(" ");
        for (String word : words) {
            initials.append(word.charAt(0));
        }
        return initials.toString();
    }

    /**
     * Generates a background color based on the initials.
     *
     * @param initials The initials used to generate the background color.
     * @return An integer representing the generated background color.
     */
    private static int generateBackgroundColor(String initials) {
        // Generate a color based on initials
        // Example: Use hash function to convert initials to color
        int hash = initials.hashCode();
        return Color.rgb((hash & 0xFF0000) >> 16, (hash & 0xFF00) >> 8, hash & 0xFF);
    }

    /**
     * Generates a contrasting text color based on the background color.
     *
     * @param backgroundColor The background color for which to generate the text color.
     * @return An integer representing the generated text color.
     */
    private static int generateTextColor(int backgroundColor) {
        // Generate contrasting text color based on background color
        // Example: Use luminance to determine if black or white text is more suitable
        double luminance = (0.2126 * Color.red(backgroundColor) +
                0.7152 * Color.green(backgroundColor) +
                0.0722 * Color.blue(backgroundColor)) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
}

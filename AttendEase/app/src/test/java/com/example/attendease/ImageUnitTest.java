package com.example.attendease;
import com.example.attendease.Image;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ImageUnitTest {

    @Test
    public void testImageUrl() {
        Image image = new Image();
        String testUrl = "http://example.com/image.jpg";
        image.setImageUrl(testUrl);
        assertEquals(testUrl, image.getImageUrl());
    }

    @Test
    public void testImageUrlConstructor() {
        String testUrl = "http://example.com/image.jpg";
        Image image = new Image(testUrl);
        assertEquals(testUrl, image.getImageUrl());
    }
}
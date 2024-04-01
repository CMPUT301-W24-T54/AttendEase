package com.example.attendease;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ImageGeneratorTest {

    @Test
    public void generateProfilePicture_isBitmapNotNull() {
        Bitmap bitmap = RandomImageGenerator.generateProfilePicture("John Doe", 100);
        assertNotNull(bitmap);
    }

    @Test
    public void generateProfilePicture_isCorrectSize() {
        int size = 100;
        Bitmap bitmap = RandomImageGenerator.generateProfilePicture("Jane Doe", size);
        assertEquals(size, bitmap.getWidth());
        assertEquals(size, bitmap.getHeight());
    }
}


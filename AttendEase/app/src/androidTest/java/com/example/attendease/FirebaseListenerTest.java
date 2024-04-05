package com.example.attendease;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FirebaseListenerTest {
    private Context context;
    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    @Test
    public void testTimeStamp() {
        // Save timestamp
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        sharedPreferences.edit().putString("TimeStamp", timeStamp).apply();

        // Retrieve saved timestamp
        String savedTimeStamp = sharedPreferences.getString("TimeStamp", "");

        // Check if saved timestamp is equal to the original timestamp
        assertEquals(timeStamp, savedTimeStamp);
    }
}
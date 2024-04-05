package com.example.attendease;

import android.util.Log;

import androidx.test.espresso.idling.CountingIdlingResource;

public class FirebaseLoadingTestHelper {
    private static final String RESOURCE_NAME = "Firebase";
    private static CountingIdlingResource sIdlingResource = new CountingIdlingResource(RESOURCE_NAME);
    public static CountingIdlingResource getIdlingResource() {
        return sIdlingResource;
    }

    public static void increment() {
        sIdlingResource.increment();
        //Log.d("error",x);
    }

    public static void decrement() {
        sIdlingResource.decrement();
        //Log.d("error",y);
    }
}

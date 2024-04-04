//package com.example.attendease;
//
//import static org.junit.Assert.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.osmdroid.views.overlay.Marker;
//import org.robolectric.Robolectric;
//
//import java.util.Collections;
//
//@RunWith(MockitoJUnitRunner.class)
//public class MapUnitTest {
//
//    MapActivity mapActivity = new MapActivity();
//
//
//    @Before
//    public void setup() {
//
//        mapActivity = Robolectric.buildActivity(MapActivity.class).create().get();
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    @Test
//    public void testFetchAttendeeName() {
//        // Mock CollectionReference
//        CollectionReference mockAttendeesRef = Mockito.mock(CollectionReference.class);
//
//        // Mock DocumentSnapshot
//        DocumentSnapshot mockDocumentSnapshot = Mockito.mock(DocumentSnapshot.class);
//        when(mockDocumentSnapshot.exists()).thenReturn(true);
//        when(mockDocumentSnapshot.getString("name")).thenReturn("tester");
//
//
//        // Mock Task
//        Task<DocumentSnapshot> mockTask = mock(Task.class);
//        when(mockTask.isSuccessful()).thenReturn(true);
//        when(mockTask.getResult()).thenReturn(mockDocumentSnapshot);
//
//        doAnswer(invocation -> {
//            OnSuccessListener<DocumentSnapshot> listener = invocation.getArgument(0);
//            listener.onSuccess(mockDocumentSnapshot);
//            return null;
//        }).when(mockTask).addOnSuccessListener(any());
//
//        // Mock DocumentReference
//        DocumentReference mockDocumentReference = mock(DocumentReference.class);
//        when(mockDocumentReference.get()).thenReturn(mockTask);
//
//        // Mock the behavior of attendeesRef.document().get()
//        when(mockAttendeesRef.document(anyString())).thenReturn(mockDocumentReference);
//
//        // Create a mocked Marker
//        Marker mockMarker = Mockito.mock(Marker.class);
//
//        // Call the method
//        mapActivity.fetchAttendeeName(mockAttendeesRef, "attendee123", mockMarker);
//
//        verify(mockAttendeesRef).document("attendee123");
//
//        // Verify that setTitle is called with the correct argument
////        verify(mockMarker).setTitle("tester");
//    }
//}

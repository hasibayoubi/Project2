package com.example.handymobileapp;
import static org.mockito.Mockito.*;

import android.widget.CalendarView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class CalendarActivityTest {

    @Mock
    FirebaseAuth mockAuth;

    @Mock
    FirebaseUser mockUser;

    @Mock
    FirebaseFirestore mockFirestore;

    @Mock
    Query mockQuery;

    @Mock
    QuerySnapshot mockSnapshot;

    @Mock
    QueryDocumentSnapshot mockDocument;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchEvents() {
        // Given
        CalendarActivity activity = new CalendarActivity();
        FirebaseAuth.setInstance(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("user_id");

        // Mock the selected date
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(2024, Calendar.APRIL, 28);

        // Mock Firestore query
        when(mockFirestore.collection(anyString())).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockSnapshot);

        // Mock the result snapshot
        when(mockSnapshot.isSuccessful()).thenReturn(true);

        // When
        ActivityScenario.launch(CalendarActivity.class);

        // Then
        verify(mockFirestore, times(1)).collection("users");
        verify(mockQuery, times(1)).whereEqualTo("date", "2024-04-28");
        verify(mockQuery, times(1)).get();
    }

    @Test
    public void testDisplayEvents() {
        // Given
        CalendarActivity activity = new CalendarActivity();
        TextView mockTextView = mock(TextView.class);
        activity.selectedDateTextView = mockTextView;

        // Mock the date
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(2024, Calendar.APRIL, 28);

        // Mock Firestore snapshot
        when(mockDocument.getString("title")).thenReturn("Test Event");
        when(mockDocument.getString("time")).thenReturn("10:00 AM");
        when(mockDocument.getString("description")).thenReturn("This is a test event description.");
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(mockDocument));
        when(mockDocument.getDate("time")).thenReturn(new Date());

        // When
        activity.displayEvents(mockSnapshot);

        // Then
        verify(mockTextView).setText(anyString());
    }
}

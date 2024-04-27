package com.example.handymobileapp;

import static org.mockito.Mockito.*;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class AccountActivityTest {

    @Mock
    FirebaseAuth mockAuth;

    @Mock
    FirebaseUser mockUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNavigationToEditAccount() {
        // Given
        AccountActivity activity = spy(AccountActivity.class);
        doNothing().when(activity).navigateToEditAccount();

        // When
        ActivityScenario<AccountActivity> scenario = ActivityScenario.launch(AccountActivity.class);
        scenario.onActivity(activityInstance -> {
            activityInstance.findViewById(R.id.buttonEditInfo).performClick();
        });

        // Then
        verify(activity, times(1)).navigateToEditAccount();
    }

    @Test
    public void testLogout() {
        // Given
        AccountActivity activity = new AccountActivity();
        FirebaseAuth.setInstance(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("user_id");

        Intent mockIntent = mock(Intent.class);
        doNothing().when(mockIntent).setFlags(anyInt());
        whenNew(Intent.class).withAnyArguments().thenReturn(mockIntent);
        doNothing().when(activity).startActivity(any(Intent.class));

        // When
        ActivityScenario<AccountActivity> scenario = ActivityScenario.launch(AccountActivity.class);
        scenario.onActivity(activityInstance -> {
            activityInstance.findViewById(R.id.buttonLogOut).performClick();
        });

        // Then
        verify(mockAuth, times(1)).signOut();
        verify(mockIntent, times(1)).setFlags(anyInt());
        verify(activity, times(1)).startActivity(any(Intent.class));
    }

    @Test
    public void testSomeOtherFunctionality() {
        // Add more tests for other functionality as needed
    }
}

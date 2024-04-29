package com.example.handymobileapp;

public class AccountActivityTest {/*

    private AccountActivity accountActivity;

    @Before
    public void setUp() {
        accountActivity = new AccountActivity();
    }

    @Test
    public void testUserDataLoaded() {
        // Set up user data manually
        accountActivity.setUserData("John", "Doe", "1990-01-01", "john.doe@example.com");

        // Verify if user data is set correctly
        assertEquals("John", accountActivity.getName());
        assertEquals("Doe", accountActivity.getLastName());
        assertEquals("1990-01-01", accountActivity.getDateOfBirth());
        assertEquals("john.doe@example.com", accountActivity.getEmail());
    }

    @Test
    public void testNavigateToEditAccount() {
        // Test navigation to EditAccount
        accountActivity.navigateToEditAccount();

        // Verify if EditAccount activity is started
        assertNotNull(accountActivity.getIntent());
        assertEquals(EditAccount.class.getName(), accountActivity.getIntent().getComponent().getClassName());
    }

    @Test
    public void testLogoutButton() {
        // Ensure activity is created
        assertNotNull(accountActivity);

        // Ensure logout button click performs logout
        accountActivity.onClickLogout();

        // Verify if user is logged out
        assertFalse(accountActivity.isUserLoggedIn());
    }

    @Test
    public void testNullUser() {
        // Ensure activity is created
        assertNotNull(accountActivity);

        // Ensure handling of null user when fetching data
        accountActivity.setCurrentUser(null);

        // Verify if appropriate error message is displayed
        assertTrue(accountActivity.isErrorFetchingData());
    }

    @Test
    public void testEmptyUserData() {
        // Ensure activity is created
        assertNotNull(accountActivity);

        // Set up empty user data manually
        accountActivity.setUserData("", "", "", "");

        // Verify if TextViews are empty
        assertTrue(accountActivity.isUserDataEmpty());
    }*/

}

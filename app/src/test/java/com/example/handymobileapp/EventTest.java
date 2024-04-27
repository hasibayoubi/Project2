package com.example.handymobileapp;

import org.junit.Test;
import java.util.Date;
import static org.junit.Assert.assertEquals;

public class EventTest {

    @Test
    public void testConstructor() {
        // Given
        String title = "Test Event";
        Date time = new Date();
        String description = "This is a test event";

        // When
        Event event = new Event(title, time, description);

        // Then
        assertEquals(title, event.getTitle());
        assertEquals(time, event.getTime());
        assertEquals(description, event.getDescription());
    }

    @Test
    public void testGettersAndSetters() {
        // Given
        Event event = new Event("Initial Title", new Date(), "Initial Description");

        // When
        String newTitle = "Updated Title";
        Date newTime = new Date();
        String newDescription = "Updated Description";

        event.setTitle(newTitle);
        event.setTime(newTime);
        event.setDescription(newDescription);

        // Then
        assertEquals(newTitle, event.getTitle());
        assertEquals(newTime, event.getTime());
        assertEquals(newDescription, event.getDescription());
    }
}

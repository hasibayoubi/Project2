
package com.example.handymobileapp;
import java.util.Date;
/**
 * Represents an event with a title, time, and description.
 */
public class Event {
    private String title;
    private Date time;
    private String description;

    /**
     * Constructs a new Event.
     * @param title The title of the event.
     * @param time The date and time of the event.
     * @param description A description of the event.
     */
    public Event(String title, Date time, String description) {
        this.title = title;
        this.time = time;
        this.description = description;
    }

    /**
     * Gets the title of the event.
     * @return The title of the event.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event.
     * @param title The new title of the event.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the time of the event.
     * @return The date and time of the event.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Sets the time of the event.
     * @param time The new date and time of the event.
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     * Gets the description of the event.
     * @return The description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event.
     * @param description The new description of the event.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a string representation of the event, useful for debugging or logging.
     * @return A string representation of the event.
     */
    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                '}';
    }
}
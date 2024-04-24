package com.example.handymobileapp;

import java.util.Date;
public class Event {
    private String title;
    private Date time;
    private String description;

    // Constructor
    public Event(String title, Date time, String description) {
        this.title = title;
        this.time = time;
        this.description = description;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for time
    public Date getTime() {
        return time;
    }

    // Setter for time
    public void setTime(Date time) {
        this.time = time;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Override toString method for debugging or logging
    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", description='" + description + '\'' +
                '}';
    }
}
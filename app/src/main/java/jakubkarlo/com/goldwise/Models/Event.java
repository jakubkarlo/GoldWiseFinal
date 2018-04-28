package jakubkarlo.com.goldwise.Models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Jakub on 15.11.2017.
 */

public class Event {

    private Bitmap eventImage;
    private String title;
    private String description;
    private ArrayList<String> participants;

    public Event(Bitmap eventImage, String title, String description) {
        this.eventImage = eventImage;
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getEventImage() {
        return eventImage;
    }

    public void setEventImage(Bitmap eventImage) {
        this.eventImage = eventImage;
    }
}

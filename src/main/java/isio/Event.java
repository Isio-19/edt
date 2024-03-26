package isio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Event {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String summary;
    private String description;
    private String prof;

    // TODO: change to enum
    private String type;
    private ArrayList<String> td;

    public Event(LocalDateTime startDate, LocalDateTime endDate, String location, String summary, String description) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.summary = summary;
        this.description = description;

        prof = null;
        type = null;
        td = null;
        if (description != null) {
            String[] descriptionList = description.split("\n");
            for (String el : descriptionList) {
                String[] elList = el.split(" : ");

                switch (elList[0]) {
                    case "Enseignant":
                        prof = elList[1];
                        break;

                    case "TD":
                        td = new ArrayList<String>(Arrays.asList(elList[1].split(", ")));
                        break;

                    case "Type":
                        type = elList[1];
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getProf() {
        return prof;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getTd() {
        return td;
    }

}

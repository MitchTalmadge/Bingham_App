package com.aptitekk.binghamapp.rssGoogleCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalendarEvent {

    String title;
    Calendar date;
    Calendar endTime;
    String location;
    String link;

    boolean dateLabelVisible = true;

    public CalendarEvent(String title, Calendar date, Calendar endTime, String location, String link) {
        this.title = title;
        this.date = date;
        this.endTime = endTime;
        this.location = location;
        this.link = link;
    }

    public String getTitle() {return title;}
    public Calendar getDate() {return date;}
    public Calendar getEndTime() {return endTime;}
    public String getLocation() {
        return location;
    }
    public String getLink() {
        return link;
    }
    public boolean isDateLabelVisible() {return dateLabelVisible;}
    public void setDateLabelVisible(boolean v) {this.dateLabelVisible = v;}

    public String toString() {
        String dateStamp = "";
        String location = "";
        String title = this.getTitle();

        //Format Date
        if (SimpleDateFormat.getDateTimeInstance().format(this.getDate().getTime()).equalsIgnoreCase(
                SimpleDateFormat.getDateTimeInstance().format(this.getEndTime().getTime()))) {
            dateStamp = (" on " + new SimpleDateFormat("MMM dd").format(this.getDate().getTime()) + " at " +
                    new SimpleDateFormat("hh:mmaa").format(this.getDate().getTime()).toLowerCase());

        } else {
            if (new SimpleDateFormat("MMM dd").format(this.getDate().getTime()).equalsIgnoreCase(
                    new SimpleDateFormat("MMM dd").format(this.getEndTime().getTime()))) {
                dateStamp = (" on " + new SimpleDateFormat("MMM dd").format(this.getDate().getTime()) + " from " +
                        new SimpleDateFormat("hh:mmaa").format(this.getDate().getTime()).toLowerCase() + " to " +
                        new SimpleDateFormat("hh:mmaa").format(this.getEndTime().getTime()).toLowerCase());
            } else {
                dateStamp = (" on " + new SimpleDateFormat("MMM dd").format(this.getDate().getTime()) + " from " +
                        new SimpleDateFormat("hh:mmaa").format(this.getDate().getTime()).toLowerCase() + " to " +
                        new SimpleDateFormat("MMM dd").format(this.getEndTime().getTime()) + " " +
                        new SimpleDateFormat("hh:mmaa").format(this.getEndTime().getTime()).toLowerCase());
            }
        }
        // Format location
        if (this.getLocation() != "") {
            if (this.getLocation().contains(",") && this.getLocation().contains("Bingham High"))
                location = "Bingham High";
            else
                location = this.getLocation();

            if(location.contains(", United States")) {
                location = location.replace(", United States", "");
            }

            if (!this.getLocation().contains(" ") && !this.getLocation().contains("Bingham High")) {
                location = "the " + location;
            }
            location = " at " + location;
        }
        // Format title
        if(this.getTitle().contains("@")) {
            title = title.split("@")[0].replaceFirst("\\s+$", ""); // cut off any trailing spaces after splitting by @
        }
        return title + location + dateStamp;
    }

    public static List<CalendarEvent> sort(List<CalendarEvent> e) {
        Collections.sort(e, new CalendarEventComparator());
        return e;
    }

    public static class CalendarEventComparator implements Comparator<CalendarEvent> {
        @Override
        public int compare(CalendarEvent o1, CalendarEvent o2) {
            //Put A/B Day Labels at the top of each day.
            if(o1.getTitle().equalsIgnoreCase("A Day") || o1.getTitle().equalsIgnoreCase("B Day")) {
                if(eventMatchesDay(o1, o2.getDate())) {
                    return -1;
                }
            }
            if(o2.getTitle().equalsIgnoreCase("A Day") || o2.getTitle().equalsIgnoreCase("B Day")) {
                if(eventMatchesDay(o2, o1.getDate())) {
                    return 1;
                }
            }

            return o1.getDate().compareTo(o2.getDate());
        }
    }

    public static List<CalendarEvent> eventsMatchesDay(List<CalendarEvent> e, Calendar day) {
        List<CalendarEvent> result = new ArrayList<>();
        for(CalendarEvent event : e) {
            if(event.getDate().get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH) &&
                    event.getDate().get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                    event.getDate().get(Calendar.YEAR) == day.get(Calendar.YEAR)) {
                result.add(event);
            }
        }
        return result;
    }
    public static boolean eventMatchesDay(CalendarEvent event, Calendar day) {
        return event.getDate().get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH) &&
                event.getDate().get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                event.getDate().get(Calendar.YEAR) == day.get(Calendar.YEAR);
    }

}

// CalendarReminder.java
// Temitope S. Olugbemi
// 3/26/23
// Interface which supports building and sending reminders

package edu.fscj.cop2805c.calendar;

public interface CalendarReminder {

    // send a reminder using contact's preferred notification method
    void sendReminder(Reminder reminder);
}

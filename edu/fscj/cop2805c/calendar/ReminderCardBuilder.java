// ReminderCardBuilder.java
// Temitope S. Olugbemi
// 3/26/23
// An interface for building reminder cards.

package edu.fscj.cop2805c.calendar;

public interface ReminderCardBuilder {

    // build a reminder card with a message
    Reminder buildReminder(Appointment appt);
}

// ReminderCard.java
// Temitope S. Olugbemi
// 3/26/23
// Class which represents a reminder card

package edu.fscj.cop2805c.calendar;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ReminderCard implements ReminderCardBuilder{

    Appointment appointment;

    public ReminderCard(Appointment appointment){
        this.appointment = appointment;
        buildReminder(appointment);
    }

    // build a reminder in the form of a formatted String
    @Override
    public Reminder buildReminder(Appointment appt) {
        //        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//        + Tuesday, February 28, 2023 at 6:32:44 PM Eastern Standard Time +
//        + You have an appointment!                                       +
//        + John Smith                                                     +
//        + Title: Dentist                                                 +
//        + Description: Cleaning appointment with Dr. Kildaire            +
//        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        final String NEWLINE = "\n";

        Contact c = appt.getContact();

        // Get the dates/times

        // build the reminder message
        // embed newlines, so we can split per line and use token (line) lengths
        String msg;
        try {
            // load the property and create the localized greeting
            ResourceBundle res = ResourceBundle.getBundle(
                    "edu.fscj.cop2805c.calendar.Reminder", c.getLocale());
            String youHaveAnAppointment = res.getString("YouHaveAnAppointment");

            // format and display the date
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
            formatter =
                    formatter.localizedBy(c.getLocale());
            msg = appt.getReminder().format(formatter) + NEWLINE;

            // add the localized reminder
            msg += youHaveAnAppointment + "\n" + c.getName() + NEWLINE;
        } catch (java.util.MissingResourceException e) {
            System.err.println(e);
            msg = "You Have An Appointment!" + NEWLINE + c.getName() + NEWLINE;
        }

        msg +=
                "Title: " + appt.getTitle() + NEWLINE +
                        "Description: " + appt.getDescription() + NEWLINE;
        // split and get the max length
        String[] msgSplit = msg.split(NEWLINE);
        int maxLen = 0;
        for (String s : msgSplit)
            if (s.length() > maxLen)
                maxLen = s.length();
        maxLen += 4; // Adjust for padding and new line

        // create our header/footer (all plus signs)
        char[] plusChars = new char[maxLen];
        Arrays.fill(plusChars, '+');
        String headerFooter = new String(plusChars);

        // add the header to our output
        StringBuilder newMsg = new StringBuilder(headerFooter + "\n");

        // reuse the header template for our body lines (plus/spaces/plus)
        Arrays.fill(plusChars, ' ');
        plusChars[0] = plusChars[maxLen - 1] = '+';
        String bodyLine = new String(plusChars);

        // for each string in the output, insert into a body line
        for (String s : msgSplit) {
            StringBuilder sBld = new StringBuilder(bodyLine);
            // add 2 to end position in body line replace
            // operation so final space/plus don't get pushed out
            sBld.replace(2,s.length() + 2, s);
            // add to our output
            newMsg.append(new String(sBld)).append("\n");
        }
        newMsg.append(headerFooter).append("\n");
        //System.out.println(newMsg); //debug

        return new Reminder(newMsg.toString(), appt.getReminder(), c);
    }
}

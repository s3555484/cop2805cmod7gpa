// AppointmentApp.java
// Temitope S. Olugbemi
// 3/26/23
// creates an appointment for a contact

package edu.fscj.cop2805c.calendar;

import edu.fscj.cop2805c.dispatch.Dispatcher;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

// main application class
public class AppointmentApp implements CalendarReminder, Dispatcher<Reminder> {

    private final ArrayList<Appointment> appointments = new ArrayList<>();

    // Use a ConcurrentLinkedQueue<LinkedList> to act as message queue for the dispatcher
    private final ConcurrentLinkedQueue<Reminder> queue = new ConcurrentLinkedQueue<>(new LinkedList<>());
//    private final Stream<Reminder> stream = queue.stream();

    private final Random rand = new Random();
    private int numAppointments = 0;

    // dispatch the reminder using the dispatcher
    public void dispatch(Reminder reminder) {
        this.queue.add(reminder);
        // System.out.println("current queue length is " + this.queue.size()); // debug
    }

    // send a reminder using contact's preferred notification method
    public void sendReminder(Reminder reminder) {
        Contact c = reminder.getContact();
        if (c.getRemindPref() == ReminderPreference.NONE)
            System.out.println(
                    "Error: no Reminder Preference set for " + c.getName());
        else {
            if (c.getRemindPref() == ReminderPreference.EMAIL)
                System.out.println(
                        "Sending the following email message to " + c.getName() + " at " +
                                c.getEmail());
                // all that's left is phone!
            else if (c.getRemindPref() == ReminderPreference.PHONE)
                System.out.println(
                        "Sending the following SMS message to " + c.getName() + " at " +
                                c.getPhone());

            // dispatch the card
            // dispatch(bc);
            // show an alternative dispatch using a lambda
            Dispatcher<Reminder> d = this.queue::add;
            d.dispatch(reminder);
        }
    }

    private Appointment createRandomAppointment(Contact c) {
        ZonedDateTime apptTime, reminder;
        int plusVal = rand.nextInt() % 12 + 1;
        // create a future appointment using random month value
        apptTime = ZonedDateTime.now().plusMonths(plusVal);

        // create the appt reminder for the appointment time minus random (<24) hours
        // use absolute value in case random is negative to prevent reminders > appt
        int minusVal = Math.abs(rand.nextInt()) % 24 + 1;
        reminder = apptTime.minusHours(minusVal);
        // create an appointment using the contact and appt time
        Appointment appt = new Appointment("Test Appointment " + ++numAppointments,
                "This is test appointment " + numAppointments,
                c, apptTime);
        appt.setReminder(reminder);
        return appt;
    }

    private void addAppointments(Appointment... appts) {
        Collections.addAll(appointments, appts);
    }

    private void checkReminderTime(Appointment appt) {
        ZonedDateTime current = ZonedDateTime.now();
        ZonedDateTime dt = appt.getReminder();

        // see if it's time to send a reminder
        // TODO: create a Reminder class and override equals()
        if (    dt.getYear() == current.getYear() &&
                dt.getMonth() == current.getMonth() &&
                dt.getDayOfMonth() == current.getDayOfMonth() &&
                dt.getHour() == current.getHour() &&
                dt.getMinute() == current.getMinute()) {
            var reminder = (new ReminderCard(appt)).buildReminder(appt);
            sendReminder(reminder);
        }

    }

    private void generateReminders() {

        // send reminders where needed
        for (Appointment a: appointments ) {
            checkReminderTime(a);
        }

        //System.out.println("starting forEach");
        //stream.forEach(System.out::print);

        appointments.clear(); // clear the list
    }
    // unit test
    public static void main(String[] args) {

        AppointmentApp apptApp = new AppointmentApp();

        // start the processor thread
        ReminderCardProcessor processor = new ReminderCardProcessor(apptApp.queue);

        ZonedDateTime current = ZonedDateTime.now();

        // start with a contact
        Contact c = new Contact("Smith", "John", "JohnSmith@email.com",
                "(904) 555-1212", ReminderPreference.PHONE,
                ZoneId.of("America/New_York"), new Locale("en"));
        Appointment a1 = apptApp.createRandomAppointment(c);
        a1.setReminder(current);

        // create more contacts to test locales
        c = new Contact("Coutaz", "Joëlle", "Joëlle.Coutaz@email.com",
                "33 01 09 75 83 51", ReminderPreference.EMAIL,
                ZoneId.of("Europe/Paris"), new Locale("fr"));
        Appointment a2 = apptApp.createRandomAppointment(c);
        a2.setReminder(current);

        c = new Contact("Bechtolsheim", "Andy", "Andy.Bechtolsheim@email.com",
                "33 01 09 75 83 51", ReminderPreference.EMAIL,
                ZoneId.of("Europe/Berlin"), new Locale("de"));
        Appointment a3 = apptApp.createRandomAppointment(c);
        a3.setReminder(current);

        c = new Contact("Peisu", "Xia", "Xia.Peisu@email.com",
                "33 01 09 75 83 51", ReminderPreference.EMAIL,
                ZoneId.of("Asia/Shanghai"), new Locale("zh"));
        Appointment a4 = apptApp.createRandomAppointment(c);
        a4.setReminder(current);

        apptApp.addAppointments(a1, a2, a3, a4);
        apptApp.generateReminders();

        // wait for a bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        apptApp.addAppointments(a1, a2, a3, a4);
        apptApp.generateReminders();

        // wait for a bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            System.out.println("sleep interrupted! " + ie);
        }

        processor.endProcessing();
    }
}

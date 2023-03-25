// ReminderCardProcessor.java
// Temitope S. Olugbemi
// 3/26/23
// Process reminder cards

package edu.fscj.cop2805c.calendar;

import edu.fscj.cop2805c.reminder.ReminderProcessor;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ReminderCardProcessor extends Thread implements ReminderProcessor{

    private final ConcurrentLinkedQueue<Reminder> safeQueue;
    private boolean stopped = false;

    public ReminderCardProcessor(ConcurrentLinkedQueue<Reminder> safeQueue) {
        this.safeQueue = safeQueue;

        // start polling (invokes run(), below)
        this.start();
    }

    // remove reminders from the queue and process them
    @Override
    public void processReminder() {
        System.out.println("before processing, queue size is " + safeQueue.size());
        safeQueue.stream().forEach(e -> {
            // Do something with each element
            e = safeQueue.remove();
            System.out.print(e);
        });
        System.out.println("after processing, queue size is now " + safeQueue.size());
    }

    // allow external class to stop us
    public void endProcessing() {
        this.stopped = true;
        interrupt();
    }

    // poll queue for cards
    public void run() {
        final int SLEEP_TIME = 1000; // ms
        while (true) {
            try {
                processReminder();
                Thread.sleep(SLEEP_TIME);
                System.out.println("polling");
            } catch (InterruptedException ie) {
                // see if we should exit
                if (this.stopped) {
                    System.out.println("poll thread received exit signal");
                    break;
                }
            }
        }
    }
}

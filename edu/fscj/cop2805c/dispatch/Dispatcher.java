// Dispatcher.java
// Temitope S. Olugbemi
// 2/15/23
// A generic interface for dispatching messages.

package edu.fscj.cop2805c.dispatch;

public interface Dispatcher<T> {
    void dispatch(T t);
}

package com.komsiluk.taxi.util;

public class Event<T> {
    /** moze se desiti da se prilikom ponovnog kreiranja aktivnosti ili fragmenta ponovo aktivira
    *   observer a mi to ne zelimo, event nam omogucava da se observer aktivira tacno jednom
     * */
    private final T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public T peekContent() {
        return content;
    }
}


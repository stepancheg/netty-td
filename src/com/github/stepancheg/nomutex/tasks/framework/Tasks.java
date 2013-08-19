package com.github.stepancheg.nomutex.tasks.framework;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class to implement {@link ActorRunner}.
 *
 * @author Stepan Koltsov
 */
public class Tasks {

    private enum State {
        /** actor is not currently running */
        WAITING,
        /** actor is running, and has no more tasks */
        RUNNING_NO_TASKS,
        /** actor is running, but some queues probably updated, actor needs to recheck them */
        RUNNING_GOT_TASKS,
    }

    private final AtomicInteger state = new AtomicInteger();

    /**
     * @return <code>true</code> iff we have to recheck queues
     */
    public boolean fetchTask() {
        int old = state.getAndDecrement();
        if (old == State.RUNNING_GOT_TASKS.ordinal()) {
            return true;
        } else if (old == State.RUNNING_NO_TASKS.ordinal()) {
            return false;
        } else {
            throw new AssertionError();
        }
    }

    /**
     * @return <code>true</code> iff caller has to schedule task execution
     */
    public boolean addTask() {
        // fast track for high-load applications
        // atomic get is cheaper than atomic swap
        // for both this thread and fetching thread
        if (state.get() == State.RUNNING_GOT_TASKS.ordinal())
            return false;

        int old = state.getAndSet(State.RUNNING_GOT_TASKS.ordinal());
        return old == State.WAITING.ordinal();
    }

}

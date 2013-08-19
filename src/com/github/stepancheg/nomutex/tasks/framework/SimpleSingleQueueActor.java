package com.github.stepancheg.nomutex.tasks.framework;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Base class for implementation of actors, processing single queue.
 *
 * @author Stepan Koltsov
 */
public abstract class SimpleSingleQueueActor<T> {


    protected abstract void processMessage(T message);


    private final LockFreeStackWithSize<T> queue = new LockFreeStackWithSize<T>();

    private final ActorRunner runner;

    protected SimpleSingleQueueActor(Executor executor) {
        this.runner = new ActorRunner(new ActorImpl(), executor);
    }

    public int getQueueSize() {
        return queue.size();
    }

    class ActorImpl implements Runnable {
        @Override
        public void run() {
            List<T> items = queue.removeAllReversed();
            // TODO: process in reverse order
            for (T item : items) {
                processMessage(item);
            }
        }
    }

        /**
     * Add task for this actor.
     */
    public void addWork(T item) {
        boolean added = queue.add(item);
        if (!added)
            throw new AssertionError();

        //runner.scheduleHereAtMostOnce();
        runner.schedule();
    }

    public void complete() {
        runner.complete();
    }

}

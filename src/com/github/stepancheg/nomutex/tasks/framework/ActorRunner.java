package com.github.stepancheg.nomutex.tasks.framework;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

/**
 * Entry point of the framework.
 *
 * @author Stepan Koltsov
 */
public class ActorRunner {

    /** Scheduling state */
    private final Tasks tasks = new Tasks();
    /** <code>true</code> if actor is shutting down */
    private volatile boolean requestCompleteSignal = false;
    /** Wait here for completion of actor */
    private final CountDownLatch completeLatch = new CountDownLatch(1);
    /** Helper runnable */
    private final RunnableImpl runnable = new RunnableImpl();

    private final Runnable actor;
    private final Executor executor;

    /**
     * @param actor actor to be executed
     * @param executor actors are executed in the given executor
     */
    public ActorRunner(Runnable actor, Executor executor) {
        this.actor = actor;
        this.executor = executor;
    }

    /**
     * Schedule actor.
     *
     * If actor is sleeping, then actor will be executed right now.
     * If actor is executing right now, it will be executed one more time.
     * If this method is called multiple time, actor will be re-executed no more than one more time.
     */
    public void schedule() {
        if (tasks.addTask()) {
            executor.execute(runnable);
        }
    }

    /**
     * Schedule actor, execute it in current thread.
     *
     * If actor is running, continue executing where it is executing.
     * If actor is sleeping, execute it in current thread.
     *
     * Operation is useful for tasks that are likely to complete quickly.
     */
    public void scheduleHere() {
        if (tasks.addTask()) {
            loop();
        }
    }

    /**
     * Schedule actor, execute in current thread no more than once.
     *
     * If actor is running, continue executing where it is executing.
     * If actor is sleeping, execute one iteration here, and if actor got new tasks,
     * reschedule it in worker pool.
     */
    public void scheduleHereAtMostOnce() {
        if (tasks.addTask()) {
            if (!tasks.fetchTask()) {
                throw new AssertionError();
            }
            actor.run();

            if (tasks.fetchTask()) {
                if (tasks.addTask()) {
                    throw new AssertionError();
                }
                executor.execute(runnable);
            } else {
                if (requestCompleteSignal) {
                    completeLatch.countDown();
                }
            }
        }
    }

    private void loop() {
        for (int i = 0; tasks.fetchTask(); ++i) {
            actor.run();

            // poor man scheduler: if actor is executing too long,
            // give other actors a chance to be executed
            if (i == 1000) {
                if (tasks.fetchTask()) {
                    if (tasks.addTask()) {
                        throw new AssertionError();
                    }
                    executor.execute(runnable);
                    return;
                }
            }
        }

        if (requestCompleteSignal) {
            completeLatch.countDown();
        }
    }

    /**
     * Wait for completion of the actor.
     */
    public void complete() {
        requestCompleteSignal = true;
        schedule();
        try {
            completeLatch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class RunnableImpl implements Runnable {
        @Override
        public void run() {
            loop();
        }
    }

}

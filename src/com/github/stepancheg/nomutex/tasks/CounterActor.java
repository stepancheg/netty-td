package com.github.stepancheg.nomutex.tasks;

import com.github.stepancheg.nomutex.common.Computation;
import com.github.stepancheg.nomutex.tasks.framework.ActorRunner;
import com.github.stepancheg.nomutex.tasks.framework.LockFreeStackWithSize;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Simple version of this actor is {@link CounterSimpleActor}.
 *
 * @author Stepan Koltsov
 */
public class CounterActor implements Runnable {

    final Computation computation = new Computation();

    private final LockFreeStackWithSize<BigInteger> queue = new LockFreeStackWithSize<BigInteger>();

    private final ActorRunner runner;

    /**
     * @param executor to execute this actor
     */
    public CounterActor(Executor executor) {
        this.runner = new ActorRunner(this, executor);
    }

    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public void run() {
        List<BigInteger> items = queue.removeAllReversed();
        // TODO: processes in reverse order
        for (BigInteger item : items) {
            computation.update(item);
        }
    }

        /**
     * Add task for this actor.
     */
    public void addWork(BigInteger item) {
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

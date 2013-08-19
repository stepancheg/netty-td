package com.github.stepancheg.nomutex.mutex;

import com.github.stepancheg.nomutex.common.Computation;
import com.github.stepancheg.nomutex.common.Parameters;

import java.math.BigInteger;

/**
 * @author Stepan Koltsov
 */
public class Worker implements Runnable {

    private final Computation computation;

    private static final Object mutex = new Object();

    public Worker(Computation computation) {
        this.computation = computation;
    }

    @Override
    public void run() {
        for (int i = 0; i < Parameters.EMIT_BY_THREAD; ++i) {
            BigInteger item = Parameters.getNumber(i);
            synchronized (mutex) {
                computation.update(item);
            }
        }

        System.out.println("worker completed");
    }
}

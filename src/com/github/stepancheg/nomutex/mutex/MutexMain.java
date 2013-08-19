package com.github.stepancheg.nomutex.mutex;

import com.github.stepancheg.nomutex.common.Computation;
import com.github.stepancheg.nomutex.common.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stepan Koltsov
 */
public class MutexMain {

    public static void main(String[] args) throws Exception {
        System.out.println(MutexMain.class.getSimpleName() + " " + Parameters.describe());

        List<Thread> threads = new ArrayList<Thread>();

        Computation computation = new Computation();

        for (int i = 0; i < 2; ++i) {
            threads.add(new Thread(new Worker(computation)));
        }

        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }

        System.out.println("joining threads");

        for (Thread thread : threads) {
            thread.join();
        }

        long duration = System.currentTimeMillis() - start;

        System.out.println(computation.getSum());
        System.out.print("took " + duration + "ms");
    }

}

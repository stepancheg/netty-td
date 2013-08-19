package com.github.stepancheg.nettytd;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * @author Stepan Koltsov
 */
public class Perftest {

    public static void main(String[] args) throws Exception {
        Server server = new Server();

        Semaphore semaphore = new Semaphore(100000);

        Client client = new Client(server.getPort(), new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                semaphore.release();
            }
        });

        for (;;) {
            long start = System.currentTimeMillis();

            int iter = 10000;
            long total = 0;
            while (System.currentTimeMillis() - start < 1000) {
                for (int i = 0; i < iter; ++i) {
                    semaphore.acquire();
                    client.send(100);
                }
                total += iter;
                iter *= 2;
            }

            long dms = System.currentTimeMillis() - start;
            long rps = total * 1000 / dms;
            System.out.println("rps: " + rps);
        }
    }
}

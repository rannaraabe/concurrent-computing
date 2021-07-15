package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/yufeialex/JavaStudy/blob/e924a14de45436d0ce233f9429e10e2ec6442889/language/src/main/java/com/petrichor/java/language/jvm/MaxThreadsMain.java
 */
public class MaxThreadsMain {
    private static final int BATCH_SIZE = 4000;

    public static void main(String... args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        try {
            for (int i = 0; i <= 100 * 1000; i += BATCH_SIZE) {
                long start = System.currentTimeMillis();
                addThread(threads, BATCH_SIZE);
                long end = System.currentTimeMillis();
                Thread.sleep(1000);
                long delay = end - start;
                System.out.printf("%,d threads: Time to create %,d threads was %.3f seconds %n", threads.size(), BATCH_SIZE, delay / 1e3);
            }
        } catch (Throwable e) {
            System.err.printf("After creating %,d threads, ", threads.size());
            e.printStackTrace();
        }

    }

    private static void addThread(List<Thread> threads, int num) {
        for (int i = 0; i < num; i++) {
            Thread t = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ignored) {
                    //
                }
            });
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            threads.add(t);
            t.start();
        }
    }
}

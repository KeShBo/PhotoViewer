package cn.keshb.photoview.util;

import javafx.concurrent.Task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JavaFX Task的线程池
 * @author keshb
 */
public class TaskThreadPool {

    private static final int QUEUE_SIZE = 5;
    private static final ExecutorService THREAD_POOL
            = new ThreadPoolExecutor(2, 5, 5, TimeUnit.SECONDS
            , new LinkedBlockingDeque<>(QUEUE_SIZE)
            , new TaskThreadFactory());


    public static void execute(Runnable r) {
        THREAD_POOL.execute(r);
    }

    /**
     * 作为Task执行
     * @param r runnable
     */
    public static void executeAsTask(Runnable r) {
        THREAD_POOL.execute(new Task<>() {
            @Override
            protected Object call() {
                r.run();
                return null;
            }
        });
    }


    /**
     * Task线程池的ThreadFactory，设置线程名和daemon
     */
    public static final class TaskThreadFactory implements ThreadFactory {

        private static final String NAME_PREFIX = "task-pool-thread-";
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, NAME_PREFIX + threadNumber.getAndIncrement());
            t.setDaemon(true);
            return t;
        }
    }
}

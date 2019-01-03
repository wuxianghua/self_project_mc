package net.imoran.auto.music.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolUtils {
    private static ExecutorService executors = Executors.newCachedThreadPool();

    /**
     * 执行任务
     */
    public static void execute(Runnable task) {
        executors.execute(task);
    }

    /**
     * 提交任务
     */
    public static Future submit(Runnable task) {
        return executors.submit(task);
    }

}

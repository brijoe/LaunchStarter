package com.github.brijoe.starter;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class TaskExecutor {

    private WorkerExecutor workerExecutor = new WorkerExecutor();
    private UIExecutor UIExecutor = new UIExecutor();

    private Linker linker;

    public TaskExecutor(Linker linker) {
        this.linker = linker;
    }

    public void enqueueTask(StartTask startTask) {
        StarterLog.d("enqueueTask-" + startTask);
        if (startTask.runInMainThread()) {
            UIExecutor.execute(startTask);
        } else {
            workerExecutor.execute(startTask);
        }
    }

    public void await() {
        UIExecutor.loop();
    }

    public void wakeup() {
        UIExecutor.wakeup();
    }


    class UIExecutor implements Executor {

        private BlockingDeque<StartTask> mainTaskBlockingDeque = new LinkedBlockingDeque<>();
        private final Object lock = new Object();

        @Override
        public void execute(Runnable command) {
            mainTaskBlockingDeque.add((StartTask) command);
        }

        public void loop() {
            //循环执行
            for (; ; ) {
                try {
                    StartTask task = mainTaskBlockingDeque.take();
                    StarterLog.d("awaitInMainThread 取出任务" + task);
                    //取完了就不阻塞了
                    task.run();
                    StarterLog.d("awaitInMainThread 执行任务" + task);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (linker.mainThreadTaskCounter.decrementAndGet() == 0) {
                        StarterLog.d("awaitInMainThread 主线程任务执行完毕");
                        break;
                    }
                }
            }
            StarterLog.d("awaitInMainThread 开始尝试等待");
            //主线程任务执行完成看是否还有其他等待任务，如果有则等待
            synchronized (lock) {
                StarterLog.d("awaitInMainThread 获取到lock锁");
                while (linker.needWaitTaskCounter.get() > 0) {
                    try {
                        StarterLog.d("awaitInMainThread 主线程等待");
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            StarterLog.d("awaitInMainThread-start方法返回");
        }

        public void wakeup() {
            lock.notifyAll();
        }
    }


    static class WorkerExecutor implements Executor {

        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        // We want at least 2 threads and at most 4 threads in the core pool,
        // preferring to have 1 less than the CPU count to avoid saturating
        // the CPU with background work
        private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        private static final int KEEP_ALIVE_SECONDS = 30;

        public static final Executor THREAD_POOL_EXECUTOR;

        private static final BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<Runnable>(128);


        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "Starter #" + mCount.getAndIncrement());
            }
        };

        static {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            THREAD_POOL_EXECUTOR = threadPoolExecutor;

        }


        @Override
        public void execute(Runnable command) {
            THREAD_POOL_EXECUTOR.execute(command);
        }
    }
}

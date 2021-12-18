package com.github.brijoe.starter;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Starter 对外提供方法
 * <p>
 * TaskDispatcher 管理任务 负责 将任务 交给对应的执行器
 */
public enum Starter {

    INSTANCE;

    private Map<Class<? extends StartTask>, TaskWrapper> taskMap = new ConcurrentHashMap<>();
    private AtomicBoolean hasStarted = new AtomicBoolean(false);
    private Linker linker = new Linker();

    /**
     * 向调度器里面添加一个任务,必须在UI 线程调度
     *
     * @param startTask
     * @return
     */
    @UiThread
    public Starter addTask(@NonNull StartTask startTask) {
        assertNotNull(startTask, "task can't be null,please check");
        assertMainThread();
        taskMap.put(startTask.getClass(),
                new TaskWrapper(startTask, linker.taskTracker));
        return this;
    }

    /**
     * 启动调度器,必须在UI线程调度
     */
    @UiThread
    public void start() {
        assertMainThread();
        assertInit();
        if (hasStarted.compareAndSet(false, true)) {
            linker.init(taskMap);
        }
    }

    private void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private void assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("must be called in UI Thread");
        }
    }

    private void assertInit() {
        if (hasStarted.get()) {
            throw new IllegalStateException("can not be start again!");
        }
    }
}

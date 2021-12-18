package com.github.brijoe.starter;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 内部启动任务封装
 * <p>
 * 增加新的方法 调度器内部使用
 */
class TaskWrapper extends StartTask {

    private volatile boolean mInFlight = false;
    private StartTask originTask;
    private TaskListener listener;

    public TaskWrapper(@NonNull StartTask originTask, @NonNull TaskListener listener) {
        this.originTask = originTask;
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.onTaskStart(this);
        try {
            originTask.run();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        listener.onTaskFinish(this);
    }

    @Override
    public boolean runInMainThread() {
        return originTask.runInMainThread();
    }

    @Override
    public boolean needWaitFinished() {
        return originTask.needWaitFinished();
    }

    @Override
    public List<Class<? extends StartTask>> dependsOn() {
        return originTask.dependsOn();
    }

    public boolean needWait() {
        return !runInMainThread() && needWaitFinished();
    }


    public String getTaskName() {
        return originTask.getClass().getSimpleName();
    }

    public final void markInFlight() {
        mInFlight = true;
    }

    protected final void markFinish() {
        mInFlight = false;
    }

    protected final boolean isInFlight() {
        return mInFlight;
    }

    @Override
    public String toString() {
        return "TaskWrapper{" +
                "originStartTask=" + originTask +
                ", listener=" + listener +
                '}';
    }
}

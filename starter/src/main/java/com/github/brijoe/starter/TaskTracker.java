package com.github.brijoe.starter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 任务追踪调度,负责任务状态管理
 */
class TaskTracker extends HandlerThread implements TaskListener {
    private Linker linker;
    private TrackerHandler handler;
    private final int WHAT_TASK_INIT = -1;
    private final int WHAT_TASK_START = 0;
    private final int WHAT_TASK_FINISH = 1;
    private AtomicBoolean firstTaskArrived = new AtomicBoolean(false);

    public TaskTracker(Linker linker) {
        super("Starter#TaskTracker");
        this.linker = linker;
    }

    @Override
    public void start() {
        super.start();
        handler = new TrackerHandler(getLooper());
        Message.obtain(handler, WHAT_TASK_INIT).sendToTarget();
    }


    @Override
    public void onTaskStart(TaskWrapper startTask) {
        Message.obtain(handler, WHAT_TASK_START, startTask).sendToTarget();
    }

    @Override
    public void onTaskFinish(TaskWrapper startTask) {
        Message.obtain(handler, WHAT_TASK_FINISH, startTask).sendToTarget();
    }

    class TrackerHandler extends Handler {
        public TrackerHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WHAT_TASK_INIT:
                    enqueueNextTasks();
                    break;
                case WHAT_TASK_START:
                    notifyTaskStarted((TaskWrapper) msg.obj);
                    break;
                case WHAT_TASK_FINISH:
                    notifyTaskFinished((TaskWrapper) msg.obj);
                    break;
            }
        }
    }

    private void notifyStarterLaunch() {
        StarterLog.d("TaskTracker-开始调度执行任务");
    }

    private void notifyStarterFinished() {
        StarterLog.d("TaskTracker-结束任务执行完毕");
    }

    private void notifyTaskStarted(TaskWrapper startTask) {
        if (firstTaskArrived.compareAndSet(false, true)) {
            notifyStarterLaunch();
        }
        StarterLog.d("TaskTracker-" + startTask + "-开始执行");
    }

    private void notifyTaskFinished(TaskWrapper startTask) {
        startTask.markFinish();
        //任务执行完毕
        StarterLog.d("TaskTracker-onTaskFinish" + startTask);
        //处理队列
        linker.taskGraph.removeZeroNode(startTask);
        linker.taskGraph.printGraph();
        enqueueNextTasks();
        //wait类型任务完成，尝试唤醒主线程
        if (startTask.needWait()) {
            if (linker.needWaitTaskCounter.decrementAndGet() == 0) {
                linker.taskExecutor.wakeup();
            }
        }
        //所有任务执行完毕
        if (linker.taskGraph.isEmpty()) {
            notifyStarterFinished();
            linker.taskDispatcher.release();
            this.release();
        }
    }

    private void release() {
        this.quit();
    }

    /**
     * 任务从图中移除的时机
     * 当任务完成执行时，从图中移除这个0入度任务
     * 同时从图中继续检索0入度结点（可能在执行当中）
     * 如果是未在执行 则加入调度器
     */

    private void enqueueNextTasks() {
        for (TaskWrapper t : linker.taskGraph.getZeroNodes()) {
            //没在执行当中
            if (!t.isInFlight()) {
                StarterLog.d("TaskDispatcher-enqueueNextTasks " + t.dump());
                linker.taskDispatcher.addTask(t);
                t.markInFlight();
            }
        }
    }
}
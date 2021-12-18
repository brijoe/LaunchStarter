package com.github.brijoe.starter;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 核心逻辑 :只负责从阻塞队列中获取任务并执行任务派发
 * 持有 阻塞队列,执行器,
 * 对外提供 addTask 方法 没任务时 退出
 */
class TaskDispatcher extends Thread {

    private BlockingDeque<TaskWrapper> taskBlockingDeque = new LinkedBlockingDeque<>();

    private Linker linker;

    public TaskDispatcher(Linker linker) {
        super("Starter#TaskDispatcher");
        this.linker = linker;
    }


    @Override
    public void run() {
        while (true) {
            TaskWrapper startTask = null;
            try {
                startTask = taskBlockingDeque.take();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            } finally {
                if (startTask != null) {
                    StarterLog.d("TaskDispatcher-run " + startTask);
                    linker.taskExecutor.enqueueTask(startTask);
                    StartStatics.record(startTask);
                }
            }
        }
        //如果任务执行完了 退出循环
        if (linker.taskGraph.isEmpty()) {
            StartStatics.printLog();
        }
    }

    public void addTask(TaskWrapper startTask) {
        taskBlockingDeque.add(startTask);
    }

    public void release() {
        interrupt();
    }


}

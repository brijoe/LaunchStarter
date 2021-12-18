package com.github.brijoe.starter;

import android.support.annotation.UiThread;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Starter 启动器内部实现， 各个组件通过此类共享资源
 */
class Linker {
    protected AtomicInteger needWaitTaskCounter = new AtomicInteger(0);
    protected AtomicInteger mainThreadTaskCounter = new AtomicInteger(0);
    protected TaskGraph<TaskWrapper> taskGraph = new TaskGraph<>();

    protected TaskDispatcher taskDispatcher = new TaskDispatcher(this);
    protected TaskTracker taskTracker = new TaskTracker(this);
    protected TaskExecutor taskExecutor = new TaskExecutor(this);


    @UiThread
    public void init(Map<Class<? extends StartTask>, TaskWrapper> map) {
        collectionTasks(map);
        taskTracker.start();
        taskDispatcher.start();
        taskExecutor.await();
    }

    private void collectionTasks(Map<Class<? extends StartTask>, TaskWrapper> map) {

        for (TaskWrapper startTask : map.values()) {
            //add Node
            taskGraph.addNode(startTask);
            //主线程
            if (startTask.runInMainThread()) {
                mainThreadTaskCounter.incrementAndGet();
            }
            //需要等待
            if (startTask.needWait()) {
                needWaitTaskCounter.incrementAndGet();
            }
        }
        StarterLog.d(String.format("mainThreadTaskCounter=%s,needWaitTaskCounter=%s",
                mainThreadTaskCounter.get(), needWaitTaskCounter.get()));
        for (Map.Entry<Class<? extends StartTask>, TaskWrapper> entry : map.entrySet()) {
            TaskWrapper startTask = entry.getValue();
            List<Class<? extends StartTask>> dependsOnList = startTask.dependsOn();
            //add Edge
            if (dependsOnList != null) {
                for (Class<? extends StartTask> taskClz : dependsOnList) {
                    taskGraph.addEdge(map.get(taskClz), startTask);
                }
            }
        }
        taskGraph.printGraph();

    }

}

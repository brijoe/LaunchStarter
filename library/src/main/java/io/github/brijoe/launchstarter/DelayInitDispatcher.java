package io.github.brijoe.launchstarter;

import android.os.Looper;
import android.os.MessageQueue;


import java.util.LinkedList;
import java.util.Queue;

import io.github.brijoe.launchstarter.task.DispatchRunnable;
import io.github.brijoe.launchstarter.task.Task;

public class DelayInitDispatcher {

    private Queue<Task> mDelayTasks = new LinkedList<>();

    private MessageQueue.IdleHandler mIdleHandler = new MessageQueue.IdleHandler() {
        @Override
        public boolean queueIdle() {
            if(mDelayTasks.size()>0){
                Task task = mDelayTasks.poll();
                new DispatchRunnable(task).run();
            }
            return !mDelayTasks.isEmpty();
        }
    };

    public DelayInitDispatcher addTask(Task task){
        mDelayTasks.add(task);
        return this;
    }

    public void start(){
        Looper.myQueue().addIdleHandler(mIdleHandler);
    }

}

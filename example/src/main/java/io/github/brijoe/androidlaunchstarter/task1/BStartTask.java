package io.github.brijoe.androidlaunchstarter.task1;

import android.os.SystemClock;

import com.github.brijoe.starter.StartTask;

import java.util.List;


/**
 * B 任务 依赖于 A
 */
public class BStartTask extends StartTask {
    @Override
    public void run() {
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends StartTask>> dependsOn() {
        dependsList.add(AStartTask.class);
        return dependsList;
    }
}

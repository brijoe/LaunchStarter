package io.github.brijoe.androidlaunchstarter.task1;

import android.os.SystemClock;

import com.github.brijoe.starter.StartTask;

import java.util.List;


/**
 * C 任务依赖于B
 */
public class CStartTask extends StartTask {
    @Override
    public void run() {
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends StartTask>> dependsOn() {
        dependsList.add(BStartTask.class);
        dependsList.add(DStartTask.class);
        return dependsList;
    }
}

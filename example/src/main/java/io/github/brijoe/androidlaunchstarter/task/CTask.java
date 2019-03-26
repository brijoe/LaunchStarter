package io.github.brijoe.androidlaunchstarter.task;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import io.github.brijoe.launchstarter.task.Task;

/**
 * C 任务依赖于B
 */
public class CTask extends Task {
    @Override
    public void run() {
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> list = new ArrayList<>();
        list.add(BTask.class);
        return list;
    }
}

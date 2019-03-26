package io.github.brijoe.androidlaunchstarter.task;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import io.github.brijoe.launchstarter.task.Task;

/**
 * B 任务 依赖于 A
 */
public class BTask extends Task {
    @Override
    public void run() {
        SystemClock.sleep(1000);
    }

    @Override
    public List<Class<? extends Task>> dependsOn() {
        List<Class<? extends Task>> list = new ArrayList<>();
        list.add(ATask.class);
        return list;
    }
}

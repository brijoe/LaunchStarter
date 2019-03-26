package io.github.brijoe.androidlaunchstarter.task;

import android.os.SystemClock;

import io.github.brijoe.launchstarter.task.Task;

public class ATask extends Task {
    @Override
    public void run() {
        SystemClock.sleep(1000);
    }
}

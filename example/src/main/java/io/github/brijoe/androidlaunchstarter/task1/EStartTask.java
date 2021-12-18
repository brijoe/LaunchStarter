package io.github.brijoe.androidlaunchstarter.task1;

import android.os.SystemClock;

import com.github.brijoe.starter.StartTask;


public class EStartTask extends StartTask {

    @Override
    public boolean runInMainThread() {
        return true;
    }

    @Override
    public void run() {
        SystemClock.sleep(1 * 1000);
    }
}

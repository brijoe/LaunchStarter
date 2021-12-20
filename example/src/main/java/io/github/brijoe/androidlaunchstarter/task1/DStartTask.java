package io.github.brijoe.androidlaunchstarter.task1;

import android.os.SystemClock;

import com.github.brijoe.starter.StartTask;


public class DStartTask extends StartTask {

    @Override
    public boolean runInMainThread() {
        return false;
    }

    @Override
    public boolean needWaitFinished() {
        return true;
    }

    @Override
    public void run() {
        SystemClock.sleep(1000);
    }
}

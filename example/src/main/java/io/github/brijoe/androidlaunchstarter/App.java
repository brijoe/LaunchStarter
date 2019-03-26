package io.github.brijoe.androidlaunchstarter;

import android.app.Application;
import android.content.Context;

import io.github.brijoe.androidlaunchstarter.task.ATask;
import io.github.brijoe.androidlaunchstarter.task.BTask;
import io.github.brijoe.androidlaunchstarter.task.CTask;
import io.github.brijoe.androidlaunchstarter.task.DTask;
import io.github.brijoe.androidlaunchstarter.task.ETask;
import io.github.brijoe.launchstarter.TaskDispatcher;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化dispatcher
        TaskDispatcher.init(this);
        TaskDispatcher dispatcher = TaskDispatcher.createInstance();

        //添加任务,并调用start方法启动
        dispatcher.addTask(new ATask())
                .addTask(new BTask())
                .addTask(new CTask())
                .addTask(new DTask())
                .addTask(new ETask())
                .start();
    }
}

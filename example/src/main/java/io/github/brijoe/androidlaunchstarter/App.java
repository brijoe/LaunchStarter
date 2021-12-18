package io.github.brijoe.androidlaunchstarter;

import android.app.Application;
import android.content.Context;

import com.github.brijoe.starter.Starter;
import com.github.brijoe.starter.StarterLog;

import io.github.brijoe.androidlaunchstarter.task1.AStartTask;
import io.github.brijoe.androidlaunchstarter.task1.BStartTask;
import io.github.brijoe.androidlaunchstarter.task1.CStartTask;
import io.github.brijoe.androidlaunchstarter.task1.DStartTask;
import io.github.brijoe.androidlaunchstarter.task1.EStartTask;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        //初始化dispatcher
//        TaskDispatcher.init(this);
//        TaskDispatcher dispatcher = TaskDispatcher.createInstance();
//
//        //添加任务,并调用start方法启动
//        dispatcher.addTask(new ATask())
//                .addTask(new BTask())
//                .addTask(new CTask())
//                .addTask(new DTask())
//                .addTask(new ETask())
//                .start();
        Starter.INSTANCE.addTask(new AStartTask())
                .addTask(new BStartTask())
                .addTask(new CStartTask())
                .addTask(new DStartTask())
                .addTask(new EStartTask())
                .start();

        StarterLog.d("Start执行关闭");

    }
}

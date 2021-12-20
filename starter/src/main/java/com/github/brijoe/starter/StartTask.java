package com.github.brijoe.starter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class StartTask implements Runnable {

    private final String TAG = getClass().getSimpleName();

    protected List<Class<? extends StartTask>> dependsList = new CopyOnWriteArrayList<>();

    /**
     * 是否运行在主线程,默认为主线程 否则运行在work线程
     *
     * @return true/false
     */
    public boolean runInMainThread() {
        return true;
    }


    /**
     * 是否 需要等待当前执行完成后 启动器才能返回 默认不需要,如果是主线程则设置无效
     *
     * @return true/false
     */
    public boolean needWaitFinished() {
        return false;
    }


    /**
     * 依赖哪些任务,必须定义成 Class 类型 并用泛型约束,默认为空
     * 子类覆写这个方法，添加自定义任务
     *
     * @return list
     */
    public List<Class<? extends StartTask>> dependsOn() {
        return new ArrayList<>(dependsList);
    }


    /**
     * 子类需要覆写 run方法实现自定义业务逻辑
     */
    @Override
    public abstract void run();


    public String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return "StartTask{" +
                "TAG='" + TAG + '\'' +
                ",runInMainThread=" + runInMainThread() +
                ",needWaitFinished=" + needWaitFinished() +
                ", dependsList=" + dependsList +
                '}';
    }
}

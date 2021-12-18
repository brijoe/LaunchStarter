package com.github.brijoe.starter;

interface StarterListener {
    /**
     * 调度器启动
     */
    void onLaunch();

    /**
     * 调度器完成返回
     */
    void onFinish();

    /**
     * 所有任务完成调度
     */
    void onAllTaskFinished();
}

package com.github.brijoe.starter;

/**
 * 负责任务事件通知
 */
interface TaskListener {
    void onTaskStart(TaskWrapper task);

    void onTaskFinish(TaskWrapper startTask);
}

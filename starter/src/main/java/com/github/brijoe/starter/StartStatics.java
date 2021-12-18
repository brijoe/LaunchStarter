package com.github.brijoe.starter;

import java.util.ArrayList;
import java.util.List;

public class StartStatics {

    private static List<TaskWrapper> dispatchLog = new ArrayList<>();

    public static void printLog() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (TaskWrapper task : dispatchLog) {
            sb.append(task.getTaskName()).append(",");
        }
        sb.append("]");
        StarterLog.d("调度记录 " + sb.toString());
    }

    public static void record(TaskWrapper task) {
        dispatchLog.add(task);
    }
}


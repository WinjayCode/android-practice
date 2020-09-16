package com.winjay.practice.activity_manager;

public class AMProcessInfo {
    /**
     * 进程id  Android规定android.system.uid=1000
     */
    private String pid;
    /**
     * 进程所在的用户id ，即该进程是由谁启动的 root/普通用户等
     */
    private String uid;
    /**
     * 进程占用的内存大小,单位为kb
     */
    private String memorySize;
    /**
     * 进程名
     */
    private String processName;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(String memorySize) {
        this.memorySize = memorySize;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }
}

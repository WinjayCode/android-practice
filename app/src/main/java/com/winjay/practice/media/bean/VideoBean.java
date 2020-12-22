package com.winjay.practice.media.bean;

import java.io.Serializable;

public class VideoBean implements Serializable {
    private static final long serialVersionUID = 92311656525719156L;

    private String path;
    private String title;
    private String duration;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

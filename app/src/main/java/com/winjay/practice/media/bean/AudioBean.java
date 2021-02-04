package com.winjay.practice.media.bean;

import java.io.Serializable;

public class AudioBean implements Serializable {
    private static final long serialVersionUID = 7648068075371842524L;

    private String path;
    private String title;
    private String duration;
    private String displayName;

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

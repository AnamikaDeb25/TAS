package com.TAS.tas;

public class ModelAnnouncement {
    public ModelAnnouncement() {
    }

    String name, url, uid, announcement,time,seen;
    long delete;

    public long getDelete() {
        return delete;
    }

    public void setDelete(long delete) {
        this.delete = delete;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public ModelAnnouncement(String name, String url, String uid, String announcement, String time,String seen,long delete) {
        this.name = name;
        this.url = url;
        this.uid = uid;
        this.announcement = announcement;
        this.time = time;
        this.seen = seen;
this.delete = delete;

    }
}

package com.fight2.entity;

public class Guild {
    private int id;
    private String name;
    private String qq;
    private String notice;
    private User president;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(final String qq) {
        this.qq = qq;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(final String notice) {
        this.notice = notice;
    }

    public User getPresident() {
        return president;
    }

    public void setPresident(final User president) {
        this.president = president;
    }

}

package com.fight2.entity;

import java.util.Set;

public class Guild {
    private int id;
    private String name;
    private String qq;
    private String notice;
    private boolean pollEnabled;
    private User president;
    private Set<Integer> arenaUsers;

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

    public boolean isPollEnabled() {
        return pollEnabled;
    }

    public void setPollEnabled(final boolean pollEnabled) {
        this.pollEnabled = pollEnabled;
    }

    public User getPresident() {
        return president;
    }

    public void setPresident(final User president) {
        this.president = president;
    }

    public Set<Integer> getArenaUsers() {
        return arenaUsers;
    }

    public void setArenaUsers(final Set<Integer> arenaUsers) {
        this.arenaUsers = arenaUsers;
    }

}

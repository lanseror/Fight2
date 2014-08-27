package com.fight2.entity;

public class UserArenaRecord {
    private User user;
    private UserArenaRecordStatus status;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public UserArenaRecordStatus getStatus() {
        return status;
    }

    public void setStatus(final UserArenaRecordStatus status) {
        this.status = status;
    }

    public enum UserArenaRecordStatus {
        NoAction,
        Win,
        Lose;
    }
}

package com.fight2.entity;

public class QuestTask {
    private int id;

    private String title;

    private String dialog;

    private String bossDialog;

    private String tips;

    private int x;

    private int y;

    private UserTaskStatus status = UserTaskStatus.End;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(final String dialog) {
        this.dialog = dialog;
    }

    public String getBossDialog() {
        return bossDialog;
    }

    public void setBossDialog(final String bossDialog) {
        this.bossDialog = bossDialog;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(final String tips) {
        this.tips = tips;
    }

    public int getX() {
        return x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public UserTaskStatus getStatus() {
        return status;
    }

    public void setStatus(final UserTaskStatus status) {
        this.status = status;
    }

    public enum UserTaskStatus {
        Ready,
        Started,
        Finished,
        End;
    }
}

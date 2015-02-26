package com.fight2.entity;

public class Dialog {
    private int id;
    private String content;
    private Speaker speaker;
    private OrderType orderType;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(final Speaker speaker) {
        this.speaker = speaker;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(final OrderType orderType) {
        this.orderType = orderType;
    }

    public enum OrderType {
        Random,
        Sequence
    }

    public enum Speaker {
        Self,
        NPC,
        OtherPlayer
    }

}

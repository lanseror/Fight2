package com.fight2.entity;

public class Bid {
    private int id;
    private int price;
    private int amount;
    private BidItemType type;
    private Card card;
    private BidStatus status;
    private int version;
    private boolean isMyBid;
    private int remainTime;

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public BidItemType getType() {
        return type;
    }

    public void setType(final BidItemType type) {
        this.type = type;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(final Card card) {
        this.card = card;
    }

    public BidStatus getStatus() {
        return status;
    }

    public void setStatus(final BidStatus status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public boolean isMyBid() {
        return isMyBid;
    }

    public void setMyBid(final boolean isMyBid) {
        this.isMyBid = isMyBid;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(final int remainTime) {
        this.remainTime = remainTime;
    }

    public enum BidItemType {
        ArenaTicket,
        Stamina,
        Card
    }

    public enum BidStatus {
        Started,
        Closed
    }
}

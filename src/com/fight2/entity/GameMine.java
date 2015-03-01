package com.fight2.entity;

public class GameMine {
    private int id;
    private int amount;
    private int row;
    private int col;
    private MineType type;
    private User owner;

    public GameMine() {
        super();
    }

    public GameMine(final GameMine mine) {
        super();
        this.id = mine.getId();
        this.amount = mine.getAmount();
        this.row = mine.getRow();
        this.col = mine.getCol();
        this.type = mine.getType();
        this.owner = mine.getOwner();
    }

    public void update(final GameMine mine) {
        this.id = mine.getId();
        this.amount = mine.getAmount();
        this.row = mine.getRow();
        this.col = mine.getCol();
        this.type = mine.getType();
        this.owner = mine.getOwner();
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(final int amount) {
        this.amount = amount;
    }

    public int getRow() {
        return row;
    }

    public void setRow(final int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(final int col) {
        this.col = col;
    }

    public MineType getType() {
        return type;
    }

    public void setType(final MineType type) {
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    public enum MineType {
        Wood(2, 0, "木"),
        Mineral(1, 0, "矿石"),
        Crystal(1, 0, "水晶"),
        Diamon(1, 0, "钻石");

        private final int xOffset;
        private final int yOffset;
        private final String desc;

        private MineType(final int xOffset, final int yOffset, final String desc) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.desc = desc;
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }

        public String getDesc() {
            return desc;
        }

    }
}

package com.fight2.entity;

public class GameMine {
    private int id;
    private int row;
    private int col;
    private MineType type;
    private User owner;

    public GameMine() {
        super();
    }

    public GameMine(final int col, final int row, final MineType type) {
        super();
        this.col = col;
        this.row = row;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
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
        Wood(2, 0),
        Mineral(1, 0),
        Crystal(1, 0);

        private final int xOffset;
        private final int yOffset;

        private MineType(final int xOffset, final int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }

    }
}

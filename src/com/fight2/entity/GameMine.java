package com.fight2.entity;

public class GameMine {
    private int row;
    private int col;
    private MineType type;

    public GameMine() {
        super();
    }

    public GameMine(final int col, final int row, final MineType type) {
        super();
        this.col = col;
        this.row = row;
        this.type = type;
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

    public enum MineType {
        Wood(2, 0),
        Mineral(1, 1),
        Crystal(1,  1);

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

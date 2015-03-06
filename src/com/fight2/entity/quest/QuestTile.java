package com.fight2.entity.quest;

public class QuestTile {
    private int row;
    private int col;
    private TileItem item;

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

    public TileItem getItem() {
        return item;
    }

    public void setItem(final TileItem item) {
        this.item = item;
    }

    public enum TileItem {
        Ticket(true),
        Stamina(true),
        CoinBag(true),
        Card(true),
        SummonCharm(true),
        Diamon(true),
        PileOfDiamon(false),
        Wood(false),
        Mineral(false),
        Crystal(false);

        private final boolean inBox;

        private TileItem(final boolean inBox) {
            this.inBox = inBox;
        }

        public boolean isInBox() {
            return inBox;
        }

    }
}

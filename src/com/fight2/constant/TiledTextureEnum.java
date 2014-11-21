package com.fight2.constant;

public enum TiledTextureEnum {
    MAIN_SUMMON_STONE_EFFECT("images/main_summon_stone_effect.png", 8, 1),
    PLAYER("images/player.png", 3, 4),
    HERO("images/horse.png", 9, 8);

    private final String url;
    private final int tileColumns;
    private final int tileRows;

    private TiledTextureEnum(final String url, final int tileColumns, final int tileRows) {
        this.url = url;
        this.tileColumns = tileColumns;
        this.tileRows = tileRows;
    }

    public String getUrl() {
        return url;
    }

    public int getTileColumns() {
        return tileColumns;
    }

    public int getTileRows() {
        return tileRows;
    }

}

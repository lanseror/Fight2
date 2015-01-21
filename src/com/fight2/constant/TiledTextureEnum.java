package com.fight2.constant;

public enum TiledTextureEnum {
    MAIN_SUMMON_STONE_EFFECT("images/main_summon_stone_effect.png", 8, 1),
    PLAYER("images/player.png", 3, 4),
    QUEST_FLAG("images/quest_flag.png", 4, 2),
    ATTACK_EFFECT("images/attack_effect.png", 3, 4),
    HERO2("images/horse2.png", 16, 8),
    HERO("images/horse.png", 9, 8),
    TREASURE_BOX("images/quest_treasure_box.png", 8, 1);

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

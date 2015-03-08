package com.fight2.constant;

public enum TiledTextureEnum {
    PLAYER("images/player.png", 3, 4),
    QUEST_FLAG("images/quest_flag.png", 4, 2),
    ATTACK_EFFECT("images/attack_effect.png", 3, 4),
    CURE_EFFECT("images/cure_effect.png", 3, 4),
    CONFUSE_EFFECT("images/confuse_effect.png", 3, 4),
    BATTLE_MAGIC_ATTACK_EFFECT("images/battle_magic_attack_effect.png", 3, 4),
    BATTLE_SKILL_GOOD("images/battle_skill_good.png", 3, 4),
    HERO2("images/horse2.png", 16, 8),
    HERO("images/horse.png", 9, 8),
    TREASURE_BOX("images/quest_treasure_box.png", 8, 1),
    TREASURE_CRYSTAL("images/quest_treasure_crystal.png", 7, 1),
    TREASURE_PILE_DIAMON("images/quest_treasure_pile_diamon.png", 5, 1),
    MINE_CRYSTAL("images/mine/crystal_mine.png", 7, 1),
    MINE_DIAMON("images/mine/diamon_mine.png", 7, 1),
    UPGRADE_EFFECT_BG("images/upgrade_effect_bg.png", 4, 4);

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

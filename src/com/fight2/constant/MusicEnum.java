package com.fight2.constant;

public enum MusicEnum {
    SUMMON("GOODLUCK.mp3"),
    MAIN_BG("Campaign-Haven.mp3"),
    ARENA_ATTACK("Sword-Draw.mp3"),
    // BATTLE_BG("Battle-Haven.mp3"),
    BATTLE_HIT("HeadHunterMissileHit2.wav"),
    BATTLE_WIN("Win Battle.mp3"),
    BATTLE_LOSE("LoseCastle.mp3");
    private final String url;

    private MusicEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

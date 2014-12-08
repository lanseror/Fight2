package com.fight2.constant;

public enum MusicEnum {
    SUMMON("GOODLUCK.mp3"),
    MAIN_BG("Mainmenu.mp3"),
    ARENA_ATTACK("Sword-Draw.mp3"),
    // BATTLE_BG("Battle-Haven.mp3"),
    BATTLE_HIT("HeadHunterMissileHit2.wav"),
    BATTLE_WIN("Win Battle.mp3"),
    BATTLE_LOSE("LoseCastle.mp3"),
    DOOR("door.mp3"),
    HORSE("HORSE01.ogg"),
    HORSE8("HORSE08.ogg"),
    LOADING("loading.ogg");

    private final String url;

    private MusicEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

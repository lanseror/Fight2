package com.fight2.constant;

public enum SoundEnum {
    SUMMON("GOODLUCK.mp3"),
    ARENA_ATTACK("Sword-Draw.mp3"),
    BATTLE_HIT("HeadHunterMissileHit2.wav"),
    BATTLE_WIN("Win Battle.mp3"),
    BATTLE_LOSE("LoseCastle.mp3"),
    DOOR("door.mp3"),
    HORSE("HORSE01.ogg"),
    HORSE8("HORSE08.ogg"),
    LOADING("Loading.ogg");

    private final String url;

    private SoundEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

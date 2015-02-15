package com.fight2.constant;

public enum SoundEnum {
    SUMMON("GOODLUCK.mp3"),
    ARENA_ATTACK("Sword-Draw.mp3"),
    BATTLE_HIT("attack_hit.wav"),
    BATTLE_CURE("battle_cure.ogg"),
    BATTLE_WIN("Win Battle.mp3"),
    BATTLE_LOSE("LoseCastle.mp3"),
    DOOR("door.mp3"),
    BUTTON_CLICK("BigButtonClick.wav"),
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

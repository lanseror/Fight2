package com.fight2.constant;

public enum SoundEnum {
    SUMMON("summon.ogg"),
    ARENA_ATTACK("Sword-Draw.mp3"),
    BATTLE_HIT("attack_hit.ogg"),
    BATTLE_CURE("battle_cure.ogg"),
    BATTLE_SKILL_ATTACK("battle_skill_attack.ogg"),
    BATTLE_SKILL_CONFUSE("battle_skill_confuse.ogg"),
    BATTLE_SKILL_GOOD("battle_skill_good.ogg"),
    BATTLE_WIN("battle_win.ogg"),
    BATTLE_LOSE("battle_lose.ogg"),
    DOOR("door.mp3"),
    BUTTON_CLICK("BigButtonClick.wav"),
    BUTTON_SMALL("small_button_click.ogg"),
    CARDPACK("cardpack.ogg"),
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

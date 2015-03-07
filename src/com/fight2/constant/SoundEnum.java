package com.fight2.constant;

public enum SoundEnum {
    SUMMON("summon.ogg"),
    ARENA_ATTACK("Sword-Draw.ogg"),
    BATTLE_HIT("attack_hit.ogg"),
    BATTLE_CURE("battle_cure.ogg"),
    BATTLE_SKILL_ATTACK("battle_skill_attack.ogg"),
    BATTLE_SKILL_CONFUSE("battle_skill_confuse.ogg"),
    BATTLE_SKILL_GOOD("battle_skill_good.ogg"),
    BATTLE_WIN("battle_win.mp3"),
    BATTLE_LOSE("battle_lose.ogg"),
    DOOR("door.ogg"),
    BUTTON_CLICK("BigButtonClick.ogg"),
    BUTTON_SMALL("small_button_click.ogg"),
    BUTTON_CLICK2("buttonClick2.ogg"),
    CARDPACK("cardpack.ogg"),
    HORSE("HORSE01.ogg"),
    HORSE8("HORSE08.ogg"),
    TREASURE1("treasure1.ogg"),
    LOADING("Loading.ogg");

    private final String url;

    private SoundEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

package com.fight2.constant;

public enum MusicEnum {
    COMMON_LOADING("CstleTown.mp3"),
    SUMMON("GOODLUCK.mp3"),
    MAIN_BG("Campaign-Haven.mp3");

    private final String url;

    private MusicEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

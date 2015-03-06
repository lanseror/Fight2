package com.fight2.constant;

public enum MusicEnum {
    MAIN_BG("Mainmenu.ogg"),
    QuestBattle("QuestBattle.ogg"),
    ArenaBattle("ArenaBattle.ogg");

    private final String url;

    private MusicEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}

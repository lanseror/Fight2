package com.fight2.constant;

public enum TextureEnum {
    MAIN_BG("images/main_backgroud.png"),
    MAIN_ARENA("images/main_arena.png"),
    MAIN_ARENA_FCS("images/main_arena_fcs.png"),
    MAIN_BILLBOARD("images/main_billboard.png"),
    MAIN_BILLBOARD_FCS("images/main_billboard_fcs.png"),
    MAIN_CONGRESS("images/main_congress.png"),
    MAIN_CONGRESS_FCS("images/main_congress_fcs.png"),
    MAIN_GATE("images/main_gate.png"),
    MAIN_GATE_FCS("images/main_gate_fcs.png"),
    MAIN_GUILD("images/main_guild.png"),
    MAIN_GUILD_FCS("images/main_guild_fcs.png"),
    MAIN_HOTEL("images/main_hotel.png"),
    MAIN_HOTEL_FCS("images/main_hotel_fcs.png"),
    MAIN_HOUSE_CENTER("images/main_house_center.png"),
    MAIN_HOUSE_LEFT("images/main_house_left.png"),
    MAIN_MAIL_BOX("images/main_mail_box.png"),
    MAIN_MAIL_BOX_FCS("images/main_mail_box_fcs.png"),
    MAIN_PEOPLE("images/main_people.png"),
    MAIN_PIGEON("images/main_pigeon.png"),
    MAIN_SUMMON_STONE("images/main_summon_stone.png"),
    MAIN_SUMMON_STONE_FCS("images/main_summon_stone_fcs.png"),
    MAIN_SUNSHINE("images/main_sunshine.png"),
    MAIN_TOWN("images/main_town.png"),
    MAIN_TRAINING_CAMP("images/main_training_camp.png"),
    MAIN_TRAINING_CAMP_FCS("images/main_training_camp_fcs.png"),
    MAIN_TREE("images/main_tree.png");

    private final String url;

    private TextureEnum(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

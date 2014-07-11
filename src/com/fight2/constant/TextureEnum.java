package com.fight2.constant;

public enum TextureEnum {

    // Main scene images.
    MAIN_BG("images/main_backgroud.png", 1136, 640),
    MAIN_ARENA("images/main_arena.png", 422, 252),
    MAIN_ARENA_FCS("images/main_arena_fcs.png", 422, 252),
    MAIN_BILLBOARD("images/main_billboard.png", 264, 228),
    MAIN_BILLBOARD_FCS("images/main_billboard_fcs.png", 264, 228),
    MAIN_CONGRESS("images/main_congress.png", 513, 483),
    MAIN_CONGRESS_FCS("images/main_congress_fcs.png", 513, 483),
    MAIN_GATE("images/main_gate.png", 1136, 198),
    MAIN_GATE_FCS("images/main_gate_fcs.png", 1136, 198),
    MAIN_GUILD("images/main_guild.png", 218, 450),
    MAIN_GUILD_FCS("images/main_guild_fcs.png", 218, 450),
    MAIN_HOTEL("images/main_hotel.png", 270, 234),
    MAIN_HOTEL_FCS("images/main_hotel_fcs.png", 270, 234),
    MAIN_HOUSE_CENTER("images/main_house_center.png", 1136, 640),
    MAIN_HOUSE_LEFT("images/main_house_left.png", 1136, 640),
    MAIN_MAIL_BOX("images/main_mail_box.png", 389, 267),
    MAIN_MAIL_BOX_FCS("images/main_mail_box_fcs.png", 389, 267),
    MAIN_PEOPLE("images/main_people.png", 1136, 640),
    MAIN_PIGEON("images/main_pigeon.png", 1136, 640),
    MAIN_SUMMON_STONE("images/main_summon_stone.png", 345, 230),
    MAIN_SUMMON_STONE_FCS("images/main_summon_stone_fcs.png", 345, 230),
    MAIN_SUNSHINE("images/main_sunshine.png", 1136, 640),
    MAIN_TOWN("images/main_town.png", 1136, 640),
    MAIN_TRAINING_CAMP("images/main_training_camp.png", 387, 219),
    MAIN_TRAINING_CAMP_FCS("images/main_training_camp_fcs.png", 387, 219),
    MAIN_TREE("images/main_tree.png", 1136, 640),

    // Team scene images.
    TEAM_BG("images/main_backgroud.png", 960, 640),
    TEAM_frame("images/team_frame.png", 850, 536),
    TEAM_FRAME_GRID("images/team_frame_grid.png", 490, 98),
    TEAM_BUTTON_ORGANIZE("images/team_button_organize.png", 101, 43);

    private final String url;
    private final float width;
    private final float height;

    private TextureEnum(final String url, final float width, final float height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

}

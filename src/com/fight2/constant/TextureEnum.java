package com.fight2.constant;

public enum TextureEnum {
    // Splash scene images.
    SPLASH_BG("images/common_splash_screen.png", 1136, 640),

    // Common images.
    COMMON_BACK_BUTTON_NORMAL("images/common_back_button.png", 130, 133),
    COMMON_BACK_BUTTON_PRESSED("images/common_back_button_fcs.png", 130, 133),
    COMMON_DEFAULT_AVATAR("images/common_default_avatar.png", 135, 135),
    COMMON_HP_LEFT_RED("images/common_hp_left_red.png", 9, 30),
    COMMON_HP_CENTER_RED("images/common_hp_center_red.png", 1, 30),
    COMMON_HP_RIGHT_RED("images/common_hp_right_red.png", 9, 30),
    COMMON_HP_LEFT_GREEN("images/common_hp_left_green.png", 9, 30),
    COMMON_HP_CENTER_GREEN("images/common_hp_center_green.png", 1, 30),
    COMMON_HP_RIGHT_GREEN("images/common_hp_right_green.png", 9, 30),

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

    // Party scene images.
    PARTY_BG("images/party_backgroud.png", 1136, 640),
    PARTY_TOPBAR("images/party_topbar.png", 725, 73),
    PARTY_FRAME("images/party_frame.png", 783, 492),
    PARTY_FRAME_GRIDS("images/party_frame_grids.png", 618, 148),
    PARTY_NUMBER_1("images/party_number_1.png", 41, 42),
    PARTY_NUMBER_2("images/party_number_2.png", 41, 42),
    PARTY_NUMBER_3("images/party_number_3.png", 41, 42),
    PARTY_EDIT_BUTTON("images/party_edit_button.png", 130, 132),
    PARTY_EDIT_BUTTON_PRESSED("images/party_edit_button_fcs.png", 130, 132),
    PARTY_EDIT_BG("images/party_edit_backgroud.png", 1136, 640),
    PARTY_EDIT_FRAME("images/party_edit_frame.png", 732, 247),
    PARTY_EDIT_FRAME_GRID("images/party_edit_frame_grid.png", 159, 148),
    PARTY_EDIT_SWITCH_BUTTON("images/party_edit_switch_button.png", 131, 133),
    PARTY_EDIT_SWITCH_BUTTON_PRESSED("images/party_edit_switch_button_fcs.png", 131, 133),
    PARTY_ENHANCE_BUTTON("images/party_enhance_button.png", 132, 132),
    PARTY_ENHANCE_BUTTON_PRESSED("images/party_enhance_button_fcs.png", 132, 132),
    PARTY_RECHARGE("images/party_recharge.png", 224, 77),
    PARTY_RECHARGE_PRESSED("images/party_recharge_fcs.png", 224, 77),

    // Summon scene images.
    SUMMON_BG("images/summon_backgroud.jpg", 1136, 640),
    SUMMON_SUMMON_BUTTON("images/summon_summon_button.jpg", 300, 90),

    // Arena scene images.
    ARENA_BG("images/arena_backgroud.png", 1136, 640),
    ARENA_BATTLE("images/arena_battle_button.jpg", 215, 68),

    // Battle scene images.
    BATTLE_BG("images/battle_backgroud.jpg", 1136, 640),
    BATTLE_PARTY_BOTTOM("images/battle_party_bottom.png", 312, 83),
    BATTLE_PARTY_TOP("images/battle_party_top.png", 312, 81),

    TEST_CARD1("card/card1.jpg", 100, 100),
    TEST_CARD2("card/card2.jpg", 100, 100),
    TEST_CARD3("card/card3.jpg", 100, 100);
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

package com.fight2.constant;

public enum TextureEnum {
    // Splash scene images.
    SPLASH_BG("images/common_splash_screen.png", 1138, 640),
    // Common images.
    COMMON_BG("images/common_backgroud.png", 1136, 640),
    COMMON_BACK_BUTTON_NORMAL("images/common_back_button.png", 130, 132),
    COMMON_BACK_BUTTON_PRESSED("images/common_back_button_fcs.png", 130, 132),
    COMMON_CONFIRM_BUTTON_NORMAL("images/common_confirm_button.png", 130, 132),
    COMMON_CONFIRM_BUTTON_PRESSED("images/common_confirm_button_fcs.png", 130, 132),
    COMMON_DEFAULT_AVATAR("images/common_default_avatar.png", 135, 135),
    COMMON_HP_RED("images/common_hp_red.png", 285, 23),
    COMMON_HP_RIGHT_RED("images/common_hp_right_red.png", 10, 23),
    COMMON_HP_RIGHT_GREEN("images/common_hp_right_green.png", 10, 23),
    COMMON_HP_GREEN("images/common_hp_green.png", 285, 23),
    COMMON_CARD_COVER("images/common_card_cover.png", 290, 435),
    COMMON_STAR_1("images/common_star_1.png", 30, 30),
    COMMON_STAR_2("images/common_star_2.png", 57, 30),
    COMMON_STAR_3("images/common_star_3.png", 83, 30),
    COMMON_STAR_4("images/common_star_4.png", 110, 30),
    COMMON_STAR_5("images/common_star_5.png", 137, 30),
    COMMON_STAR_6("images/common_star_6.png", 164, 30),
    COMMON_ARENA_TICKET("images/common_arena_ticket.png", 109, 76),
    COMMON_STAMINA("images/common_stamina.png", 120, 138),
    COMMON_COIN("images/common_coin.png", 45, 42),
    COMMON_COIN_BAG("images/common_coin_bag.png", 120, 114),
    COMMON_MIGHT_ICON("images/common_might_icon.png", 44, 44),
    COMMON_GUILD_CONTRIBUTION("images/common_guild_contribution.png", 36, 36),
    COMMON_STAMINA_BOX("images/common_stamina_box.png", 262 * 0.8f, 65 * 0.8f),
    COMMON_STAMINA_STICK("images/common_stamina_stick.png", 203 * 0.8f, 37 * 0.8f),
    COMMON_STAMINA_STICK_RIGHT("images/common_stamina_stick_right.png", 7 * 0.8f, 37 * 0.8f),
    COMMON_EXPERIENCE_BOX("images/common_experience_box.png", 267, 67),
    COMMON_EXPERIENCE_BOX_STAR("images/common_experience_box_star.png", 267, 67),
    COMMON_EXPERIENCE_STICK("images/common_experience_stick.png", 205, 67),
    COMMON_COIN_BOX("images/common_coin_box.png", 208, 52),
    COMMON_BUTTON("images/common_button.png", 129, 60),
    COMMON_INPUT_ICON("images/common_input_icon.png", 54, 53),
    COMMON_CHECKBOX_ON("images/common_checkbox_on.png", 40, 40),
    COMMON_CARD_FRAME_HUMAN("images/common_card_frame_human.png", 299, 450),
    COMMON_CARD_FRAME_ANGEL("images/common_card_frame_angel.png", 299, 450),
    COMMON_CARD_FRAME_DEVIL("images/common_card_frame_devil.png", 299, 450),
    COMMON_CARD_FRAME_ELF("images/common_card_frame_elf.png", 315, 455),
    COMMON_CARD_FRAME_TIER_GRID_HUMAN("images/common_card_frame_tier_grid_human.png", 36, 12),
    COMMON_CARD_FRAME_TIER_GRID_HUMAN_2("images/common_card_frame_tier_grid_human_2.png", 72, 12),
    COMMON_CARD_FRAME_TIER_STICK_HUMAN("images/common_card_frame_tier_stick_human.png", 34, 11),
    COMMON_CARD_FRAME_TIER_STICK_HUMAN_2("images/common_card_frame_tier_stick_human_2.png", 70, 11),
    COMMON_CARD_FRAME_TIER_STICK_HUMAN_3("images/common_card_frame_tier_stick_human_3.png", 107, 11),
    COMMON_CARD_FRAME_TIER_GRID_ANGEL("images/common_card_frame_tier_grid_angel.png", 36, 12),
    COMMON_CARD_FRAME_TIER_GRID_ANGEL_2("images/common_card_frame_tier_grid_angel_2.png", 72, 12),
    COMMON_CARD_FRAME_TIER_STICK_ANGEL("images/common_card_frame_tier_stick_angel.png", 34, 11),
    COMMON_CARD_FRAME_TIER_STICK_ANGEL_2("images/common_card_frame_tier_stick_angel_2.png", 70, 11),
    COMMON_CARD_FRAME_TIER_STICK_ANGEL_3("images/common_card_frame_tier_stick_angel_3.png", 105, 11),
    COMMON_CARD_FRAME_TIER_GRID_ELF("images/common_card_frame_tier_grid_elf.png", 35, 11),
    COMMON_CARD_FRAME_TIER_GRID_ELF_2("images/common_card_frame_tier_grid_elf_2.png", 72, 11),
    COMMON_CARD_FRAME_TIER_STICK_ELF("images/common_card_frame_tier_stick_elf.png", 34, 11),
    COMMON_CARD_FRAME_TIER_STICK_ELF_2("images/common_card_frame_tier_stick_elf_2.png", 71, 11),
    COMMON_CARD_FRAME_TIER_STICK_ELF_3("images/common_card_frame_tier_stick_elf_3.png", 107, 11),
    COMMON_CARD_FRAME_TIER_GRID_DEVIL("images/common_card_frame_tier_grid_devil.png", 36, 12),
    COMMON_CARD_FRAME_TIER_GRID_DEVIL_2("images/common_card_frame_tier_grid_devil_2.png", 72, 12),
    COMMON_CARD_FRAME_TIER_STICK_DEVIL("images/common_card_frame_tier_stick_devil.png", 34, 11),
    COMMON_CARD_FRAME_TIER_STICK_DEVIL_2("images/common_card_frame_tier_stick_devil_2.png", 71, 11),
    COMMON_CARD_FRAME_TIER_STICK_DEVIL_3("images/common_card_frame_tier_stick_devil_3.png", 107, 11),
    COMMON_HP_ICON("images/common_hp_icon.png", 39, 33),
    COMMON_ATK_ICON("images/common_atk_icon.png", 45, 35),
    COMMON_ALERT_FRAME("images/common_alert_frame.png", 500, 236),
    COMMON_DIAMOND("images/common_diamond.png", 40, 32),
    COMMON_SUMMON_STONE("images/common_summon_stone.png", 25, 27),
    COMMON_SUMMON_STONE_BIG("images/common_summon_stone_big.png", 130, 139),
    COMMON_SUMMON_CHARM("images/common_summon_charm.png", 31, 33),
    COMMON_CLOSE_BUTTON("images/common_close_button.png", 43, 43),
    COMMON_LOADING("images/common_loading2.png", 100, 100),
    COMMON_AVATAR_FRAME("images/common_avatar_frame.png", 208, 208),
    COMMON_CARD_FRAME("images/common_card_frame.png", 400, 600),
    COMMON_PROGRESS_BAR("images/common_progress_bar.png", 868, 22),
    COMMON_PROGRESS_BAR_RIGHT("images/common_progress_bar_right.png", 9, 22),
    COMMON_GUILD_CONTRIB_BAR("images/common_guild_contrib_bar.png", 208, 52),
    COMMON_PARTICLE_POINT("images/particle_point.png", 32, 32),
    COMMON_ITEM_GRID("images/common_item_grid.png", 135, 138),

    // Chat room images.
    CHAT_INPUT_BG("images/chat_input_backgroud.png", 850, 82),
    CHAT_INPUT_OPEN("images/chat_input_open.png", 84, 83),
    CHAT_INPUT_OPEN_FCS("images/chat_input_open_fcs.png", 84, 83),
    CHAT_INPUT_SEND("images/chat_input_send.png", 141, 82),

    // Main scene images.
    MAIN_BG("images/main_backgroud.png", 1136, 640),
    MAIN_ARENA("images/main_arena.png", 422, 252),
    MAIN_ARENA_FCS("images/main_arena_fcs.png", 422, 252),
    MAIN_BILLBOARD("images/main_billboard.png", 264, 228),
    // MAIN_BILLBOARD_FCS("images/main_billboard_fcs.png", 264, 228),
    MAIN_CONGRESS("images/main_congress.png", 513, 483),
    MAIN_CONGRESS_FCS("images/main_congress_fcs.png", 513, 483),
    MAIN_GATE("images/main_gate.png", 1136, 198),
    MAIN_GATE_FCS("images/main_gate_fcs.png", 1136, 198),
    MAIN_GUILD("images/main_guild.png", 218, 450),
    MAIN_GUILD_FCS("images/main_guild_fcs.png", 218, 450),
    MAIN_HOTEL("images/main_hotel.png", 270, 234),
    MAIN_HOTEL_FCS("images/main_hotel_fcs.png", 270, 234),
    MAIN_HOUSE_CENTER("images/main_house_center.png", 1136, 640),
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
    MAIN_STOREROOM("images/main_storeroom.png", 201, 441),
    MAIN_STOREROOM_FCS("images/main_storeroom_fcs.png", 201, 441),
    MAIN_TIPS("images/main_tips_3.png", 150, 35),
    MAIN_TIPS2("images/main_tips_5.png", 115, 35),
    MAIN_MSG("images/main_msg.png", 419, 600),
    MAIN_MSG_SMALL("images/main_msg_small.png", 100, 110),
    MAIN_MSG_NEW_SMALL("images/main_msg_new_small.png", 100, 110),
    MAIN_PLAYER_INFO("images/main_player_info.png", 308, 138),
    MAIN_PLAYER_INFO_STAMINA("images/main_player_info_stamina.png", 160, 26),
    MAIN_PLAYER_INFO_STAMINA_BOX("images/main_player_info_stamina_box.png", 184, 35),

    // Party scene images.
    PARTY_TOPBAR("images/party_topbar.png", 725, 73),
    PARTY_FRAME("images/party_frame.png", 783, 492),
    PARTY_FRAME_GRIDS("images/party_frame_grids.png", 618, 148),
    PARTY_NUMBER_1("images/party_number_1.png", 41, 42),
    PARTY_NUMBER_2("images/party_number_2.png", 41, 42),
    PARTY_NUMBER_3("images/party_number_3.png", 41, 42),
    PARTY_EDIT_BUTTON("images/party_edit_button.png", 130, 132),
    PARTY_EDIT_BUTTON_PRESSED("images/party_edit_button_fcs.png", 130, 132),
    PARTY_EDIT_FRAME("images/party_edit_frame.png", 732, 247),
    PARTY_EDIT_FRAME_GRID("images/party_edit_frame_grid.png", 159, 148),
    PARTY_EDIT_COVER_LEFT("images/party_edit_cover_left.png", 180, 270),
    PARTY_EDIT_COVER_RIGHT("images/party_edit_cover_right.png", 430, 270),
    PARTY_EDIT_SWITCH_BUTTON("images/party_edit_switch_button.png", 130, 132),
    PARTY_EDIT_SWITCH_BUTTON_PRESSED("images/party_edit_switch_button_fcs.png", 130, 132),
    PARTY_ENHANCE_BUTTON("images/party_enhance_button.png", 130, 132),
    PARTY_ENHANCE_BUTTON_PRESSED("images/party_enhance_button_fcs.png", 130, 132),
    PARTY_RECHARGE("images/party_recharge.png", 182, 46),
    PARTY_RECHARGE_PRESSED("images/party_recharge_fcs.png", 182, 46),

    // Card upgrade scene images.
    UPGRADE_FRAME("images/upgrade_frame.png", 746, 368),
    UPGRADE_FRAME_BUTTON("images/upgrade_frame_button.png", 268, 69),
    UPGRADE_FRAME_BUTTON_FCS("images/upgrade_frame_button_fcs.png", 268, 69),
    UPGRADE_EVO_BUTTON("images/upgrade_evo_button.png", 130, 132),
    UPGRADE_EVO_BUTTON_PRESSED("images/upgrade_evo_button_fcs.png", 130, 132),

    // Card evolution scene images.
    EVOLUTION_FRAME("images/evolution_frame.png", 746, 368),
    EVOLUTION_FRAME_BUTTON("images/evolution_frame_button.png", 268, 69),
    EVOLUTION_FRAME_BUTTON_FCS("images/evolution_frame_button_fcs.png", 268, 69),
    EVOLUTION_ICON("images/evolution_icon.png", 92, 45),

    // Summon scene images.
    SUMMON_TOPBAR("images/summon_topbar.png", 614, 77),
    SUMMON_FRAME("images/summon_frame.png", 700, 333),
    SUMMON_BUTTON("images/summon_button.png", 261, 61),
    SUMMON_BUTTON_FCS("images/summon_button_fcs.png", 261, 61),

    // PlayerInfo scene images.
    PLAYERINFO_FRAME("images/playerinfo_frame.png", 491, 469),

    // CardInfo scene images.
    CARDINFO_FRAME("images/cardinfo_frame.png", 491, 469),
    CARDINFO_TIER_GRID("images/cardinfo_tier_grid.png", 80, 25),
    CARDINFO_TIER_STICK("images/cardinfo_tier_stick.png", 84, 29),
    CARDINFO_COMBO_FRAME("images/cardinfo_combo_frame.png", 810, 52),

    // Combo scene images.
    COMBO_FRAME("images/combo_frame.png", 758, 296),

    // Guild scene images.
    GUILD_FRAME("images/guild_frame.png", 851, 495),
    GUILD_TOPBAR("images/guild_topbar.png", 454, 69),
    GUILD_OPTION_BG("images/guild_option_backgroud.png", 805, 37),
    GUILD_OPTION_LINE("images/guild_option_line.png", 2, 32),
    GUILD_SCROLL_ROW_SEPARATOR("images/guild_scroll_row_separator.png", 748, 2),
    GUILD_FRAME_SCROLLBAR("images/guild_frame_scrollbar.png", 23, 332),
    GUILD_FRAME_SCROLLSTICK("images/guild_frame_scrollstick.png", 23, 332),
    GUILD_FRAME_BUTTON_FCS("images/guild_frame_button_fcs.png", 116, 39),
    GUILD_FRAME_BUTTON("images/guild_frame_button.png", 116, 39),

    // Arena scene images.
    ARENA_TOPBAR("images/arena_topbar.png", 751, 73),
    ARENA_BATTLE_FRAME("images/arena_battle_frame.png", 242, 311),
    ARENA_BATTLE_BUTTON("images/arena_battle_button.png", 229, 75),
    ARENA_BATTLE_BUTTON_FCS("images/arena_battle_button_fcs.png", 229, 75),
    ARENA_BATTLE_INFO("images/arena_battle_info.png", 740, 181),
    ARENA_BATTLE_REFRESH("images/arena_battle_refresh.png", 130, 132),
    ARENA_BATTLE_REFRESH_FCS("images/arena_battle_refresh_fcs.png", 130, 132),
    ARENA_BATTLE_RANKING("images/arena_battle_ranking.png", 130, 132),
    ARENA_BATTLE_RANKING_FCS("images/arena_battle_ranking_fcs.png", 130, 132),
    ARENA_BATTLE_REWARD("images/arena_battle_reward.png", 130, 132),
    ARENA_BATTLE_REWARD_FCS("images/arena_battle_reward_fcs.png", 130, 132),
    ARENA_BATTLE_CONTINUOUS_WIN("images/arena_battle_continuous_win.png", 121, 118),
    ARENA_RESULT_WIN("images/arena_result_win.png", 218, 44),
    ARENA_RESULT_LOSE("images/arena_result_lose.png", 218, 44),

    // Arena list scene images.
    ARENA_LIST_FRAME("images/arena_list_frame.png", 790, 489),
    ARENA_LIST_LINE("images/arena_list_line.png", 515, 1),
    ARENA_LIST_SELECTED("images/arena_list_selected.png", 527, 35),
    ARENA_LIST_ENTER("images/arena_list_enter.png", 178, 98),
    ARENA_LIST_ENTER_PRESSED("images/arena_list_enter_fcs.png", 178, 98),

    // Arena reward scene images.
    ARENA_REWARD_BG("images/arena_reward_backgroud.png", 605, 555),
    ARENA_REWARD_MIGHT_BUTTON("images/arena_reward_might_button.png", 557, 60),
    ARENA_REWARD_RANK_BUTTON("images/arena_reward_rank_button.png", 557, 60),
    ARENA_REWARD_DESC("images/arena_reward_desc.png", 582, 181),
    ARENA_REWARD_GRID("images/arena_reward_grid.png", 582, 344),
    ARENA_REWARD_MIGHT_POINT("images/arena_reward_might_point.png", 88, 87),

    // Arena ranking scene images.
    ARENA_RANKING_BG("images/arena_ranking_backgroud.png", 605, 555),
    ARENA_RANKING_NUMBER_1("images/arena_ranking_number_1.png", 41, 50),
    ARENA_RANKING_NUMBER_2("images/arena_ranking_number_2.png", 41, 50),
    ARENA_RANKING_NUMBER_3("images/arena_ranking_number_3.png", 40, 50),

    // Battle scene images.
    BATTLE_QUEST_BG("images/battle_quest_backgroud.png", 1138, 640),
    BATTLE_ARENA_BG("images/battle_arena_backgroud.png", 1138, 640),
    BATTLE_PARTY_BOTTOM("images/battle_party_bottom.png", 312, 83),
    BATTLE_PARTY_TOP("images/battle_party_top.png", 312, 81),
    BATTLE_SKILL_FRAME("images/battle_skill_frame.png", 1124 * 0.9f, 219 * 0.9f),
    BATTLE_WIN("images/battle_win.png", 791, 254),
    BATTLE_LOSE("images/battle_lose.png", 791, 251),
    BATTLE_SKIP("images/battle_skip.png", 142, 101),
    BATTLE_CARD_SKILL_FCS("images/battle_card_skill_fcs.png", 146, 202),
    BATTLE_AVATAR_SKILL_FCS("images/battle_avatar_skill_fcs.png", 74, 74),
    BATTLE_RESULT("images/battle_result.png", 730, 551),
    BATTLE_SKILL_REVIVAL("images/battle_skill_revival.png", 400, 78),
    BATTLE_EFFECT_HIT_RED("images/battle_effect_hit_red.png", 1136, 640),

    // PreBattle scene images.
    PREBATTLE_RETREAT_BUTTON("images/prebattle_retreat_button.png", 130, 132),
    PREBATTLE_RETREAT_BUTTON_FCS("images/prebattle_retreat_button_fcs.png", 130, 132),
    PREBATTLE_BATTLE_BUTTON("images/prebattle_battle_button.png", 130, 132),
    PREBATTLE_BATTLE_BUTTON_FCS("images/prebattle_battle_button_fcs.png", 130, 132),
    PREBATTLE_VS_ICON("images/prebattle_vs_icon.png", 190, 176),
    PREBATTLE_INFO_LEFT("images/prebattle_info_left.png", 283, 84),
    PREBATTLE_INFO_RIGHT("images/prebattle_info_right.png", 283, 84),
    PREBATTLE_NAME_BOX("images/prebattle_name_box.png", 378, 77),
    PREBATTLE_COMBO_SKILL_LEFT("images/prebattle_combo_skill_left.png", 285, 122),
    PREBATTLE_COMBO_SKILL_RIGHT("images/prebattle_combo_skill_right.png", 285, 122),

    // Path images.
    PATH_UP("images/path/up.png", 20, 26),
    PATH_UP2RIGHTUP("images/path/up2rightup.png", 18, 26),
    PATH_UP2LEFTUP("images/path/up2leftup.png", 19, 26),
    PATH_RIGHTUP("images/path/rightup.png", 21, 23),
    PATH_RIGHTUP2UP("images/path/rightup2up.png", 20, 26),
    PATH_RIGHTUP2RIGHT("images/path/rightup2right.png", 24, 22),
    PATH_RIGHTDOWN("images/path/rightdown.png", 21, 23),
    PATH_RIGHTDOWN2RIGHT("images/path/rightdown2right.png", 24, 22),
    PATH_RIGHTDOWN2DOWN("images/path/rightdown2down.png", 20, 26),
    PATH_RIGHT("images/path/right.png", 24, 22),
    PATH_RIGHT2RIGHTDOWN("images/path/right2rightdown.png", 24, 20),
    PATH_RIGHT2RIGHTUP("images/path/right2rightup.png", 24, 20),
    PATH_LEFTUP("images/path/leftup.png", 21, 23),
    PATH_LEFTUP2UP("images/path/leftup2up.png", 20, 26),
    PATH_LEFTUP2LEFT("images/path/leftup2left.png", 24, 22),
    PATH_LEFTDOWN("images/path/leftdown.png", 21, 23),
    PATH_LEFTDOWN2LEFT("images/path/leftdown2left.png", 24, 22),
    PATH_LEFTDOWN2DOWN("images/path/leftdown2down.png", 20, 26),
    PATH_LEFT("images/path/left.png", 24, 22),
    PATH_LEFT2LEFTUP("images/path/left2leftup.png", 24, 20),
    PATH_LEFT2LEFTDOWN("images/path/left2leftdown.png", 24, 20),
    PATH_DOWN("images/path/down.png", 20, 26),
    PATH_DOWN2RIGHTDOWN("images/path/down2rightdown.png", 18, 26),
    PATH_DOWN2LEFTDOWN("images/path/down2leftdown.png", 18, 26),

    // Quest images.
    QUEST_TOWN("images/quest_town.png", 80, 83),
    QUEST_TOWN_BIG("town_big.png", 180, 153),
    QUEST_CANCEL_BUTTON("images/quest_cancel_button.png", 130, 132),
    QUEST_CANCEL_BUTTON_FCS("images/quest_cancel_button_fcs.png", 130, 132),
    QUEST_PATH_TAG("images/quest_path_tag.png", 15, 15),
    QUEST_PATH_TAG_LEFT_END("images/quest_path_tag_left_end.png", 30, 26),
    QUEST_PATH_TAG_LEFT_TREASURE("images/quest_path_tag_left_treasure.png", 28, 31),
    QUEST_PATH_TAG_RIGHT_END("images/quest_path_tag_right_end.png", 30, 26),
    QUEST_PATH_TAG_RIGHT_TREASURE("images/quest_path_tag_right_treasure.png", 28, 31),
    QUEST_TREASURE_WOOD("images/quest_treasure_wood.png", 34, 23),
    QUEST_TREASURE_WOOD_BIG("images/quest_treasure_wood_big.png", 68, 46),
    QUEST_TREASURE_MINERAL("images/quest_treasure_mineral.png", 34, 22),
    QUEST_TREASURE_MINERAL_BIG("images/quest_treasure_mineral_big.png", 68, 44),
    QUEST_MINE_MINERAL("images/mine/mineral_mine.png", 93, 64),
    QUEST_MINE_WOOD("images/mine/wood_mine.png", 93, 79),

    // Dialog images.
    DIALOG_FULL("images/dialog_full.png", 902, 602),
    DIALOG_RIGHT("images/dialog_right.png", 52, 602),
    DIALOG_BOTTOM("images/dialog_bottom.png", 902, 49),
    DIALOG_RIGHT_BOTTOM("images/dialog_right_bottom.png", 52, 49);

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

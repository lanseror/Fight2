package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.text.InputType;
import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Guild;
import com.fight2.entity.ScrollZone;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.InputText;
import com.fight2.entity.engine.InputText.OnConfirmListener;
import com.fight2.util.GuildUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class GuildScene extends BaseScene {
    private final static int FRAME_BOTTOM = 90;
    private static int BOARD_WIDTH = 805;
    private static int BOARD_HEIGHT = 370;
    private static int SCROLL_ZONE_WIDTH = 795;
    private static int SCROLL_ZONE_HEIGHT = 323;
    private static int TOUCH_AREA_WIDTH = 790;
    private static int TOUCH_AREA_HEIGHT = 290;
    private final Sprite[] focusedButtons = new Sprite[7];
    private final Sprite[] unfocusedButtons = new Sprite[7];
    private final static String[] NOGUILD_STRINGS = { "公会列表", "创建公会" };
    private final static String[] INGUILD_STRINGS = { "公会信息", "公会排名", "成员列表", "公会仓库", "公会战", "炼金房", "投票" };
    private final static String[] HEADBAR_GUILD_LIST = { "NO.", "公会名", "会长" };
    private final static float[] HBW_GUILD_LIST = { 0.1f, 0.5f, 0.4f };
    private final List<IEntity> boards = new ArrayList<IEntity>();
    private final Sprite frame;

    private final Font buttonFont;
    private final Font infoFont;
    private final Font headBarFont;
    private int focusedIndex = 0;
    private Guild guild;
    private boolean inGuild;

    public GuildScene(final GameActivity activity) throws IOException {
        super(activity);
        this.guild = GuildUtils.getUserGuild();
        inGuild = (this.guild != null);
        buttonFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        infoFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
        headBarFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        frame = createALBImageSprite(TextureEnum.GUILD_FRAME, this.simulatedLeftX, FRAME_BOTTOM);
        this.attachChild(frame);
        // final Sprite optionBackgroud = createALBImageSprite(TextureEnum.GUILD_OPTION_BG, 23, 387);
        // frame.attachChild(optionBackgroud);
        // final Sprite scrollbar = createALBImageSprite(TextureEnum.GUILD_FRAME_SCROLLBAR, 802, 58);
        // frame.attachChild(scrollbar);
        // final Sprite scrollStick = createALBImageSprite(TextureEnum.GUILD_FRAME_SCROLLSTICK, 0, 0);
        // scrollbar.attachChild(scrollStick);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topBar = createALBImageSprite(TextureEnum.GUILD_TOPBAR, this.simulatedLeftX, this.simulatedHeight - TextureEnum.GUILD_TOPBAR.getHeight());
        this.attachChild(topBar);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        createButtons();
        createGuildBoards();

        focusButton(focusedIndex);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private void focusButton(final int i) {
        focusedButtons[focusedIndex].setVisible(false);
        final IEntity unfocusedBoard = boards.get(focusedIndex);
        unfocusedBoard.detachSelf();
        unfocusedButtons[focusedIndex].setVisible(true);
        focusedButtons[i].setVisible(true);
        final IEntity focusedBoard = boards.get(i);
        frame.attachChild(focusedBoard);
        unfocusedButtons[i].setVisible(false);
        focusedIndex = i;

    }

    public Sprite createButton(final TextureEnum textureEnum, final float x, final float y, final int index) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    focusButton(index);
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    private void createButtons() {
        final float buttonWidth = TextureEnum.GUILD_FRAME_BUTTON.getWidth();
        final float buttonHeight = TextureEnum.GUILD_FRAME_BUTTON.getHeight();
        final float buttonGap = 0;
        final String[] buttonTextStrings = inGuild ? INGUILD_STRINGS : NOGUILD_STRINGS;
        for (int i = 0; i < buttonTextStrings.length; i++) {
            final Sprite focusedButton = createALBImageSprite(TextureEnum.GUILD_FRAME_BUTTON_FCS, 20 + i * (buttonWidth + buttonGap), 11);
            frame.attachChild(focusedButton);
            focusedButton.setVisible(false);
            focusedButtons[i] = focusedButton;
            final Sprite unfocusedButton = createButton(TextureEnum.GUILD_FRAME_BUTTON, 20 + i * (buttonWidth + buttonGap), 11, i);
            frame.attachChild(unfocusedButton);
            this.registerTouchArea(unfocusedButton);
            unfocusedButtons[i] = unfocusedButton;
            final Text focusedText = new Text(buttonWidth * 0.5f, buttonHeight * 0.5f, buttonFont, buttonTextStrings[i], vbom);
            focusedButton.attachChild(focusedText);
            final Text unfocusedText = new Text(buttonWidth * 0.5f, buttonHeight * 0.5f, buttonFont, buttonTextStrings[i], vbom);
            unfocusedText.setColor(0XFFF8B551);
            unfocusedButton.attachChild(unfocusedText);
        }
    }

    private IEntity createGuildInfoBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(80, 335, infoFont, "公会名：", vbom);
        board.attachChild(guildNameTitle);
        final Text guildName = new Text(220, 335, infoFont, guild.getName(), vbom);
        board.attachChild(guildName);
        this.leftAlignEntity(guildName, 140);
        final Text presidentNameTitle = new Text(80, 295, infoFont, "会   长 ：", vbom);
        board.attachChild(presidentNameTitle);
        final Text presidentName = new Text(220, 295, infoFont, guild.getPresident().getName(), vbom);
        board.attachChild(presidentName);
        this.leftAlignEntity(presidentName, 140);
        final Text qqTitle = new Text(430, 335, infoFont, "QQ群 ：", vbom);
        board.attachChild(qqTitle);
        final InputText qq = new InputText(600, 335, 210, 40, guild.getQq(), "输入QQ群号码", 12, InputType.TYPE_CLASS_NUMBER, infoFont, this, true, false, 0);
        board.attachChild(qq);
        qq.setConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm() {
                guild.setQq(qq.getText());
                activity.runOnUpdateThread(new Runnable() {

                    @Override
                    public void run() {
                        GuildUtils.editGuild(guild);
                    }

                });
            }
        });
        final Text rankingTitle = new Text(430, 295, infoFont, "排  名：", vbom);
        board.attachChild(rankingTitle);
        final Text ranking = new Text(510, 295, infoFont, "1", vbom);
        board.attachChild(ranking);
        final Text noticeTitle = new Text(80, 255, infoFont, "公   告 ：", vbom);
        board.attachChild(noticeTitle);
        final InputText notice = new InputText(435, 195, 590, 140, guild.getNotice(), "输入公告", 75, InputType.TYPE_CLASS_TEXT, infoFont, this, true, true, 6);
        board.attachChild(notice);
        notice.setConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm() {
                guild.setNotice(notice.getText());
                activity.runOnUpdateThread(new Runnable() {

                    @Override
                    public void run() {
                        GuildUtils.editGuild(guild);

                    }

                });

            }
        });
        return board;
    }

    private IEntity createGuildRankingBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "公会排名", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createMemberListBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "成员列表", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createGuildWarehouseBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "公会仓库", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createGuildBattleBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "公会战", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createGuildFactoryBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "炼金房", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createGuildVotingBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "投票", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private void createGuildBoards() {
        if (inGuild) {
            boards.add(createGuildInfoBoard());
            boards.add(createGuildListBoard());
            // boards.add(createGuildRankingBoard());
            boards.add(createMemberListBoard());
            boards.add(createGuildWarehouseBoard());
            boards.add(createGuildBattleBoard());
            boards.add(createGuildFactoryBoard());
            boards.add(createGuildVotingBoard());
        } else {
            boards.add(createGuildListBoard());
            boards.add(createGuildApplyBoard());
        }

        frame.attachChild(boards.get(0));
    }

    private void createHeadBar(final String[] headBarStrings, final float[] headBarWidths, final IEntity board) {
        final Sprite optionBackgroud = createALBImageSprite(TextureEnum.GUILD_OPTION_BG, 0, board.getHeight() - TextureEnum.GUILD_OPTION_BG.getHeight());
        board.attachChild(optionBackgroud);
        final float optionBarWidth = optionBackgroud.getWidth();
        final float optionBarHeight = optionBackgroud.getHeight();
        final float optionY = optionBarHeight * 0.5f;
        float optionX = 0;
        for (int i = 0; i < headBarStrings.length; i++) {
            final float optionWidth = optionBarWidth * headBarWidths[i];
            final String title = headBarStrings[i];
            final Text titleText = new Text(optionX + optionWidth * 0.5f, optionY, headBarFont, title, vbom);
            titleText.setColor(0XFFF8B551);
            optionBackgroud.attachChild(titleText);
            optionX += optionWidth;
            if (i < headBarStrings.length - 1) {
                final Sprite optionSeparator = createACImageSprite(TextureEnum.GUILD_OPTION_LINE, optionX, optionY + 2);
                optionBackgroud.attachChild(optionSeparator);
            }
        }

    }

    private IEntity createScrollZone(final IEntity parent) {
        final ClipEntity scrollZone = new ClipEntity(parent.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT);
        scrollZone.setAlpha(0);
        parent.attachChild(scrollZone);
        return scrollZone;
    }

    private IEntity createScrollContainer(final IEntity parent) {
        final IEntity scrollContainer = new Rectangle(parent.getWidth() * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        scrollContainer.setAlpha(0);
        parent.attachChild(scrollContainer);
        return scrollContainer;
    }

    private IEntity createTouchArea(final IEntity board) {
        final IEntity touchArea = new Rectangle(board.getWidth() * 0.5f, 25 + TOUCH_AREA_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT, vbom);
        board.attachChild(touchArea);
        touchArea.setAlpha(0);
        this.registerTouchArea(touchArea);
        return touchArea;
    }

    private IEntity createGuildListBoard() {
        final IEntity board = createBoardBox();
        this.createHeadBar(HEADBAR_GUILD_LIST, HBW_GUILD_LIST, board);
        final List<Guild> guilds = GuildUtils.getTopGuilds();

        final Text guildNameTitle = new Text(257, 225, infoFont, "公会列表", vbom);
        board.attachChild(guildNameTitle);

        final ScrollZone scrollZGone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = createTouchArea(board);
        return board;
    }

    private IEntity createGuildApplyBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(250, 225, infoFont, "公会名称：", vbom);
        board.attachChild(guildNameTitle);
        final InputText guildNameText = new InputText(430, 225, 210, 40, "", "输入公会名称", 6, InputType.TYPE_CLASS_TEXT, infoFont, this, true, false, 0);
        board.attachChild(guildNameText);
        final F2ButtonSprite applyGuildSaveButton = createALBF2CommonButton(330, 50, "保存");
        board.attachChild(applyGuildSaveButton);
        this.registerTouchArea(applyGuildSaveButton);
        applyGuildSaveButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final String guildName = guildNameText.getText();
                if (guildName == null || guildName.replaceAll(" ", "").equals("")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "公会名不能为空！", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (GuildUtils.applyGuild(guildName)) {
                    guild = GuildUtils.getUserGuild();
                    inGuild = true;
                    for (int i = 0; i < 7; i++) {
                        final Sprite focusedButton = focusedButtons[i];
                        if (focusedButton != null) {
                            focusedButton.detachSelf();
                        }
                        final Sprite unfocusedButton = unfocusedButtons[i];
                        if (unfocusedButton != null) {
                            focusedButton.detachSelf();
                        }
                    }
                    for (final IEntity board : boards) {
                        board.detachSelf();
                    }
                    boards.clear();
                    focusedIndex = 0;
                    createButtons();
                    createGuildBoards();
                    focusButton(focusedIndex);
                }
            }

        });
        return board;
    }

    private IEntity createBoardBox() {
        final IEntity board = new Rectangle(23 + BOARD_WIDTH * 0.5f, 55 + BOARD_HEIGHT * 0.5f, BOARD_WIDTH, BOARD_HEIGHT, vbom);
        board.setAlpha(0);
        return board;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(true);
    }

    @Override
    public void leaveScene() {
    }

}

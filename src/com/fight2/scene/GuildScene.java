package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.text.InputType;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Guild;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.InputText;
import com.fight2.entity.engine.InputText.OnConfirmListener;
import com.fight2.util.GuildUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class GuildScene extends BaseScene {
    private final static int FRAME_BOTTOM = 80;
    private static int BOARD_WIDTH = 805;
    private static int BOARD_HEIGHT = 370;
    private final Sprite[] focusedButtons = new Sprite[7];
    private final Sprite[] unfocusedButtons = new Sprite[7];
    private final String[] NOGUILD_STRINGS = { "公会列表", "创建公会" };
    private final String[] INGUILD_STRINGS = { "公会信息", "公会排名", "成员列表", "公会仓库", "公会战", "炼金房", "投票" };
    private final List<IEntity> boards = new ArrayList<IEntity>();
    private final Sprite frame;

    private final Font buttonFont;
    private final Font infoFont;
    private int focusedIndex = 0;
    private final Guild guild;
    private boolean inGuild;

    public GuildScene(final GameActivity activity) throws IOException {
        super(activity);
        this.guild = GuildUtils.getUserGuild();
        inGuild = (this.guild != null);
        buttonFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        infoFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
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

        final Sprite experienceBox = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX, this.simulatedLeftX + 20, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight());
        this.attachChild(experienceBox);
        final Sprite experienceStick = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_STICK, 52, 0);
        experienceBox.attachChild(experienceStick);
        final Sprite experienceBoxStar = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX_STAR, this.simulatedLeftX + 20, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight());
        this.attachChild(experienceBoxStar);

        final Sprite staminaBox = createALBImageSprite(TextureEnum.COMMON_STAMINA_BOX, this.simulatedLeftX + 320, this.simulatedHeight
                - TextureEnum.COMMON_STAMINA_BOX.getHeight());
        this.attachChild(staminaBox);
        final Sprite staminaStick = createALBImageSprite(TextureEnum.COMMON_STAMINA_STICK, 56, 11);
        staminaBox.attachChild(staminaStick);

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
            boards.add(createGuildRankingBoard());
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

    private IEntity createGuildListBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "公会列表", vbom);
        board.attachChild(guildNameTitle);
        return board;
    }

    private IEntity createGuildApplyBoard() {
        final IEntity board = createBoardBox();
        final Text guildNameTitle = new Text(257, 225, infoFont, "公会名称：", vbom);
        board.attachChild(guildNameTitle);
        final InputText guildNameText = new InputText(512, 225, "请输入", "输入公会名称", infoFont, this);
        board.attachChild(guildNameText);
        final F2ButtonSprite applyGuildSaveButton = createALBF2CommonButton(330, 50, "保存");
        board.attachChild(applyGuildSaveButton);
        this.registerTouchArea(applyGuildSaveButton);
        applyGuildSaveButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (GuildUtils.applyGuild(guildNameText.getText())) {
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

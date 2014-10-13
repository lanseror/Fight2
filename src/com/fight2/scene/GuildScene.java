package com.fight2.scene;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.text.InputType;
import android.util.SparseArray;
import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.Guild;
import com.fight2.entity.GuildArenaUser;
import com.fight2.entity.GuildStoreroom;
import com.fight2.entity.ScrollZone;
import com.fight2.entity.User;
import com.fight2.entity.engine.CheckboxSprite;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.InputText;
import com.fight2.entity.engine.InputText.OnConfirmListener;
import com.fight2.util.AccountUtils;
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
    private static int TOUCH_AREA_HEIGHT = 280;
    private final Sprite[] focusedButtons = new Sprite[7];
    private final Sprite[] unfocusedButtons = new Sprite[7];
    private final static GuildTapEnum[] NOGUILD_ENUMS = { GuildTapEnum.GuildList, GuildTapEnum.GuildApply };
    private final static GuildTapEnum[] INGUILD_ENUMS = { GuildTapEnum.Info, GuildTapEnum.Ranking, GuildTapEnum.Members, GuildTapEnum.Warehouse,
            GuildTapEnum.Bid, GuildTapEnum.Poll, GuildTapEnum.QuitGuild };
    private final static String[] HEADBAR_GUILD_LIST = { "NO.", "公会名", "" };
    private final static float[] HBW_GUILD_LIST = { 0.1f, 0.5f, 0.4f };
    private final static String[] HEADBAR_GUILD_RANK = { "NO.", "公会名", "会长" };
    private final static float[] HBW_GUILD_RANK = { 0.1f, 0.5f, 0.4f };
    private final static String[] HEADBAR_GUILD_MEMBER = { "NO.", "名称", "身价", "出战" };
    private final static float[] HBW_GUILD_MEMBER = { 0.1f, 0.3f, 0.3f, 0.3f };
    private final static String[] HEADBAR_GUILD_POLL = { "NO.", "名称", "身价", "" };
    private final static float[] HBW_GUILD_POLL = { 0.1f, 0.3f, 0.3f, 0.3f };
    private final static String[] HEADBAR_GUILD_BID = { "NO.", "物品", "当前出价", "" };
    private final static float[] HBW_GUILD_BID = { 0.1f, 0.4f, 0.2f, 0.3f };
    private final Sprite frame;

    private final Font buttonFont;
    private final Font infoFont;
    private final Font amountFont;
    private final Font headBarFont;
    private final Font rankingFont;
    private final Font headTitleFont;
    private final Text headTitleText;
    private final Text contributionText;
    private int focusedIndex = 0;
    private Guild guild;
    private boolean inGuild;
    private boolean isAdmin;
    private IEntity currentBoard;
    private final Set<User> selectedArenaUsers = new HashSet<User>();

    public GuildScene(final GameActivity activity) throws IOException {
        super(activity);
        buttonFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        infoFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
        this.amountFont = ResourceManager.getInstance().getFont(FontEnum.Default, 30);
        headBarFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        rankingFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
        headTitleFont = ResourceManager.getInstance().getFont(FontEnum.Default, 30);
        frame = createALBImageSprite(TextureEnum.GUILD_FRAME, this.simulatedLeftX, FRAME_BOTTOM);
        this.attachChild(frame);
        headTitleText = new Text(frame.getWidth() * 0.5f, frame.getHeight() - 25, headTitleFont, "公会信息", 15, vbom);
        headTitleText.setColor(0XFF390800);
        frame.attachChild(headTitleText);
        contributionText = new Text(125, 43, amountFont, String.valueOf(session.getGuildContribution()), 10, vbom);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topBar = createALBImageSprite(TextureEnum.GUILD_TOPBAR, this.simulatedLeftX, this.simulatedHeight - TextureEnum.GUILD_TOPBAR.getHeight());
        this.attachChild(topBar);
        topBar.attachChild(contributionText);

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

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private void focusButton(final int i) {
        final GuildTapEnum[] guildTapEnums = inGuild ? INGUILD_ENUMS : NOGUILD_ENUMS;
        focusedButtons[focusedIndex].setVisible(false);
        unfocusedButtons[focusedIndex].setVisible(true);
        focusedButtons[i].setVisible(true);

        headTitleText.setText(guildTapEnums[i].getName());
        unfocusedButtons[i].setVisible(false);
        focusedIndex = i;
        switchBoard(guildTapEnums[i]);
    }

    public Sprite createButton(final TextureEnum textureEnum, final float x, final float y, final int index) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite button = new Sprite(pX, pY, width, height, texture, vbom);
        final IEntity touchArea = new Rectangle(button.getWidth() * 0.5f, button.getHeight() * 0.5f, button.getWidth(), button.getHeight() + 20, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    focusButton(index);
                    return true;
                }
                return false;
            }
        };
        touchArea.setAlpha(0);
        button.attachChild(touchArea);
        this.registerTouchArea(touchArea);
        return button;
    }

    private void createButtons() {
        for (int i = 0; i < 7; i++) {
            final Sprite focusedButton = focusedButtons[i];
            if (focusedButton != null) {
                focusedButton.detachSelf();
            }
            final Sprite unfocusedButton = unfocusedButtons[i];
            if (unfocusedButton != null) {
                unfocusedButton.detachSelf();
            }
        }
        final float buttonWidth = TextureEnum.GUILD_FRAME_BUTTON.getWidth();
        final float buttonHeight = TextureEnum.GUILD_FRAME_BUTTON.getHeight();
        final float buttonGap = 0;
        final GuildTapEnum[] guildTapEnums = inGuild ? INGUILD_ENUMS : NOGUILD_ENUMS;
        for (int i = 0; i < guildTapEnums.length; i++) {
            final Sprite focusedButton = createALBImageSprite(TextureEnum.GUILD_FRAME_BUTTON_FCS, 20 + i * (buttonWidth + buttonGap), 11);
            frame.attachChild(focusedButton);
            focusedButton.setVisible(false);
            focusedButtons[i] = focusedButton;
            final Sprite unfocusedButton = createButton(TextureEnum.GUILD_FRAME_BUTTON, 20 + i * (buttonWidth + buttonGap), 11, i);
            frame.attachChild(unfocusedButton);
            unfocusedButtons[i] = unfocusedButton;
            final Text focusedText = new Text(buttonWidth * 0.5f, buttonHeight * 0.5f, buttonFont, guildTapEnums[i].name, vbom);
            focusedButton.attachChild(focusedText);
            final Text unfocusedText = new Text(buttonWidth * 0.5f, buttonHeight * 0.5f, buttonFont, guildTapEnums[i].name, vbom);
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
        final InputText qq = new InputText(600, 335, 210, 40, guild.getQq(), "输入QQ群号码", 12, InputType.TYPE_CLASS_NUMBER, infoFont, this, true, false, 0,
                isAdmin);
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
        final InputText notice = new InputText(435, 195, 590, 140, guild.getNotice(), "输入公告", 75, InputType.TYPE_CLASS_TEXT, infoFont, this, true, true, 6,
                isAdmin);
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
        this.createHeadBar(HEADBAR_GUILD_RANK, HBW_GUILD_RANK, board);
        final List<Guild> guilds = GuildUtils.getTopGuilds();
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);
        this.registerTouchArea(touchArea);

        for (int i = 0; i < guilds.size(); i++) {
            final Guild guild = guilds.get(i);
            final IEntity row = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 64, vbom);
            row.setAlpha(0);
            if (i < guilds.size() - 1) {
                final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
                row.attachChild(line);
            }
            final float rowY = row.getHeight() * 0.5f;
            final Text number = new Text(25, rowY, rankingFont, String.valueOf(i + 1), vbom);
            row.attachChild(number);
            final Text guildName = new Text(100, rowY, rankingFont, guild.getName(), vbom);
            row.attachChild(guildName);
            this.leftAlignEntity(guildName, (HBW_GUILD_RANK[0]) * SCROLL_ZONE_WIDTH + 25);
            final Text presidentName = new Text(100, rowY, rankingFont, guild.getPresident().getName(), vbom);
            row.attachChild(presidentName);
            this.leftAlignEntity(presidentName, (HBW_GUILD_RANK[0] + HBW_GUILD_RANK[1]) * SCROLL_ZONE_WIDTH + 25);
            scrollZone.attachRow(row);
        }
        return board;
    }

    private IEntity createMemberListBoard() {
        final IEntity board = createBoardBox();
        this.createHeadBar(HEADBAR_GUILD_MEMBER, HBW_GUILD_MEMBER, board);
        final List<User> members = GuildUtils.getMembers(guild.getId());
        final SparseArray<GuildArenaUser> arenaUsers = guild.getArenaUsers();
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);

        for (int i = 0; i < members.size(); i++) {
            final User member = members.get(i);
            final IEntity row = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 64, vbom);
            row.setAlpha(0);
            if (i < members.size() - 1) {
                final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
                row.attachChild(line);
            }
            final float rowY = row.getHeight() * 0.5f;
            final Text number = new Text(25, rowY, rankingFont, String.valueOf(i + 1), vbom);
            row.attachChild(number);
            final Text memberName = new Text(100, rowY, rankingFont, member.getName(), vbom);
            row.attachChild(memberName);
            this.leftAlignEntity(memberName, (HBW_GUILD_MEMBER[0]) * SCROLL_ZONE_WIDTH + 25);
            final Text salaryText = new Text(100, rowY, rankingFont, "100", vbom);
            row.attachChild(salaryText);
            this.leftAlignEntity(salaryText, (HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1]) * SCROLL_ZONE_WIDTH + 25);
            final GuildArenaUser guildArenaUser = arenaUsers.get(member.getId());
            final boolean isArenaUser = guildArenaUser != null;
            if (isAdmin) {
                if (isArenaUser) {
                    selectedArenaUsers.add(member);
                }
                final CheckboxSprite checkedIcon = new CheckboxSprite(200, rowY, isArenaUser, vbom);
                row.attachChild(checkedIcon);
                checkedIcon.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
                final IEntity checkedIconTouchArea = new Rectangle(200, rowY, HBW_GUILD_MEMBER[3] * SCROLL_ZONE_WIDTH, 64, vbom) {
                    @Override
                    public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                        if (touchEvent.isActionUp()) {
                            clickCheckedIcon(checkedIcon, member, guildArenaUser);
                        }
                        return true;
                    }
                };
                checkedIconTouchArea.setAlpha(0);
                checkedIconTouchArea.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
                row.attachChild(checkedIconTouchArea);
                this.registerTouchArea(checkedIconTouchArea);
            } else if (isArenaUser) {
                final Sprite checkedIcon = createACImageSprite(TextureEnum.COMMON_CHECKBOX_ON, 200, rowY);
                row.attachChild(checkedIcon);
                checkedIcon.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
            }

            scrollZone.attachRow(row);
        }
        this.registerTouchArea(touchArea);
        return board;
    }

    private void clickCheckedIcon(final CheckboxSprite checkboxSprite, final User member, final GuildArenaUser guildArenaUser) {
        if (guildArenaUser != null && guildArenaUser.isLocked()) {
            alert("此出战人员已锁定！");
            return;
        }

        if (checkboxSprite.isChecked()) {
            final int status = GuildUtils.removeArenaUser(member.getId());
            if (status == 0) {
                selectedArenaUsers.remove(member);
                checkboxSprite.switchCheckbox();
            } else if (status == 2) {
                alert("此出战人员已锁定！");
            } else {
                alert("保存失败！");
            }
        } else {
            if (selectedArenaUsers.size() >= 3) {
                alert("最多只可以3个人出战！");
                return;
            }

            if (GuildUtils.addArenaUser(member.getId())) {
                selectedArenaUsers.add(member);
                checkboxSprite.switchCheckbox();
            } else {
                alert("保存失败！");
            }

        }

    }

    private IEntity createGuildWarehouseBoard() {
        final IEntity board = createBoardBox();
        final GuildStoreroom storeroom = GuildUtils.getGuildStoreroom(activity);
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);

        final IEntity row1 = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 150, vbom);
        row1.setAlpha(0);
        final float row1Y = row1.getHeight() * 0.5f;
        final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
        row1.attachChild(line);
        // Ticket
        final TextureEnum ticketEnum = TextureEnum.COMMON_ARENA_TICKET;
        final Text ticketAmountText = new Text(95, 25, amountFont, String.format("×%s", storeroom.getTicket()), vbom);
        ticketAmountText.setColor(0XFFAECE01);
        final IEntity ticketImg = createACImageSprite(ticketEnum, 200, row1Y);
        row1.attachChild(ticketImg);
        row1.attachChild(ticketAmountText);
        this.leftAlignEntity(ticketAmountText, ticketImg.getX() + ticketImg.getWidth() * 0.5f + 2);
        // Stamina
        final TextureEnum staminaEnum = TextureEnum.COMMON_STAMINA;
        final Text staminaAmountText = new Text(400, 25, amountFont, String.format("×%s", storeroom.getStamina()), vbom);
        staminaAmountText.setColor(0XFFAECE01);
        final IEntity staminaImg = createACImageSprite(staminaEnum, 500, row1Y - 5);
        row1.attachChild(staminaImg);
        row1.attachChild(staminaAmountText);
        this.leftAlignEntity(staminaAmountText, staminaImg.getX() + staminaImg.getWidth() * 0.5f + 2);
        scrollZone.attachRow(row1);

        final List<Card> cards = storeroom.getCards();
        for (int i = 0; i < cards.size(); i++) {
            final Card card = cards.get(i);
            final IEntity cardRow = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 190, vbom);
            cardRow.setAlpha(0);
            final float cardRowY = cardRow.getHeight() * 0.5f;
            final Sprite cardRowLine = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
            cardRow.attachChild(cardRowLine);
            final ITextureRegion cardTexture = TextureFactory.getInstance().getTextureRegion(card.getImage());
            final Sprite cardSprite = new Sprite(220, cardRowY, 110, 165, cardTexture, vbom);
            final Text cardAmountText = new Text(300, 25, amountFont, String.format("×%s", card.getAmount()), vbom);
            cardAmountText.setColor(0XFFAECE01);
            this.leftAlignEntity(cardAmountText, cardSprite.getX() + cardSprite.getWidth() * 0.5f + 5);
            cardRow.attachChild(cardSprite);
            cardRow.attachChild(cardAmountText);
            // Receive button
            final F2ButtonSprite receiveButton = createACF2CommonButton(550, cardRowY, "拍卖");
            cardRow.attachChild(receiveButton);
            // this.registerTouchArea(receiveButton);
            receiveButton.setOnClickListener(new F2OnClickListener() {
                @Override
                public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    final int status = GuildUtils.sendCardToBid(card.getId());
                    if (status == 0) {
                        alert("已经加入到拍卖阵列");
                        final int amount = card.getAmount();
                        if (amount > 1) {
                            card.setAmount(amount - 1);
                            cardAmountText.setText(String.format("×%s", card.getAmount()));
                        } else {
                            updateScene();
                        }
                    } else if (status == 2) {
                        alert("拍卖阵列已满");
                    } else {
                        alert("错误");
                    }
                }
            });

            scrollZone.attachRow(cardRow);
        }

        this.registerTouchArea(touchArea);
        return board;
    }

    private IEntity createGuildBidBoard() {
        final IEntity board = createBoardBox();
        this.createHeadBar(HEADBAR_GUILD_BID, HBW_GUILD_BID, board);
        final List<User> members = GuildUtils.getMembers(guild.getId());
        final SparseArray<GuildArenaUser> arenaUsers = guild.getArenaUsers();
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);

        for (int i = 0; i < members.size(); i++) {
            final User member = members.get(i);
            final IEntity row = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 64, vbom);
            row.setAlpha(0);
            if (i < members.size() - 1) {
                final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
                row.attachChild(line);
            }
            final float rowY = row.getHeight() * 0.5f;
            final Text number = new Text(25, rowY, rankingFont, String.valueOf(i + 1), vbom);
            row.attachChild(number);
            final Text memberName = new Text(100, rowY, rankingFont, member.getName(), vbom);
            row.attachChild(memberName);
            this.leftAlignEntity(memberName, (HBW_GUILD_MEMBER[0]) * SCROLL_ZONE_WIDTH + 25);
            final Text salaryText = new Text(100, rowY, rankingFont, "100", vbom);
            row.attachChild(salaryText);
            this.leftAlignEntity(salaryText, (HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1]) * SCROLL_ZONE_WIDTH + 25);
            final GuildArenaUser guildArenaUser = arenaUsers.get(member.getId());
            final boolean isArenaUser = guildArenaUser != null;
            if (isAdmin) {
                if (isArenaUser) {
                    selectedArenaUsers.add(member);
                }
                final CheckboxSprite checkedIcon = new CheckboxSprite(200, rowY, isArenaUser, vbom);
                row.attachChild(checkedIcon);
                checkedIcon.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
                final IEntity checkedIconTouchArea = new Rectangle(200, rowY, HBW_GUILD_MEMBER[3] * SCROLL_ZONE_WIDTH, 64, vbom) {
                    @Override
                    public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                        if (touchEvent.isActionUp()) {
                            clickCheckedIcon(checkedIcon, member, guildArenaUser);
                        }
                        return true;
                    }
                };
                checkedIconTouchArea.setAlpha(0);
                checkedIconTouchArea.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
                row.attachChild(checkedIconTouchArea);
                this.registerTouchArea(checkedIconTouchArea);
            } else if (isArenaUser) {
                final Sprite checkedIcon = createACImageSprite(TextureEnum.COMMON_CHECKBOX_ON, 200, rowY);
                row.attachChild(checkedIcon);
                checkedIcon.setX((HBW_GUILD_MEMBER[0] + HBW_GUILD_MEMBER[1] + HBW_GUILD_MEMBER[2] + HBW_GUILD_MEMBER[3] * 0.5f) * SCROLL_ZONE_WIDTH);
            }

            scrollZone.attachRow(row);
        }
        this.registerTouchArea(touchArea);
        return board;
    }

    private IEntity createGuildPollBoard() {
        final IEntity board = createBoardBox();
        if (!guild.isPollEnabled()) {
            final Text guildNameTitle = new Text(257, 225, infoFont, "投票未开启", vbom);
            board.attachChild(guildNameTitle);
            return board;
        } else if (GuildUtils.hasVoted()) {
            final Text guildNameTitle = new Text(257, 225, infoFont, "你已经投过票", vbom);
            board.attachChild(guildNameTitle);
            return board;
        }
        this.createHeadBar(HEADBAR_GUILD_POLL, HBW_GUILD_POLL, board);
        final List<User> members = GuildUtils.getMembers(guild.getId());
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);

        for (int i = 0; i < members.size(); i++) {
            final User member = members.get(i);
            final IEntity row = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 64, vbom);
            row.setAlpha(0);
            if (i < members.size() - 1) {
                final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
                row.attachChild(line);
            }
            final float rowY = row.getHeight() * 0.5f;
            final Text number = new Text(25, rowY, rankingFont, String.valueOf(i + 1), vbom);
            row.attachChild(number);
            final Text memberName = new Text(100, rowY, rankingFont, member.getName(), vbom);
            row.attachChild(memberName);
            this.leftAlignEntity(memberName, (HBW_GUILD_POLL[0]) * SCROLL_ZONE_WIDTH + 25);
            final Text salaryText = new Text(100, rowY, rankingFont, "100", vbom);
            row.attachChild(salaryText);
            this.leftAlignEntity(salaryText, (HBW_GUILD_POLL[0] + HBW_GUILD_POLL[1]) * SCROLL_ZONE_WIDTH + 25);
            final F2ButtonSprite voteButton = createACF2CommonButton(300, rowY, "投票");
            this.leftAlignEntity(voteButton, (HBW_GUILD_POLL[0] + HBW_GUILD_POLL[1] + HBW_GUILD_POLL[2]) * SCROLL_ZONE_WIDTH + 25);
            row.attachChild(voteButton);
            this.registerTouchArea(voteButton);
            voteButton.setOnClickListener(new F2OnClickListener() {
                @Override
                public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    final int statusCode = GuildUtils.vote(member.getId());
                    if (statusCode == 0) {
                        alert("你已经投了一票给" + member.getName());
                        updateScene();
                    } else if (statusCode == 1) {
                        alert("你已经投过票！");
                    } else if (statusCode == 2) {
                        alert("投票失败！");
                    }
                }
            });
            scrollZone.attachRow(row);
        }
        this.registerTouchArea(touchArea);
        return board;
    }

    private IEntity createQuitGuildBoard() {
        final IEntity board = createBoardBox();
        final F2ButtonSprite quitGuildButton = createACF2CommonButton(board.getWidth() * 0.5f, board.getHeight() * 0.5f, "退出公会");
        board.attachChild(quitGuildButton);
        this.registerTouchArea(quitGuildButton);
        quitGuildButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (GuildUtils.quitGuild()) {
                    updateScene();
                } else {
                    alert("退出公会失败！");
                }
            }
        });
        return board;
    }

    public IEntity getBoard(final GuildTapEnum guildTapEnum) {

        switch (guildTapEnum) {
            case Info:
                currentBoard = createGuildInfoBoard();
                break;
            case Ranking:
                currentBoard = createGuildRankingBoard();
                break;
            case Members:
                currentBoard = createMemberListBoard();
                break;
            case Warehouse:
                currentBoard = createGuildWarehouseBoard();
                break;
            case Bid:
                currentBoard = createGuildBidBoard();
                break;
            case Poll:
                currentBoard = createGuildPollBoard();
                break;
            case QuitGuild:
                currentBoard = createQuitGuildBoard();
                break;
            case GuildList:
                currentBoard = createGuildListBoard();
                break;
            case GuildApply:
                currentBoard = createGuildApplyBoard();
                break;

        }
        return currentBoard;
    }

    private void switchBoard(final GuildTapEnum guildTapEnum) {
        if (currentBoard != null) {
            currentBoard.detachSelf();
        }
        this.unregisterTouchAreas(new ITouchAreaMatcher() {
            @Override
            public boolean matches(final ITouchArea touchArea) {
                if (touchArea instanceof IEntity) {
                    boolean areaInScene = false;
                    final IEntity entityTouchArea = (IEntity) touchArea;
                    IEntity entityTouchAreaParent = entityTouchArea.getParent();
                    while (entityTouchAreaParent != null) {
                        if (entityTouchAreaParent == GuildScene.this) {
                            areaInScene = true;
                            break;
                        }
                        entityTouchAreaParent = entityTouchAreaParent.getParent();
                    }
                    return !areaInScene;
                } else {
                    return false;
                }

            }

        });

        switch (guildTapEnum) {
            case Info:
                currentBoard = createGuildInfoBoard();
                break;
            case Ranking:
                currentBoard = createGuildRankingBoard();
                break;
            case Members:
                currentBoard = createMemberListBoard();
                break;
            case Warehouse:
                currentBoard = createGuildWarehouseBoard();
                break;
            case Bid:
                currentBoard = createGuildBidBoard();
                break;
            case Poll:
                currentBoard = createGuildPollBoard();
                break;
            case QuitGuild:
                currentBoard = createQuitGuildBoard();
                break;
            case GuildList:
                currentBoard = createGuildListBoard();
                break;
            case GuildApply:
                currentBoard = createGuildApplyBoard();
                break;

        }
        frame.attachChild(currentBoard);
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

    private IEntity createGuildListBoard() {
        final IEntity board = createBoardBox();
        this.createHeadBar(HEADBAR_GUILD_LIST, HBW_GUILD_LIST, board);
        final List<Guild> guilds = GuildUtils.getTopGuilds();

        // final List<Guild> guilds = new ArrayList<Guild>();
        // for (int i = 0; i < 20; i++) {
        // final int seq = i + 1;
        // final Guild guild = new Guild();
        // guild.setId(seq);
        // guild.setName("公会" + seq);
        // final User president = new User();
        // president.setId(seq);
        // president.setName("会长" + seq);
        // guild.setPresident(president);
        // guilds.add(guild);
        // }
        final ScrollZone scrollZone = new ScrollZone(board.getWidth() * 0.5f, 5 + SCROLL_ZONE_HEIGHT * 0.5f, SCROLL_ZONE_WIDTH, SCROLL_ZONE_HEIGHT, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(SCROLL_ZONE_WIDTH * 0.5f, SCROLL_ZONE_HEIGHT * 0.5f, TOUCH_AREA_WIDTH, TOUCH_AREA_HEIGHT);
        board.attachChild(scrollZone);

        for (int i = 0; i < guilds.size(); i++) {
            final Guild guild = guilds.get(i);
            final IEntity row = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 64, vbom);
            row.setAlpha(0);
            if (i < guilds.size() - 1) {
                final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
                row.attachChild(line);
            }
            final float rowY = row.getHeight() * 0.5f;
            final Text number = new Text(25, rowY, rankingFont, String.valueOf(i + 1), vbom);
            row.attachChild(number);
            final Text guildName = new Text(100, rowY, rankingFont, guild.getName(), vbom);
            row.attachChild(guildName);
            this.leftAlignEntity(guildName, (HBW_GUILD_LIST[0]) * SCROLL_ZONE_WIDTH + 25);
            final F2ButtonSprite joinGuildButton = createACF2CommonButton((HBW_GUILD_LIST[0] + HBW_GUILD_LIST[1] + HBW_GUILD_LIST[2] * 0.5f)
                    * SCROLL_ZONE_WIDTH, rowY, "申请加入");
            row.attachChild(joinGuildButton);
            this.registerTouchArea(joinGuildButton);
            joinGuildButton.setOnClickListener(new F2OnClickListener() {
                @Override
                public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    if (GuildUtils.joinGuild(guild.getId())) {
                        GuildScene.this.guild = GuildUtils.getUserGuild();
                        inGuild = true;
                        isAdmin = true;
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
                        focusedIndex = 0;
                        createButtons();
                        focusButton(focusedIndex);
                    }
                }
            });
            scrollZone.attachRow(row);
        }
        this.registerTouchArea(touchArea);
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
                    alert("公会名不能为空！");
                } else if (GuildUtils.applyGuild(guildName)) {
                    updateScene();
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
        selectedArenaUsers.clear();
        focusedIndex = 0;
        this.guild = GuildUtils.getUserGuild();
        contributionText.setText(String.valueOf(session.getGuildContribution()));
        inGuild = (this.guild != null);
        isAdmin = (inGuild && (guild.getPresident().getId() == session.getId()));
        createButtons();
        focusButton(focusedIndex);
    }

    @Override
    public void leaveScene() {
    }

    private enum GuildTapEnum {
        Info("公会信息"),
        Ranking("公会排名"),
        Members("成员列表"),
        Warehouse("公会仓库"),
        Bid("拍卖"),
        Poll("投票"),
        QuitGuild("退出公会"),
        GuildList("公会列表"),
        GuildApply("创建公会");
        private final String name;

        private GuildTapEnum(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }
}

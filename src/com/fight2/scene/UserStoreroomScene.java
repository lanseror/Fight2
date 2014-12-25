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
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.ScrollZone;
import com.fight2.entity.UserStoreroom;
import com.fight2.entity.engine.CardFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.AccountUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.ResourceManager;

public class UserStoreroomScene extends BaseScene {
    private final static int FRAME_BOTTOM = 90;
    private static int BOARD_WIDTH = 805;
    private static int BOARD_HEIGHT = 370;
    private static int SCROLL_ZONE_WIDTH = 795;
    private static int SCROLL_ZONE_HEIGHT = 360;
    private static int TOUCH_AREA_WIDTH = 790;
    private static int TOUCH_AREA_HEIGHT = 280;
    private final List<IEntity> boards = new ArrayList<IEntity>();
    private final Sprite frame;

    private final Font headTitleFont;
    private final Font amountFont;
    private final Text headTitleText;

    public UserStoreroomScene(final GameActivity activity) throws IOException {
        super(activity);
        headTitleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        this.amountFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        frame = createALBImageSprite(TextureEnum.GUILD_FRAME, this.simulatedLeftX, FRAME_BOTTOM);
        this.attachChild(frame);
        headTitleText = new Text(frame.getWidth() * 0.5f, frame.getHeight() - 25, headTitleFont, "物品仓库", 15, vbom);
        headTitleText.setColor(0XFF390800);
        frame.attachChild(headTitleText);
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
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
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

    private IEntity createMainItemBoard() {
        final IEntity board = createBoardBox();
        final UserStoreroom userStoreroom = AccountUtils.getUserStoreroom(activity);
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
        final Text ticketAmountText = new Text(95, 25, amountFont, String.format("×%s", userStoreroom.getTicket()), vbom);
        ticketAmountText.setColor(0XFFAECE01);
        final IEntity ticketImg = createACImageSprite(ticketEnum, 200, row1Y);
        row1.attachChild(ticketImg);
        row1.attachChild(ticketAmountText);
        this.leftAlignEntity(ticketAmountText, ticketImg.getX() + ticketImg.getWidth() * 0.5f + 2);
        // Stamina
        final TextureEnum staminaEnum = TextureEnum.COMMON_STAMINA;
        final Text staminaAmountText = new Text(400, 25, amountFont, String.format("×%s", userStoreroom.getStamina()), vbom);
        staminaAmountText.setColor(0XFFAECE01);
        final IEntity staminaImg = createACImageSprite(staminaEnum, 500, row1Y - 5);
        row1.attachChild(staminaImg);
        row1.attachChild(staminaAmountText);
        this.leftAlignEntity(staminaAmountText, staminaImg.getX() + staminaImg.getWidth() * 0.5f + 2);
        scrollZone.attachRow(row1);

        final List<Card> cards = userStoreroom.getCards();
        for (int i = 0; i < cards.size(); i++) {
            final Card card = cards.get(i);
            final IEntity cardRow = new Rectangle(0, 0, SCROLL_ZONE_WIDTH, 190, vbom);
            cardRow.setAlpha(0);
            final float cardRowY = cardRow.getHeight() * 0.5f;
            final Sprite cardRowLine = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, SCROLL_ZONE_WIDTH * 0.5f, 1);
            cardRow.attachChild(cardRowLine);
            final IEntity cardSprite = new CardFrame(220, cardRowY, 110, 165, card, activity);
            final Text cardAmountText = new Text(300, 25, amountFont, String.format("×%s", card.getAmount()), vbom);
            cardAmountText.setColor(0XFFAECE01);
            this.leftAlignEntity(cardAmountText, cardSprite.getX() + cardSprite.getWidth() * 0.5f + 5);
            cardRow.attachChild(cardSprite);
            cardRow.attachChild(cardAmountText);
            this.registerTouchArea(cardSprite);
            // Receive button
            final F2ButtonSprite receiveButton = createACF2CommonButton(550, cardRowY, "提取");
            cardRow.attachChild(receiveButton);
            this.registerTouchArea(receiveButton);
            receiveButton.setOnClickListener(new F2OnClickListener() {
                @Override
                public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    final int size = AccountUtils.receiveCardFromUserStoreroom(activity, card.getId());
                    if (size == 0) {
                        alert("你的卡组已满！");
                    } else if (size < card.getAmount()) {
                        card.setAmount(card.getAmount() - size);
                        cardAmountText.setText(String.format("×%s", card.getAmount()));
                    } else {
                        updateScene();
                    }
                }
            });

            scrollZone.attachRow(cardRow);
        }

        this.registerTouchArea(touchArea);
        return board;
    }

    private void createBoards() {
        for (final IEntity board : boards) {
            board.detachSelf();
        }
        boards.clear();
        boards.add(createMainItemBoard());
        frame.attachChild(boards.get(0));
    }

    private IEntity createBoardBox() {
        final IEntity board = new Rectangle(23 + BOARD_WIDTH * 0.5f, 55 + BOARD_HEIGHT * 0.5f, BOARD_WIDTH, BOARD_HEIGHT, vbom);
        board.setAlpha(0);
        return board;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(true);
        createBoards();
        F2SoundManager.getInstance().play(SoundEnum.DOOR);
    }

    @Override
    public void leaveScene() {
        F2SoundManager.getInstance().play(SoundEnum.DOOR);
    }

}

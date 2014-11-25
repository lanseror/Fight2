package com.fight2.scene;

import java.io.IOException;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.MusicEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.User;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.CardUtils;
import com.fight2.util.F2MusicManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class PreBattleScene extends BaseScene {
    private final TextureFactory textureFactory = TextureFactory.getInstance();
    private final Sprite myCardSprite;
    private final Sprite opponentCardSprite;
    public final static int GAP = 25;
    public final static int CARD_WIDTH = 340;
    public final static int CARD_HEIGHT = 510;
    public final static float CARD_CENTER_X = CARD_WIDTH * 0.5f;
    public final static float CARD_CENTER_Y = CARD_HEIGHT * 0.5f;
    private final PartyInfo myPartyInfo = GameUserSession.getInstance().getPartyInfo();
    private final PartyInfo opponentPartyInfo;
    private final Party[] myParties = myPartyInfo.getParties();
    private final Party[] opponentParties;

    private final Font font = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
    private final Text skillTextLeft;
    private final Text skillTextRight;

    private final Font nameFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
    private final Text nameTextLeft;
    private final Text nameTextRight;

    private final Font attributeFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
    private final Text atkTextLeft;
    private final Text hpTextLeft;
    private final Text atkTextRight;
    private final Text hpTextRight;
    private final User attackPlayer;
    private final boolean isArena;

    public PreBattleScene(final GameActivity activity, final User attackPlayer, final boolean isArena) throws IOException {
        super(activity);
        this.attackPlayer = attackPlayer;
        this.isArena = isArena;
        opponentPartyInfo = CardUtils.getPartyByUserId(activity, attackPlayer.getId());
        opponentParties = opponentPartyInfo.getParties();
        final Card myLeader = myParties[0].getCards()[0];
        final Card opponentLeader = opponentParties[0].getCards()[0];
        final ITextureRegion myTexture = textureFactory.getTextureRegion(myLeader.getImage());
        myCardSprite = new Sprite(this.cameraCenterX - CARD_CENTER_X - GAP, this.cameraCenterY, CARD_WIDTH, CARD_HEIGHT, myTexture, vbom);
        this.attachChild(myCardSprite);
        final ITextureRegion opponentTexture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        this.opponentCardSprite = new Sprite(this.cameraCenterX + CARD_CENTER_X + GAP, this.cameraCenterY, CARD_WIDTH, CARD_HEIGHT, opponentTexture, vbom);
        this.attachChild(opponentCardSprite);
        loadImageFromServer(opponentLeader);

        final Sprite vsSprite = createACImageSprite(TextureEnum.PREBATTLE_VS_ICON, this.cameraCenterX + 15, this.cameraCenterY);
        this.attachChild(vsSprite);
        this.skillTextLeft = new Text(this.cameraCenterX - CARD_CENTER_X - GAP, this.cameraCenterY - 90, font, "组合技能", vbom);
        skillTextLeft.setColor(0XFFF6BB0C);
        this.attachChild(skillTextLeft);
        this.skillTextRight = new Text(this.cameraCenterX + CARD_CENTER_X + GAP, this.cameraCenterY - 90, font, "组合技能", vbom);
        skillTextRight.setColor(0XFFF6BB0C);
        this.attachChild(skillTextRight);

        this.nameTextLeft = new Text(TextureEnum.PREBATTLE_NAME_BOX.getWidth() * 0.5f, 48, nameFont, GameUserSession.getInstance().getName(), vbom);
        this.nameTextRight = new Text(TextureEnum.PREBATTLE_NAME_BOX.getWidth() * 0.5f, 48, nameFont, attackPlayer.getName(), vbom);

        this.atkTextLeft = new Text(180, 60, attributeFont, String.valueOf(myPartyInfo.getAtk()), vbom);
        this.hpTextLeft = new Text(180, 20, attributeFont, String.valueOf(myPartyInfo.getHp()), vbom);
        this.atkTextRight = new Text(165, 60, attributeFont, String.valueOf(opponentPartyInfo.getAtk()), vbom);
        this.hpTextRight = new Text(165, 20, attributeFont, String.valueOf(opponentPartyInfo.getHp()), vbom);

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.BATTLE_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite nameBoxSpriteLeft = createACImageSprite(TextureEnum.PREBATTLE_NAME_BOX, this.cameraCenterX - CARD_CENTER_X - GAP, this.cameraCenterY
                + CARD_CENTER_Y - 20);
        this.attachChild(nameBoxSpriteLeft);
        nameBoxSpriteLeft.attachChild(nameTextLeft);
        final Sprite nameBoxSpriteRight = createACImageSprite(TextureEnum.PREBATTLE_NAME_BOX, this.cameraCenterX + CARD_CENTER_X + GAP, this.cameraCenterY
                + CARD_CENTER_Y - 20);
        this.attachChild(nameBoxSpriteRight);
        nameBoxSpriteRight.attachChild(nameTextRight);

        final Sprite leftInfoSprite = createACImageSprite(TextureEnum.PREBATTLE_INFO_LEFT, this.cameraCenterX - CARD_CENTER_X - GAP - 25, this.cameraCenterY);
        final Sprite rightInfoSprite = createACImageSprite(TextureEnum.PREBATTLE_INFO_RIGHT, this.cameraCenterX + CARD_CENTER_X + GAP + 25, this.cameraCenterY);
        leftInfoSprite.setAlpha(0.8f);
        rightInfoSprite.setAlpha(0.8f);
        this.attachChild(leftInfoSprite);
        this.attachChild(rightInfoSprite);
        leftInfoSprite.attachChild(atkTextLeft);
        leftInfoSprite.attachChild(hpTextLeft);
        rightInfoSprite.attachChild(atkTextRight);
        rightInfoSprite.attachChild(hpTextRight);

        final Sprite comboSkillBoxSpriteLeft = createACImageSprite(TextureEnum.PREBATTLE_COMBO_SKILL_LEFT, this.cameraCenterX - CARD_CENTER_X - GAP,
                this.cameraCenterY - CARD_CENTER_Y + 80);
        this.attachChild(comboSkillBoxSpriteLeft);
        final Sprite comboSkillBoxSpriteRight = createACImageSprite(TextureEnum.PREBATTLE_COMBO_SKILL_RIGHT, this.cameraCenterX + CARD_CENTER_X + GAP,
                this.cameraCenterY - CARD_CENTER_Y + 80);
        this.attachChild(comboSkillBoxSpriteRight);

        final F2ButtonSprite battleButton = createALBF2ButtonSprite(TextureEnum.PREBATTLE_BATTLE_BUTTON, TextureEnum.PREBATTLE_BATTLE_BUTTON_FCS,
                this.simulatedRightX - 135, 400);
        battleButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2MusicManager.getInstance().playMusic(MusicEnum.ARENA_ATTACK);
                try {
                    final Scene battleScene = new BattleScene(activity, attackPlayer.getId(), opponentParties, isArena);
                    activity.getEngine().setScene(battleScene);
                } catch (final IOException e) {
                    Debug.e(e);
                }
            }
        });
        this.attachChild(battleButton);
        this.registerTouchArea(battleButton);

        final F2ButtonSprite retreatButton = createALBF2ButtonSprite(TextureEnum.PREBATTLE_RETREAT_BUTTON, TextureEnum.PREBATTLE_RETREAT_BUTTON_FCS,
                this.simulatedRightX - 135, 60);
        retreatButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().sceneBack(false);
            }
        });
        this.attachChild(retreatButton);
        this.registerTouchArea(retreatButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        updateScene();
        registerUpdateHandler(new IUpdateHandler() {

            private int updates = 0;

            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(final float pSecondsElapsed) {
                ++updates;
                if (updates > 10) {
                    unregisterUpdateHandler(this);

                    // if (!battleRecordQueue.isEmpty()) {
                    // // F2MusicManager.getInstance().playMusic(MusicEnum.BATTLE_BG);
                    // handleBattleRecord(battleRecordQueue.poll(), battleFinishedCallbackQueue.poll());
                    // }
                }
            }
        });
    }

    public void loadImageFromServer(final Card card) {
        final IAsyncCallback callback = new IAsyncCallback() {
            private String image;

            @Override
            public void workToDo() {
                try {
                    image = ImageUtils.getLocalString(card.getImage(), activity);
                    textureFactory.addCardResource(activity, image);
                    card.setImage(image);
                } catch (final IOException e) {
                    Debug.e(e);
                }

            }

            @Override
            public void onComplete() {

                if (image != null) {
                    final ITextureRegion texture = textureFactory.getTextureRegion(image);
                    final Sprite imageSprite = new Sprite(CARD_CENTER_X, CARD_CENTER_Y, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
                    opponentCardSprite.attachChild(imageSprite);
                }

            }

        };

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskLoader().execute(callback);
            }
        });

    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
    }

}

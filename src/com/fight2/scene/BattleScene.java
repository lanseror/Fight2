package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.BattlePartyFrame;
import com.fight2.entity.Card;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.HpBar;
import com.fight2.entity.Party;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class BattleScene extends BaseScene {
    private final float TOP_PARTY_FRAME_Y = this.cameraHeight - TextureEnum.BATTLE_PARTY_TOP.getHeight();
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    private final Party[] myParties = GameUserSession.getInstance().getPartyInfo().getParties();
    private final Party[] opponentParties;
    private final SparseArray<HpBar> hpBars = new SparseArray<HpBar>();

    public BattleScene(final GameActivity activity, final int attackPlayerId) throws IOException {
        super(activity);
        opponentParties = CardUtils.getPartyByUserId(activity, attackPlayerId).getParties();
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.BATTLE_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 250);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite tButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 140, 150);
        tButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final HpBar ppBar = hpBars.get(myParties[0].getId());
                ppBar.setCurrentPoint(2000);
            }
        });
        this.attachChild(tButton);
        this.registerTouchArea(tButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        updateScene();
    }

    @Override
    public void updateScene() {
        final float width = TextureEnum.BATTLE_PARTY_BOTTOM.getWidth();
        final float frameStartX = 90;
        final float partyGap = 10;

        for (int i = 0; i < myParties.length; i++) {
            final Party party = myParties[i];
            final IEntity partyContainer = createPartyContainer(party, frameStartX + (width + partyGap) * i, 0, true);
            this.attachChild(partyContainer);
        }

        for (int i = 0; i < opponentParties.length; i++) {
            final Party party = opponentParties[i];
            final IEntity partyContainer = createOpponentPartyContainer(party, frameStartX + (width + partyGap) * i, TOP_PARTY_FRAME_Y - 67, false);
            this.attachChild(partyContainer);
        }
    }

    private IEntity createPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        final TextureEnum textureEnum = (isBottom ? TextureEnum.BATTLE_PARTY_BOTTOM : TextureEnum.BATTLE_PARTY_TOP);
        final float width = textureEnum.getWidth();
        final float height = 165;
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final IEntity partyContainer = new Rectangle(pX, pY, width, height, vbom);
        partyContainer.setAlpha(0);

        final TextureFactory textureFactory = TextureFactory.getInstance();
        final float startX = 60;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }

            final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
            final Sprite cardSprite = new Sprite(startX + 65 * i, 90, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
            partyContainer.attachChild(cardSprite);
        }

        final BattlePartyFrame battlePartyFrame = createPartyFrame(textureEnum, 0, 0, isBottom);
        final HpBar hpBar = new HpBar(158, 61, vbom, party.getHp(), true);
        battlePartyFrame.attachChild(hpBar);
        hpBars.put(party.getId(), hpBar);
        partyContainer.attachChild(battlePartyFrame);

        return partyContainer;
    }

    private IEntity createOpponentPartyContainer(final Party party, final float x, final float y, final boolean isBottom) {
        final float avatarWidth = 72;
        final float avatarHeight = 72;
        final TextureEnum textureEnum = (isBottom ? TextureEnum.BATTLE_PARTY_BOTTOM : TextureEnum.BATTLE_PARTY_TOP);
        final float width = textureEnum.getWidth();
        final float height = 81 + avatarHeight - 5;
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final IEntity partyContainer = new Rectangle(pX, pY, width, height, vbom);
        partyContainer.setAlpha(0);

        final TextureFactory textureFactory = TextureFactory.getInstance();

        final float avatarGap = 1;
        final float startX = 10 + avatarWidth * 0.5f;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }
            final String avatar = card.getAvatar();
            final ITextureRegion texture = StringUtils.isEmpty(avatar) ? textureFactory.getAssetTextureRegion(TextureEnum.COMMON_DEFAULT_AVATAR)
                    : textureFactory.getTextureRegion(avatar);
            final Sprite cardSprite = new Sprite(startX + (avatarWidth + avatarGap) * i, avatarHeight * 0.5f, avatarWidth, avatarHeight, texture, vbom);
            partyContainer.attachChild(cardSprite);
        }

        final BattlePartyFrame battlePartyFrame = createPartyFrame(textureEnum, 0, avatarHeight - 5, isBottom);
        final HpBar hpBar = new HpBar(156, 22, vbom, party.getHp());
        battlePartyFrame.attachChild(hpBar);
        hpBars.put(party.getId(), hpBar);
        partyContainer.attachChild(battlePartyFrame);

        return partyContainer;
    }

    private BattlePartyFrame createPartyFrame(final TextureEnum textureEnum, final float x, final float y, final boolean isBottom) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final BattlePartyFrame partyFrame = new BattlePartyFrame(pX, pY, texture, vbom, isBottom);
        return partyFrame;
    }
}

package com.fight2.entity;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.scene.BattleScene.ModifierFinishedListener;
import com.fight2.scene.BattleScene.OnFinishedCallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class BattlePartyFrame extends Rectangle {
    public final static int WIDTH = 312;
    public final static int HEIGHT = 165;
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    public final static int AVATAR_WIDTH = 72;
    public final static int AVATAR_HEIGHT = 72;
    private final float initX;
    private final float initY;
    private final VertexBufferObjectManager vbom;
    private HpBar hpBar;
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Main);
    private final Text atkText;
    private int atk;
    private int defence;
    private final Party party;
    private final Sprite[] cardSprites = new Sprite[4];
    private final boolean isBottom;
    private final GameActivity activity;

    public BattlePartyFrame(final float pX, final float pY, final Party party, final GameActivity activity, final boolean isBottom) {
        super(pX + WIDTH * 0.5f, pY + HEIGHT * 0.5f, WIDTH, HEIGHT, activity.getVertexBufferObjectManager());
        super.setAlpha(0);
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.party = party;
        this.isBottom = isBottom;
        if (isBottom) {
            this.createBottomCard(party);
            this.createBottomFrame(party);
            atkText = new Text(91, 26, font, "0123456789", vbom);
        } else {
            final float height = 81 + AVATAR_HEIGHT - 5;
            this.setHeight(height);
            this.setY(pY + height * 0.5f);
            this.createTopCard(party);
            this.createTopFrame(party);
            atkText = new Text(91, 125, font, "0123456789", vbom);
        }
        this.initX = this.getX();
        this.initY = this.getY();
        atkText.setText(String.valueOf(party.getAtk()));
        this.attachChild(atkText);
        atk = party.getAtk();
    }

    private void createBottomCard(final Party party) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final float startX = 60;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }

            final ITextureRegion texture = textureFactory.getTextureRegion(card.getImage());
            final Sprite cardSprite = new Sprite(startX + 65 * i, 80, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
            cardSprites[i] = cardSprite;
            this.attachChild(cardSprite);
        }
    }

    private void createTopCard(final Party party) {
        final TextureFactory textureFactory = TextureFactory.getInstance();

        final float avatarGap = 1;
        final float startX = 10 + AVATAR_WIDTH * 0.5f;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }
            final String avatar = card.getAvatar();
            final ITextureRegion texture = StringUtils.isEmpty(avatar) ? textureFactory.getAssetTextureRegion(TextureEnum.COMMON_DEFAULT_AVATAR)
                    : textureFactory.getTextureRegion(avatar);
            final Sprite cardSprite = new Sprite(startX + (AVATAR_WIDTH + avatarGap) * i, AVATAR_HEIGHT * 0.5f, AVATAR_WIDTH, AVATAR_HEIGHT, texture, vbom);
            cardSprites[i] = cardSprite;
            this.attachChild(cardSprite);
        }

    }

    private void createTopFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, 0, AVATAR_HEIGHT - 5, false);
        hpBar = new HpBar(156, 22, activity, party.getHp(), false);
        battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
    }

    private void createBottomFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, 0, 0, true);
        hpBar = new HpBar(158, 61, activity, party.getHp(), true);
        battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
    }

    private Sprite createPartyFrame(final TextureEnum textureEnum, final float x, final float y, final boolean isBottom) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite partyFrame = new Sprite(pX, pY, texture, vbom);
        return partyFrame;
    }

    public void useSkill(final int cardIndex, final OnFinishedCallback onFirstStepFinishedCallback, final OnFinishedCallback onFinishedCallback) {
        final Sprite cardSprite = cardSprites[cardIndex];
        final float x = cardSprite.getX();
        final float fromY = cardSprite.getY();
        final float toY = (isBottom ? fromY + 40 : fromY - 30);

        final IEntityModifier firstStepModifier = new MoveModifier(0.2f, x, fromY, x, toY);
        final IEntityModifier secondStepModifier = new MoveModifier(0.2f, x, toY, x, fromY);
        firstStepModifier.addModifierListener(new ModifierFinishedListener(onFirstStepFinishedCallback));
        secondStepModifier.addModifierListener(new ModifierFinishedListener(onFinishedCallback));

        final IEntityModifier cardModifier = new SequenceEntityModifier(firstStepModifier, new DelayModifier(0.2f), secondStepModifier, new DelayModifier(1f));
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                cardSprite.clearEntityModifiers();
                cardSprite.registerEntityModifier(cardModifier);
            }
        });

    }

    public int getAtk() {
        return atk;
    }

    public void setAtk(final int atk) {
        final AtkModifier modifier = new AtkModifier(0.5f, this.atk, atk);
        this.atk = atk;
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                atkText.clearEntityModifiers();
                atkText.registerEntityModifier(modifier);
            }
        });
    }

    public int getDefence() {
        return defence;
    }

    public void setDefence(final int defence) {
        this.defence = defence;
    }

    private class AtkModifier extends SingleValueSpanEntityModifier {

        public AtkModifier(final float pDuration, final float pFromValue, final float pToValue) {
            super(pDuration, pFromValue, pToValue);
        }

        @Override
        protected void onSetInitialValue(final IEntity pItem, final float pValue) {
            final int atk = (int) pValue;
            final Text atkText = (Text) pItem;
            atkText.setText(String.valueOf(atk));
        }

        @Override
        protected void onSetValue(final IEntity pItem, final float pPercentageDone, final float pValue) {
            final int atk = (int) pValue;
            final Text atkText = (Text) pItem;
            atkText.setText(String.valueOf(atk));
        }

        protected AtkModifier(final AtkModifier modifier) {
            super(modifier);
        }

        @Override
        public AtkModifier deepCopy() throws org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException {
            return new AtkModifier(this);
        }

    }

    public HpBar getHpBar() {
        return hpBar;
    }

    public void setHpBar(final HpBar hpBar) {
        this.hpBar = hpBar;
    }

    public int getHp() {
        return hpBar.getCurrentPoint();
    }

    public void setHp(final int hp) {
        this.hpBar.setCurrentPoint(hp);
    }

    public float getInitX() {
        return initX;
    }

    public float getInitY() {
        return initY;
    }

    public boolean isBottom() {
        return isBottom;
    }

}

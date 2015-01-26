package com.fight2.entity.engine;

import java.io.IOException;

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
import com.fight2.entity.Card;
import com.fight2.entity.Party;
import com.fight2.scene.BattleScene.ModifierFinishedListener;
import com.fight2.scene.BattleScene.OnFinishedCallback;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
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
    private final Font font = ResourceManager.getInstance().newFont(FontEnum.Main);
    private final Text atkText;
    private int atk;
    private int defence;
    private final Party party;
    private final IEntity[] cardSprites = new IEntity[4];
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

            final CardFrame cardSprite = new CardFrame(startX + 65 * i, 80, CARD_WIDTH, CARD_HEIGHT, card, activity);
            cardSprites[i] = cardSprite;
            final ITextureRegion textureFcs = textureFactory.getAssetTextureRegion(TextureEnum.BATTLE_CARD_SKILL_FCS);
            final Sprite cardSpriteFcs = new Sprite(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, textureFcs, vbom);
            cardSpriteFcs.setVisible(false);
            cardSprite.attachChild(cardSpriteFcs);
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
            final ITextureRegion texture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
            final Sprite cardSprite = new Sprite(startX + (AVATAR_WIDTH + avatarGap) * i, AVATAR_HEIGHT * 0.5f, AVATAR_WIDTH, AVATAR_HEIGHT, texture, vbom);
            cardSprites[i] = cardSprite;
            final IAsyncCallback callback = new IAsyncCallback() {
                private String avatar;

                @Override
                public void workToDo() {
                    try {
                        if (card.getAvatar() != null && !card.isAvatarLoaded()) {
                            avatar = ImageUtils.getLocalString(card.getAvatar(), activity);
                            card.setAvatar(avatar);
                            card.setAvatarLoaded(true);
                        } else {
                            avatar = card.getAvatar();
                        }
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onComplete() {

                    if (avatar != null) {
                        final ITextureRegion texture = textureFactory.newTextureRegion(avatar);
                        final Sprite imageSprite = new Sprite(AVATAR_WIDTH * 0.5f, AVATAR_HEIGHT * 0.5f, AVATAR_WIDTH, AVATAR_HEIGHT, texture, vbom);
                        cardSprite.attachChild(imageSprite);
                    }

                }

            };

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AsyncTaskLoader().execute(callback);
                }
            });

            final ITextureRegion textureFcs = textureFactory.getAssetTextureRegion(TextureEnum.BATTLE_AVATAR_SKILL_FCS);
            final Sprite cardSpriteFcs = new Sprite(AVATAR_WIDTH * 0.5f, AVATAR_HEIGHT * 0.5f, textureFcs, vbom);
            cardSpriteFcs.setVisible(false);
            cardSprite.attachChild(cardSpriteFcs);
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
        final IEntity cardSprite = cardSprites[cardIndex];
        final float x = cardSprite.getX();
        final float fromY = cardSprite.getY();
        final float toY = (isBottom ? fromY + 40 : fromY - 30);

        final IEntity cardSpriteFcs = cardSprite.getChildByIndex(0);
        cardSpriteFcs.setVisible(true);

        final IEntityModifier firstStepModifier = new MoveModifier(0.2f, x, fromY, x, toY);
        final IEntityModifier secondStepModifier = new MoveModifier(0.2f, x, toY, x, fromY);
        firstStepModifier.addModifierListener(new ModifierFinishedListener(onFirstStepFinishedCallback));
        secondStepModifier.addModifierListener(new ModifierFinishedListener(new OnFinishedCallback() {
            @Override
            public void onFinished(final IEntity pItem) {
                cardSpriteFcs.setVisible(false);
                onFinishedCallback.onFinished(pItem);
            }
        }));

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

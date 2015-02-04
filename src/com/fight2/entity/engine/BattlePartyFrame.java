package com.fight2.entity.engine;

import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.ComboSkill;
import com.fight2.entity.Party;
import com.fight2.scene.BattleScene.ModifierFinishedListener;
import com.fight2.scene.BattleScene.OnFinishedCallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class BattlePartyFrame extends Rectangle {
    final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    public final static int WIDTH = 312;
    public final static int HEIGHT = 165;
    public final static int CARD_WIDTH = 100;
    public final static int CARD_HEIGHT = 150;
    public final static int AVATAR_WIDTH = 73;
    public final static int AVATAR_HEIGHT = 73;
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
    private final IEntity fcsSprite;
    private final boolean isBottom;
    private final GameActivity activity;
    private final SparseArray<Sprite> comboSpriteMap = new SparseArray<Sprite>();
    private final IEntity cardContainer;
    private static final SimplePoint[] POINTS = { point(0, 0), point(-2, 2), point(0, 4), point(1, 4), point(2, 3), point(2, -3), point(1, -3), point(-2, 0),
            point(-2, 1), point(0, 1), point(1, 0), point(0, -1), point(-1, 0), point(0, 2), point(0, 0) };

    public BattlePartyFrame(final float pX, final float pY, final Party party, final GameActivity activity, final boolean isBottom) {
        super(pX + WIDTH * 0.5f, pY + HEIGHT * 0.5f, WIDTH, HEIGHT, activity.getVertexBufferObjectManager());
        super.setAlpha(0);
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.party = party;
        this.isBottom = isBottom;
        this.cardContainer = new Rectangle(WIDTH * 0.5f, HEIGHT * 0.5f, WIDTH, HEIGHT, vbom);
        cardContainer.setAlpha(0);
        this.attachChild(cardContainer);
        if (isBottom) {
            this.createBottomCard(party);
            this.createBottomFrame(party);
            atkText = new Text(91, 26, font, "0123456789", vbom);
            final ITextureRegion textureFcs = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.BATTLE_CARD_SKILL_FCS);
            fcsSprite = new Sprite(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, textureFcs, vbom);
        } else {
            final float height = 81 + AVATAR_HEIGHT - 5;
            this.setHeight(height);
            this.setY(pY + height * 0.5f);
            this.createTopCard(party);
            this.createTopFrame(party);
            atkText = new Text(91, 125, font, "0123456789", vbom);
            final ITextureRegion textureFcs = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.BATTLE_AVATAR_SKILL_FCS);
            fcsSprite = new Sprite(AVATAR_WIDTH * 0.5f, AVATAR_HEIGHT * 0.5f, textureFcs, vbom);
        }
        fcsSprite.setVisible(false);
        this.attachChild(fcsSprite);
        this.initX = this.getX();
        this.initY = this.getY();
        atkText.setText(String.valueOf(party.getAtk()));
        this.attachChild(atkText);
        atk = party.getAtk();
    }

    private void createBottomCard(final Party party) {
        final float startX = 60;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }

            final CardFrame cardSprite = new CardFrame(startX + 65 * i, 80, CARD_WIDTH, CARD_HEIGHT, card, activity);
            cardSprites[i] = cardSprite;
            cardContainer.attachChild(cardSprite);
        }
    }

    private void createTopCard(final Party party) {
        final float avatarGap = 0;
        final float startX = 10 + AVATAR_WIDTH * 0.5f;
        final Card[] cards = party.getCards();
        for (int i = 0; i < cards.length; i++) {
            final Card card = cards[i];
            if (card == null) {
                continue;
            }
            final CardAvatar avatar = new CardAvatar(startX + (AVATAR_WIDTH + avatarGap) * i, AVATAR_HEIGHT * 0.5f, AVATAR_WIDTH, AVATAR_HEIGHT, card, activity);
            cardSprites[i] = avatar;
            cardContainer.attachChild(avatar);
        }
    }

    private void createTopFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_TOP, 0, AVATAR_HEIGHT - 5, false);
        hpBar = new HpBar(156, 22, activity, party.getHp(), false);
        battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
        final List<ComboSkill> comboSkills = party.getComboSkills();
        int iconX = 166;
        for (final ComboSkill comboSkill : comboSkills) {
            final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(comboSkill.getIcon());
            final Sprite iconSprite = new Sprite(iconX, 58.5f, 30, 30, texture, vbom);
            battlePartyFrame.attachChild(iconSprite);
            iconX += 37;
            comboSpriteMap.put(comboSkill.getId(), iconSprite);
        }
    }

    private void createBottomFrame(final Party party) {
        final Sprite battlePartyFrame = createPartyFrame(TextureEnum.BATTLE_PARTY_BOTTOM, 0, 0, true);
        hpBar = new HpBar(158, 61, activity, party.getHp(), true);
        battlePartyFrame.attachChild(hpBar);
        this.attachChild(battlePartyFrame);
        final List<ComboSkill> comboSkills = party.getComboSkills();
        int iconX = 167;
        for (final ComboSkill comboSkill : comboSkills) {
            final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(comboSkill.getIcon());
            final Sprite iconSprite = new Sprite(iconX, 26.5f, 31, 31, texture, vbom);
            battlePartyFrame.attachChild(iconSprite);
            iconX += 39;
            comboSpriteMap.put(comboSkill.getId(), iconSprite);
        }
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

    public void removeComboSprite(final int comboId) {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                comboSpriteMap.get(comboId).detachSelf();
            }
        });
    }

    public void useSkill(final int cardIndex, final OnFinishedCallback onFirstStepFinishedCallback, final OnFinishedCallback onFinishedCallback) {
        final IEntity cardSprite = cardSprites[cardIndex];
        final float x = cardSprite.getX();
        final float fromY = cardSprite.getY();
        final float toY = (isBottom ? fromY + 40 : fromY - 30);

        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                fcsSprite.detachSelf();
                cardSprite.attachChild(fcsSprite);
                fcsSprite.setVisible(true);
            }
        });

        final IEntityModifier firstStepModifier = new MoveModifier(0.2f, x, fromY, x, toY);
        final IEntityModifier secondStepModifier = new MoveModifier(0.2f, x, toY, x, fromY);
        firstStepModifier.addModifierListener(new ModifierFinishedListener(onFirstStepFinishedCallback));
        secondStepModifier.addModifierListener(new ModifierFinishedListener(new OnFinishedCallback() {
            @Override
            public void onFinished(final IEntity pItem) {
                fcsSprite.setVisible(false);
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

    public void attack(final OnFinishedCallback upFinishedCallback, final OnFinishedCallback downFinishedCallback) {
        final float initX = cardContainer.getX();
        final float initY = cardContainer.getY();
        final float toY = isBottom ? 105 : -80;
        final float returnY = isBottom ? initY - 20 : initY + 15;
        final IEntityModifier upModifier = new MoveModifier(0.08f, initX, initY, initX, initY + toY, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {

            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                upFinishedCallback.onFinished(pItem);
            }
        });
        final IEntityModifier downModifier = new MoveModifier(0.11f, initX, initY + toY, initX, returnY);
        final IEntityModifier returnModifier = new MoveModifier(0.11f, initX, returnY, initX, initY, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem) {
            }

            @Override
            public void onModifierFinished(final IModifier<IEntity> pModifier, final IEntity pItem) {
                downFinishedCallback.onFinished(pItem);
            }
        });
        final IEntityModifier modifier = new SequenceEntityModifier(new DelayModifier(0.3f), upModifier, downModifier, returnModifier);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                cardContainer.clearEntityModifiers();
                cardContainer.registerEntityModifier(modifier);
            }
        });
    }

    public void beenHit() {
        final Path path = new Path(POINTS.length);
        final float initX = cardContainer.getX();
        final float initY = cardContainer.getY();
        final float offset = 2f;
        for (final SimplePoint point : POINTS) {
            path.to(initX + point.getX() * offset, initY + point.getY() * offset);
        }
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                cardContainer.clearEntityModifiers();
                cardContainer.registerEntityModifier(new PathModifier(0.4f, path));
            }
        });
    }

    public void useCombo(final int comboId) {
        final IEntityModifier scaleModifier = new ScaleModifier(1f, 1, 1.8f);
        final IEntityModifier alphaModifier = new AlphaModifier(1f, 1, 0f);
        final IEntityModifier modifier = new SequenceEntityModifier(scaleModifier, alphaModifier);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                comboSpriteMap.get(comboId).registerEntityModifier(modifier);
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

    public Party getParty() {
        return party;
    }

    private static SimplePoint point(final float x, final float y) {
        return new SimplePoint(x, y);
    }

}

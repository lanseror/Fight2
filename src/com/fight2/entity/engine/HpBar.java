package com.fight2.entity.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
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
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class HpBar extends Rectangle {
    private static final float WIDTH = TextureEnum.COMMON_HP_GREEN.getWidth();
    private static final float HEIGHT = TextureEnum.COMMON_HP_GREEN.getHeight();
    private static final float EDGE_WIDTH = 10;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final TextureEnum mainTextureEnum;
    private final TextureEnum hpRightTextureEnum;
    private final int fullHp;
    private final BigDecimal bigFullPoint;
    private int currentPoint;
    private ClipEntity mainClipEntity;
    private ClipEntity rightClipEntity;
    private Sprite rightHpSprite;
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Main);
    private final Text hpText;

    public HpBar(final float pX, final float pY, final GameActivity activity, final int fullHp) {
        this(pX, pY, activity, fullHp, false);
    }

    public HpBar(final float pX, final float pY, final GameActivity activity, final int fullHp, final boolean isGreenHp) {
        super(pX, pY, WIDTH, HEIGHT, activity.getVertexBufferObjectManager());
        super.setAlpha(0);
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.fullHp = fullHp;
        this.currentPoint = fullHp;
        this.bigFullPoint = BigDecimal.valueOf(fullHp);
        hpText = new Text(WIDTH * 0.5f, HEIGHT * 0.5f + 1, font, "0123456789", vbom);
        hpText.setText(String.valueOf(fullHp));

        if (isGreenHp) {
            mainTextureEnum = TextureEnum.COMMON_HP_GREEN;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_GREEN;
        } else {
            mainTextureEnum = TextureEnum.COMMON_HP_RED;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_RED;
        }
        initHP();
        this.attachChild(hpText);
    }

    private void initHP() {
        final Sprite initHpSprite = createSprite(mainTextureEnum, 0, 0);
        mainClipEntity = new ClipEntity(WIDTH * 0.5f, HEIGHT * 0.5f, WIDTH, HEIGHT);
        mainClipEntity.attachChild(initHpSprite);
        rightHpSprite = createSprite(hpRightTextureEnum, 0, 0);
        rightClipEntity = new ClipEntity(WIDTH - EDGE_WIDTH * 0.5f, HEIGHT * 0.5f, EDGE_WIDTH, HEIGHT);
        rightClipEntity.attachChild(rightHpSprite);

        this.attachChild(mainClipEntity);
        this.attachChild(rightClipEntity);
    }

    public int getFullHp() {
        return fullHp;
    }

    public int getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(final int currentPoint) {
        final int toPoint = currentPoint > fullHp ? fullHp : currentPoint;
        final int fromHp = this.currentPoint;
        final float duration = fromHp > toPoint ? 0.2f : 1f;
        final HpBarModifier modifier = new HpBarModifier(duration, fromHp, toPoint);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                HpBar.this.clearEntityModifiers();
                HpBar.this.registerEntityModifier(modifier);
            }
        });

        this.currentPoint = toPoint;
    }

    private void changeHp(final float currentPoint) {
        final int toPoint = (int) currentPoint;
        final BigDecimal bigCurrentPoint = BigDecimal.valueOf(toPoint);
        final BigDecimal factor = bigCurrentPoint.divide(bigFullPoint, 4, RoundingMode.HALF_UP);
        final float currentWidth = factor.multiply(BigDecimal.valueOf(WIDTH)).floatValue();

        if (currentWidth < EDGE_WIDTH * 2f) {
            final float hpEdgeWidth = currentWidth * 0.5f;
            mainClipEntity.setWidth(hpEdgeWidth);
            mainClipEntity.setX(hpEdgeWidth * 0.5f);
            rightClipEntity.setWidth(hpEdgeWidth);
            rightClipEntity.setX(hpEdgeWidth * 1.5f);
            rightHpSprite.setX(hpEdgeWidth - EDGE_WIDTH + EDGE_WIDTH * 0.5f);
        } else {
            mainClipEntity.setWidth(currentWidth - EDGE_WIDTH);
            mainClipEntity.setX(mainClipEntity.getWidth() * 0.5f);
            rightClipEntity.setWidth(EDGE_WIDTH);
            rightClipEntity.setX(currentWidth - EDGE_WIDTH * 0.5f);
        }
        hpText.setText(String.valueOf(toPoint));
    }

    private Sprite createSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, texture, vbom);
        return sprite;
    }

    private class HpBarModifier extends SingleValueSpanEntityModifier {

        public HpBarModifier(final float pDuration, final float pFromValue, final float pToValue) {
            super(pDuration, pFromValue, pToValue);
        }

        @Override
        protected void onSetInitialValue(final IEntity pItem, final float pValue) {
            final HpBar hpBar = (HpBar) pItem;
            hpBar.changeHp(pValue);
        }

        @Override
        protected void onSetValue(final IEntity pItem, final float pPercentageDone, final float pValue) {
            final HpBar hpBar = (HpBar) pItem;
            hpBar.changeHp(pValue);
        }

        protected HpBarModifier(final HpBarModifier modifier) {
            super(modifier);
        }

        @Override
        public HpBarModifier deepCopy() throws org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException {
            return new HpBarModifier(this);
        }

    }

}

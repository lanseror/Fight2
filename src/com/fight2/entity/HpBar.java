package com.fight2.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TextureEnum;
import com.fight2.util.TextureFactory;

public class HpBar extends Rectangle {
    private static final int HEIGHT = 30;
    private static final int WIDTH = 284;
    private static final int EDGE_WIDTH = 18;
    private static final int CENTER_WIDTH = WIDTH - EDGE_WIDTH;
    private static final int CENTER_X = EDGE_WIDTH / 2;
    private final VertexBufferObjectManager vbom;
    private final TextureEnum hpLeftTextureEnum;
    private final TextureEnum hpCenterTextureEnum;
    private final TextureEnum hpRightTextureEnum;
    private final int fullHp;
    private final BigDecimal bigFullPoint;
    private int currentPoint;
    private final Sprite[] centerHpSprites = new Sprite[CENTER_WIDTH];

    public HpBar(final float pX, final float pY, final VertexBufferObjectManager pVertexBufferObjectManager, final int fullHp) {
        this(pX, pY, pVertexBufferObjectManager, fullHp, false);
    }

    public HpBar(final float pX, final float pY, final VertexBufferObjectManager pVertexBufferObjectManager, final int fullHp, final boolean isGreenHp) {
        super(pX, pY, WIDTH, HEIGHT, pVertexBufferObjectManager);
        super.setAlpha(0);
        this.vbom = pVertexBufferObjectManager;
        this.fullHp = fullHp;
        this.currentPoint = fullHp;
        this.bigFullPoint = BigDecimal.valueOf(fullHp);

        if (isGreenHp) {
            hpLeftTextureEnum = TextureEnum.COMMON_HP_LEFT_GREEN;
            hpCenterTextureEnum = TextureEnum.COMMON_HP_CENTER_GREEN;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_GREEN;
        } else {
            hpLeftTextureEnum = TextureEnum.COMMON_HP_LEFT_RED;
            hpCenterTextureEnum = TextureEnum.COMMON_HP_CENTER_RED;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_RED;
        }
        initHP();
    }

    private void initHP() {
        final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpLeftTextureEnum.getWidth(), 0, 0, true);
        this.attachChild(hpLeft);
        int i = 0;
        for (; i < CENTER_WIDTH; i++) {
            final Sprite hpCenter = createHpCenter(CENTER_X + i, 0);
            centerHpSprites[i] = hpCenter;
            this.attachChild(hpCenter);
        }
        final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpRightTextureEnum.getWidth(), CENTER_X + i, 0, false);
        this.attachChild(hpRight);
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
        this.clearEntityModifiers();
        this.registerEntityModifier(modifier);
        this.currentPoint = toPoint;
    }

    private void changeHp(final float currentPoint) {
        final BigDecimal bigCurrentPoint = BigDecimal.valueOf(currentPoint);
        final BigDecimal factor = bigCurrentPoint.divide(bigFullPoint, 4, RoundingMode.HALF_UP);
        final int currentWidth = factor.multiply(BigDecimal.valueOf(WIDTH)).intValue();
        this.detachChildren();

        if (currentWidth < EDGE_WIDTH) {
            final int hpEdgeWidth = currentWidth / 2;
            final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpEdgeWidth, 0, 0, true);
            final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpEdgeWidth, hpEdgeWidth, 0, false);
            this.attachChild(hpLeft);
            this.attachChild(hpRight);
        } else {
            final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpLeftTextureEnum.getWidth(), 0, 0, true);
            this.attachChild(hpLeft);
            int i = 0;
            for (; i < currentWidth - EDGE_WIDTH && i < centerHpSprites.length; i++) {
                final Sprite hpCenter = centerHpSprites[i];
                this.attachChild(hpCenter);
            }
            final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpRightTextureEnum.getWidth(), CENTER_X + i, 0, false);
            this.attachChild(hpRight);
        }
    }

    private Sprite createHpEdge(final TextureEnum textureEnum, final float width, final float x, final float y, final boolean isLeft) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        texture.setTextureWidth(width);
        if (isLeft) {
            texture.setTextureX(0);
        } else {
            texture.setTextureX(textureEnum.getWidth() - width);
        }
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, texture, vbom);
        return sprite;
    }

    private Sprite createHpCenter(final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(hpCenterTextureEnum);
        final float width = hpCenterTextureEnum.getWidth();
        final float height = hpCenterTextureEnum.getHeight();
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

package com.fight2.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.constant.TextureEnum;
import com.fight2.util.TextureFactory;

public class BattlePartyFrame extends Sprite {
    private final VertexBufferObjectManager vbom;
    private final TextureEnum hpLeftTextureEnum;
    private final TextureEnum hpCenterTextureEnum;
    private final TextureEnum hpRightTextureEnum;
    private int hpLeftX;
    private int hpRightX;
    private final int hp_y;
    private final int hpCenterWidth;
    private final int hpCenterX;
    private final int hpEdgeWidth = 18;
    private final int hpTotalWidth;
    private int fullPoint;
    private BigDecimal bigFullPoint;
    private int currentPoint;

    public BattlePartyFrame(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager,
            final boolean isBottom) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.vbom = pVertexBufferObjectManager;
        if (isBottom) {
            hpLeftX = 16;
            hpRightX = 291;
            hp_y = 46;
            hpLeftTextureEnum = TextureEnum.COMMON_HP_LEFT_GREEN;
            hpCenterTextureEnum = TextureEnum.COMMON_HP_CENTER_GREEN;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_GREEN;
        } else {
            hpLeftX = 14;
            hpRightX = 289;
            hp_y = 7;
            hpLeftTextureEnum = TextureEnum.COMMON_HP_LEFT_RED;
            hpCenterTextureEnum = TextureEnum.COMMON_HP_CENTER_RED;
            hpRightTextureEnum = TextureEnum.COMMON_HP_RIGHT_RED;
        }
        hpCenterWidth = hpRightX - hpLeftX - 9;
        hpCenterX = hpRightX - hpCenterWidth;
        hpTotalWidth = hpRightX - hpLeftX + 9;
    }

    private void initHP() {
        final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpLeftTextureEnum.getWidth(), hpLeftX, hp_y, false);
        final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpRightTextureEnum.getWidth(), hpRightX, hp_y, true);
        this.attachChild(hpLeft);
        this.attachChild(hpRight);
        for (int i = 0; i < hpCenterWidth; i++) {
            final Sprite hpCenter = createHpCenter(hpCenterX + i, hp_y);
            this.attachChild(hpCenter);
        }
    }

    private Sprite createHpEdge(final TextureEnum textureEnum, final float width, final float x, final float y, final boolean isRight) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        texture.setTextureWidth(width);
        if (isRight) {
            texture.setTextureX(textureEnum.getWidth() - width);
        } else {
            texture.setTextureX(0);
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

    public int getFullPoint() {
        return fullPoint;
    }

    public void setFullPoint(final int fullPoint) {
        this.fullPoint = fullPoint;
        bigFullPoint = BigDecimal.valueOf(fullPoint);
        initHP();
    }

    public int getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(final int currentPoint) {
        this.currentPoint = currentPoint;
        final BigDecimal bigCurrentPoint = BigDecimal.valueOf(currentPoint);
        final BigDecimal factor = bigCurrentPoint.divide(bigFullPoint, 4, RoundingMode.HALF_UP);
        final int currentWidth = factor.multiply(BigDecimal.valueOf(hpTotalWidth)).intValue();
        this.detachChildren();

        if (currentWidth < hpEdgeWidth) {
            final int hpEdgeWidth = currentWidth / 2;
            final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpEdgeWidth, hpLeftX, hp_y, false);
            final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpEdgeWidth, hpLeftX + hpEdgeWidth, hp_y, true);
            this.attachChild(hpLeft);
            this.attachChild(hpRight);
        } else {
            final Sprite hpLeft = createHpEdge(hpLeftTextureEnum, hpLeftTextureEnum.getWidth(), hpLeftX, hp_y, false);
            this.attachChild(hpLeft);
            int i = 0;
            for (; i < currentWidth - hpEdgeWidth; i++) {
                final Sprite hpCenter = createHpCenter(hpCenterX + i, hp_y);
                this.attachChild(hpCenter);
            }
            final Sprite hpRight = createHpEdge(hpRightTextureEnum, hpRightTextureEnum.getWidth(), hpCenterX + i, hp_y, true);
            this.attachChild(hpRight);
        }
    }
}

package com.fight2.entity.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.util.EntityFactory;

public class ProgressBar extends HUD {
    private static final float WIDTH = TextureEnum.COMMON_PROGRESS_BAR.getWidth();
    private static final float HEIGHT = TextureEnum.COMMON_PROGRESS_BAR.getHeight();
    private static final float EDGE_WIDTH = TextureEnum.COMMON_PROGRESS_BAR_RIGHT.getWidth();
    private final TextureEnum textureEnum = TextureEnum.COMMON_PROGRESS_BAR;
    private final TextureEnum textureEnumRight = TextureEnum.COMMON_PROGRESS_BAR_RIGHT;

    private final GameActivity activity;
    private final ClipEntity mainClipEntity;
    private final ClipEntity rightClipEntity;
    private final Sprite rightHpSprite;

    private int currentPercent;

    public ProgressBar(final float pX, final float pY, final GameActivity activity) {
        super();
        super.setCamera(activity.getCamera());
        this.activity = activity;

        final Sprite initHpSprite = EntityFactory.getInstance().createALBImageSprite(textureEnum, 0, 0);
        mainClipEntity = new ClipEntity(WIDTH * 0.5f, HEIGHT * 0.5f, 1, HEIGHT);
        mainClipEntity.attachChild(initHpSprite);
        rightHpSprite = EntityFactory.getInstance().createALBImageSprite(textureEnumRight, 0, 0);
        rightClipEntity = new ClipEntity(WIDTH - EDGE_WIDTH * 0.5f, HEIGHT * 0.5f, 1, HEIGHT);
        rightClipEntity.attachChild(rightHpSprite);

        final Rectangle container = new Rectangle(pX, pY, WIDTH, HEIGHT, activity.getVertexBufferObjectManager());
        container.setAlpha(0);
        this.attachChild(container);
        container.attachChild(mainClipEntity);
        container.attachChild(rightClipEntity);
    }

    public void setPercent(final int percentage) {
        for (int i = this.currentPercent * 10; i < percentage * 10; i++) {
            this.changePercent(i * 0.1f);
            try {
                Thread.sleep(2);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.currentPercent = percentage;
    }

    private void changePercent(final float percentage) {
        final int toPoint = (int) percentage;
        final float currentWidth = BigDecimal.valueOf(WIDTH).multiply(BigDecimal.valueOf(toPoint)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .floatValue();

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
    }

    private class ProgressBarModifier extends SingleValueSpanEntityModifier {

        public ProgressBarModifier(final float pDuration, final float pFromValue, final float pToValue) {
            super(pDuration, pFromValue, pToValue);
        }

        @Override
        protected void onSetInitialValue(final IEntity pItem, final float pValue) {
            final ProgressBar progressBar = (ProgressBar) pItem;
            progressBar.changePercent(pValue);
        }

        @Override
        protected void onSetValue(final IEntity pItem, final float pPercentageDone, final float pValue) {
            final ProgressBar progressBar = (ProgressBar) pItem;
            progressBar.changePercent(pValue);
        }

        protected ProgressBarModifier(final ProgressBarModifier modifier) {
            super(modifier);
        }

        @Override
        public ProgressBarModifier deepCopy() throws org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException {
            return new ProgressBarModifier(this);
        }

    }
}
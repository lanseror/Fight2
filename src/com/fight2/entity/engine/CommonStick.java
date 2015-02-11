package com.fight2.entity.engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.EntityFactory;
import com.fight2.util.ResourceManager;

public class CommonStick extends Entity {
    public static final String TEXT_FORMAT = "%s/%s";
    private final float width;
    private final float height;
    private final float edgeWidth;
    private final ClipEntity mainClipEntity;
    private final ClipEntity rightClipEntity;
    private final Sprite rightSprite;
    private final GameActivity activity;
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
    private final Text text;

    private final int fullValue;
    private int currentValue;

    public CommonStick(final float pX, final float pY, final TextureEnum mainEnum, final TextureEnum rightEnum, final GameActivity activity, final int fullValue) {
        super(pX, pY, mainEnum.getWidth(), mainEnum.getHeight());
        this.activity = activity;
        this.fullValue = fullValue;
        this.width = mainEnum.getWidth();
        this.height = mainEnum.getHeight();
        this.edgeWidth = rightEnum.getWidth();
        final Sprite initSprite = EntityFactory.getInstance().createALBImageSprite(mainEnum, 0, 0);
        mainClipEntity = new ClipEntity(width * 0.5f, height * 0.5f, width, height);
        mainClipEntity.attachChild(initSprite);
        rightSprite = EntityFactory.getInstance().createALBImageSprite(rightEnum, 0, 0);
        rightClipEntity = new ClipEntity(width - edgeWidth * 0.5f, height * 0.5f, 0, height);
        rightClipEntity.attachChild(rightSprite);

        this.attachChild(mainClipEntity);
        this.attachChild(rightClipEntity);
        this.text = new Text(width * 0.5f, height * 0.5f, font, String.format(TEXT_FORMAT, fullValue, fullValue), activity.getVertexBufferObjectManager());
        this.attachChild(text);
        this.currentValue = fullValue;
    }

    public void setValue(final int value) {
        setValue(value, false);
    }

    public void setValue(final int value, final boolean immediately) {
        final int toValue = value > fullValue ? fullValue : value;
        final int fromValue = this.currentValue;
        final float duration = fromValue > toValue ? 0.2f : 0.2f;
        if (!immediately) {
            final CommonStickModifier modifier = new CommonStickModifier(duration, fromValue, toValue);
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    CommonStick.this.clearEntityModifiers();
                    CommonStick.this.registerEntityModifier(modifier);
                }
            });
        } else {
            changeValue(toValue);
        }
        this.currentValue = toValue;
        text.setText(String.format(TEXT_FORMAT, currentValue, fullValue));
    }

    private void changeValue(final float value) {
        final int toPoint = (int) value;
        final float currentWidth = BigDecimal.valueOf(width).multiply(BigDecimal.valueOf(toPoint)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .floatValue();

        if (currentWidth < edgeWidth * 2f) {
            final float changeEdgeWidth = currentWidth * 0.5f;
            mainClipEntity.setWidth(changeEdgeWidth);
            mainClipEntity.setX(changeEdgeWidth * 0.5f);
            rightClipEntity.setWidth(changeEdgeWidth);
            rightClipEntity.setX(changeEdgeWidth * 1.5f);
            rightSprite.setX(changeEdgeWidth - changeEdgeWidth + changeEdgeWidth * 0.5f);
        } else {
            mainClipEntity.setWidth(currentWidth - edgeWidth);
            mainClipEntity.setX(mainClipEntity.getWidth() * 0.5f);
            rightClipEntity.setWidth(edgeWidth);
            rightClipEntity.setX(currentWidth - edgeWidth * 0.5f);
        }
    }

    private class CommonStickModifier extends SingleValueSpanEntityModifier {

        public CommonStickModifier(final float pDuration, final float pFromValue, final float pToValue) {
            super(pDuration, pFromValue, pToValue);
        }

        @Override
        protected void onSetInitialValue(final IEntity pItem, final float pValue) {
            final CommonStick stick = (CommonStick) pItem;
            stick.changeValue(pValue);
        }

        @Override
        protected void onSetValue(final IEntity pItem, final float pPercentageDone, final float pValue) {
            final CommonStick stick = (CommonStick) pItem;
            stick.changeValue(pValue);
        }

        protected CommonStickModifier(final CommonStickModifier modifier) {
            super(modifier);
        }

        @Override
        public CommonStickModifier deepCopy() throws org.andengine.util.modifier.IModifier.DeepCopyNotSupportedException {
            return new CommonStickModifier(this);
        }

    }
}
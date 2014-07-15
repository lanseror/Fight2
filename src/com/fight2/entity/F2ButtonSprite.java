package com.fight2.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class F2ButtonSprite extends Sprite {
    private F2OnClickListener mOnClickListener;
    private boolean isPressed = false;

    public F2ButtonSprite(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
            this.setScale(1.2f);
            isPressed = true;
        } else if (pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
            this.setScale(1f);
        } else if (pSceneTouchEvent.isActionUp() && isPressed) {
            this.setScale(1f);
            if (this.mOnClickListener != null) {
                this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        }

        return true;
    }

    public void setOnClickListener(final F2OnClickListener pOnClickListener) {
        this.mOnClickListener = pOnClickListener;
    }

    public interface F2OnClickListener {
        // ===========================================================
        // Constants
        // ===========================================================

        // ===========================================================
        // Methods
        // ===========================================================

        public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY);
    }
}

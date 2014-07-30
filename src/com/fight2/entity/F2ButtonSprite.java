package com.fight2.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class F2ButtonSprite extends Sprite {
    protected final ITextureRegion pressedTextureRegion;
    private F2OnClickListener mOnClickListener;
    private State state = State.NORMAL;

    public F2ButtonSprite(final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
        this.pressedTextureRegion = pTextureRegion;
    }

    public F2ButtonSprite(final float pX, final float pY, final ITextureRegion normalTextureRegion, final ITextureRegion pressedTextureRegion,
            final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, normalTextureRegion, pVertexBufferObjectManager);
        this.pressedTextureRegion = pressedTextureRegion;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
            changeState(State.PRESSED);
        } else if (pSceneTouchEvent.isActionCancel() || !this.contains(pSceneTouchEvent.getX(), pSceneTouchEvent.getY())) {
            changeState(State.NORMAL);
        } else if (pSceneTouchEvent.isActionUp() && this.state == State.PRESSED) {
            changeState(State.NORMAL);
            if (this.mOnClickListener != null) {
                this.mOnClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
            }
        }

        return true;
    }

    @Override
    public ITextureRegion getTextureRegion() {
        final State mState = (this.state == null ? State.NORMAL : this.state);
        switch (mState) {
            case NORMAL:
                return this.mTextureRegion;
            case PRESSED:
                return this.pressedTextureRegion;
            default:
                return this.mTextureRegion;
        }
    }

    private void changeState(final State pState) {
        if (pState == this.state) {
            return;
        }
        this.state = pState;
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

    public static enum State {
        NORMAL,
        PRESSED,
        DISABLED;
    }
}

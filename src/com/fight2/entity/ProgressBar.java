package com.fight2.entity;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

/**
 * @author Jong - Yonatan
 * 
 */
public class ProgressBar extends HUD {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final float FRAME_LINE_WIDTH = 5f;
    // ===========================================================
    // Fields
    // ===========================================================
    private final Line[] mFrameLines = new Line[4];
    private final Rectangle mBackgroundRectangle;
    private final Rectangle mProgressRectangle;

    private final float mPixelsPerPercentRatio;

    private final float initX;
    private final float initY;
    private int progress = 0;

    // ===========================================================
    // Constructors
    // ===========================================================
    public ProgressBar(final Camera pCamera, final float pX, final float pY, final float pWidth, final float pHeight, final VertexBufferObjectManager vbo) {
        super();
        super.setCamera(pCamera);
        initX = pX - pWidth * 0.5f;
        initY = pY;

        this.mBackgroundRectangle = new Rectangle(pX, pY, pWidth, pHeight, vbo);

        this.mFrameLines[0] = new Line(pX - pWidth * 0.5f, pY - pHeight * 0.5f, pX + pWidth * 0.5f, pY - pHeight * 0.5f, FRAME_LINE_WIDTH, vbo); // Bottom line.
        this.mFrameLines[1] = new Line(pX + pWidth * 0.5f, pY - pHeight * 0.5f, pX + pWidth * 0.5f, pY + pHeight * 0.5f, FRAME_LINE_WIDTH, vbo); // Right line.
        this.mFrameLines[2] = new Line(pX - pWidth * 0.5f, pY + pHeight * 0.5f, pX + pWidth * 0.5f, pY + pHeight * 0.5f, FRAME_LINE_WIDTH, vbo); // Top line.
        this.mFrameLines[3] = new Line(pX - pWidth * 0.5f, pY - pHeight * 0.5f, pX - pWidth * 0.5f, pY + pHeight * 0.5f, FRAME_LINE_WIDTH, vbo); // Left line.

        this.mProgressRectangle = new Rectangle(pX, pY, 0.0001f, pHeight, vbo);

        super.attachChild(this.mBackgroundRectangle); // This one is drawn first.
        super.attachChild(this.mProgressRectangle); // The progress is drawn afterwards.
        for (int i = 0; i < this.mFrameLines.length; i++)
            super.attachChild(this.mFrameLines[i]); // Lines are drawn last, so they'll override everything.

        this.mPixelsPerPercentRatio = pWidth / 100;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
    public void setBackColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
        this.mBackgroundRectangle.setColor(pRed, pGreen, pBlue, pAlpha);
    }

    public void setFrameColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
        for (int i = 0; i < this.mFrameLines.length; i++)
            this.mFrameLines[i].setColor(pRed, pGreen, pBlue, pAlpha);
    }

    public void setProgressColor(final float pRed, final float pGreen, final float pBlue, final float pAlpha) {
        this.mProgressRectangle.setColor(pRed, pGreen, pBlue, pAlpha);
    }

    /**
     * Set the current progress of this progress bar.
     * 
     * @param pProgress
     *            is <b> BETWEEN </b> 0 - 100.
     */
    private void setProgress(final float pProgress) {
        if (pProgress < 0)
            this.mProgressRectangle.setWidth(0); // This is an internal check for my specific game, you can remove it.
        final float width = this.mPixelsPerPercentRatio * pProgress;
        this.mProgressRectangle.setPosition(initX + width * 0.5f, initY);
        this.mProgressRectangle.setWidth(width);
    }

    public void increase(final int progressNum) {
        for (int i = this.progress * 10; i < progressNum * 10; i++) {
            this.setProgress(i * 0.1f);
            try {
                Thread.sleep(2);
            } catch (final InterruptedException e) {
                Debug.e(e);
            }
        }
        this.progress = progressNum;
    }
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
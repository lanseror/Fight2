package com.fight2.entity;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class ScrollZone extends ClipEntity implements IScrollDetectorListener {
    private final VertexBufferObjectManager vbom;
    private final SurfaceScrollDetector scrollDetector;
    private final IEntity scrollContainer;
    private final float containerCenterX;
    private final float containerInitY;
    private float rowHeight = 0;
    private float scrollBottomY = 0;

    public ScrollZone(final float x, final float y, final float width, final float height, final VertexBufferObjectManager vbom) {
        super(x, y, width, height);
        this.vbom = vbom;
        scrollDetector = new SurfaceScrollDetector(this);
        containerCenterX = width * 0.5f;
        containerInitY = height * 0.5f;
        scrollContainer = new Rectangle(containerCenterX, containerInitY, width, height, vbom);
        scrollContainer.setAlpha(0);
        this.attachChild(scrollContainer);
    }

    public IEntity createTouchArea(final float x, final float y, final float width, final float height) {
        final IEntity touchArea = new Rectangle(x, y, width, height, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(touchEvent);
                return true;
            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    public void attachRow(final IEntity row) {
        row.setPosition(containerCenterX, scrollContainer.getHeight() - rowHeight - row.getHeight() * 0.5f);
        scrollContainer.attachChild(row);
        rowHeight += row.getHeight();
        scrollBottomY = rowHeight - containerInitY;
        if (scrollBottomY < containerInitY) {
            scrollBottomY = containerInitY;
        }
    }

    public IEntity getScrollContainer() {
        return scrollContainer;
    }

    private void handleScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        final float scrollToY = scrollContainer.getY() - pDistanceY;
        if (scrollToY < containerInitY) {
            scrollContainer.setY(containerInitY);
        } else {
            if (scrollToY > scrollBottomY) {
                scrollContainer.setY(scrollBottomY);
            } else {
                scrollContainer.setY(scrollToY);
            }
        }
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

    @Override
    public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }
}

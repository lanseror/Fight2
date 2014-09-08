package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;

public class ArenaRewardScene extends BaseScene implements IScrollDetectorListener {
    private final static float CLIP_HEIGHT = 460;
    private final static float CONTAINER_INIT_Y = CLIP_HEIGHT * 0.5f;
    private final float frameWidth = TextureEnum.ARENA_REWARD_BG.getWidth();
    private final float frameHeight = TextureEnum.ARENA_REWARD_BG.getHeight();
    private final float frameCenter = frameWidth * 0.5f;
    private final float frameY = simulatedHeight - frameHeight * 0.5f;
    private final SurfaceScrollDetector scrollDetector;
    private final IEntity mightContainer;
    private final IEntity rankContainer;
    private float scrollMightBottomY = 0;
    private float scrollRankBottomY = 0;

    public ArenaRewardScene(final GameActivity activity) throws IOException {
        super(activity);
        scrollDetector = new SurfaceScrollDetector(this);
        mightContainer = new Rectangle(frameCenter, CONTAINER_INIT_Y, frameWidth, CLIP_HEIGHT, vbom);
        mightContainer.setAlpha(0);
        rankContainer = new Rectangle(frameCenter, CONTAINER_INIT_Y, frameWidth, CLIP_HEIGHT, vbom);
        rankContainer.setAlpha(0);
        rankContainer.setVisible(false);
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.5f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite frameSprite = createACImageSprite(TextureEnum.ARENA_REWARD_BG, cameraCenterX, frameY);
        this.attachChild(frameSprite);

        final IEntity closeTouchArea = new Rectangle(frameWidth - 10, frameHeight - 15, 130, 120, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    back();
                    activity.getGameHub().needSmallChatRoom(true);
                    return true;
                }
                return false;

            }
        };
        closeTouchArea.setAlpha(0);
        frameSprite.attachChild(closeTouchArea);
        this.registerTouchArea(closeTouchArea);

        final TextureEnum descEnum = TextureEnum.ARENA_REWARD_DESC;
        final TextureEnum gridEnum = TextureEnum.ARENA_REWARD_GRID;
        final float gridHeight = gridEnum.getHeight();
        // Might elements
        final TextureEnum mightButtonEnum = TextureEnum.ARENA_REWARD_MIGHT_BUTTON;
        final IEntity mightButton = createACImageSprite(mightButtonEnum, frameCenter, CLIP_HEIGHT - mightButtonEnum.getHeight() * 0.5f);
        mightContainer.attachChild(mightButton);
        final IEntity mightTouchArea = createMightTouchArea();
        mightButton.attachChild(mightTouchArea);
        this.registerTouchArea(mightTouchArea);

        final Sprite mightDesc = createACImageSprite(descEnum, frameCenter, CLIP_HEIGHT - mightButton.getHeight() - descEnum.getHeight() * 0.5f);
        mightContainer.attachChild(mightDesc);

        final float gridInitY = CLIP_HEIGHT - mightButton.getHeight() - descEnum.getHeight() - gridHeight * 0.5f + 2;
        float mightGridBottomY = 0;
        for (int i = 0; i < 3; i++) {
            mightGridBottomY = gridInitY - gridHeight * i;
            final IEntity rewardGrid = createRewardGrid(frameCenter, mightGridBottomY);
            mightContainer.attachChild(rewardGrid);
        }
        scrollMightBottomY = CONTAINER_INIT_Y - mightGridBottomY + gridHeight * 0.5f;
        // Rank elements

        final TextureEnum rankButtonEnum = TextureEnum.ARENA_REWARD_RANK_BUTTON;
        final IEntity rankButton = createACImageSprite(rankButtonEnum, frameCenter, CLIP_HEIGHT - rankButtonEnum.getHeight() * 0.5f);
        rankContainer.attachChild(rankButton);
        final IEntity rankTouchArea = createRankTouchArea();
        rankButton.attachChild(rankTouchArea);
        this.registerTouchArea(rankTouchArea);
        final Sprite rankDesc = createACImageSprite(descEnum, frameCenter, CLIP_HEIGHT - rankButton.getHeight() - descEnum.getHeight() * 0.5f);
        rankContainer.attachChild(rankDesc);

        float rankGridBottomY = 0;
        for (int i = 0; i < 2; i++) {
            rankGridBottomY = gridInitY - gridHeight * i;
            final IEntity rewardGrid = createRewardGrid(frameCenter, rankGridBottomY);
            rankContainer.attachChild(rewardGrid);
        }
        scrollRankBottomY = CONTAINER_INIT_Y - rankGridBottomY + gridHeight * 0.5f;

        final ClipEntity rewardListTouchArea = new ClipEntity(frameCenter, 250, frameWidth, CLIP_HEIGHT) {
            @Override
            public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (mightButton.contains(touchEvent.getX(), touchEvent.getY())) {
                    return false;
                } else {
                    scrollDetector.onTouchEvent(touchEvent);
                    return true;
                }
            }
        };
        frameSprite.attachChild(rewardListTouchArea);
        this.registerTouchArea(rewardListTouchArea);
        rewardListTouchArea.attachChild(mightContainer);
        rewardListTouchArea.attachChild(rankContainer);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private IEntity createRewardGrid(final float x, final float y) {
        final TextureEnum textureEnum = TextureEnum.ARENA_REWARD_GRID;
        final Sprite rewardGrid = createACImageSprite(textureEnum, x, y);
        return rewardGrid;

    }

    private IEntity createMightTouchArea() {
        final IEntity touchArea = new Rectangle(140, 30, 250, 60, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    mightContainer.setVisible(true);
                    rankContainer.setVisible(false);
                    return true;
                }
                return false;

            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    private IEntity createRankTouchArea() {
        final IEntity touchArea = new Rectangle(405, 30, 230, 60, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    mightContainer.setVisible(false);
                    rankContainer.setVisible(true);
                    return true;
                }
                return false;

            }
        };
        touchArea.setAlpha(0);
        return touchArea;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    private void handleScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        final float mightToY = mightContainer.getY() - pDistanceY;
        final float rankToY = rankContainer.getY() - pDistanceY;
        if (mightToY < CONTAINER_INIT_Y) {
            mightContainer.setY(CONTAINER_INIT_Y);
        } else {
            if (mightContainer.isVisible()) {
                if (mightToY > scrollMightBottomY) {
                    mightContainer.setY(scrollMightBottomY);
                } else {
                    mightContainer.setY(mightToY);
                }
            } else {
                if (rankToY > scrollRankBottomY) {
                    mightContainer.setY(scrollRankBottomY);
                } else {
                    mightContainer.setY(rankToY);
                }
            }
        }

        if (rankToY < CONTAINER_INIT_Y) {
            rankContainer.setY(CONTAINER_INIT_Y);
        } else {
            if (rankContainer.isVisible()) {
                if (rankToY > scrollRankBottomY) {
                    rankContainer.setY(scrollRankBottomY);
                } else {
                    rankContainer.setY(rankToY);
                }
            } else {
                if (mightToY > scrollMightBottomY) {
                    rankContainer.setY(scrollMightBottomY);
                } else {
                    rankContainer.setY(mightToY);
                }
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

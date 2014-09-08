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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.util.TextureFactory;

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
    private final float scrollRankBottomY = CLIP_HEIGHT * 0.5f;

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

        final IEntity closeTouchArea = new Rectangle(frameWidth - 75, frameHeight - 75, 150, 150, vbom) {
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

        final ClipEntity rewardListTouchArea = new ClipEntity(frameCenter, 250, frameWidth, CLIP_HEIGHT) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(pSceneTouchEvent);
                return true;
            }
        };
        frameSprite.attachChild(rewardListTouchArea);
        this.registerTouchArea(rewardListTouchArea);

        rewardListTouchArea.attachChild(mightContainer);
        rewardListTouchArea.attachChild(rankContainer);

        final IEntity mightButton = createMightButton();
        mightContainer.attachChild(mightButton);
        this.registerTouchArea(mightButton);
        final TextureEnum mightDescEnum = TextureEnum.ARENA_REWARD_DESC;
        final Sprite mightDesc = createACImageSprite(mightDescEnum, frameCenter, CLIP_HEIGHT - mightButton.getHeight() - mightDescEnum.getHeight() * 0.5f);
        mightContainer.attachChild(mightDesc);

        final TextureEnum gridEnum = TextureEnum.ARENA_REWARD_GRID;
        final float gridHeight = gridEnum.getHeight();
        final float gridInitY = CLIP_HEIGHT - mightButton.getHeight() - mightDescEnum.getHeight() - gridHeight * 0.5f + 2;

        float mightGridBottomY = 0;
        for (int i = 0; i < 3; i++) {
            mightGridBottomY = gridInitY - gridHeight * i;
            final IEntity rewardGrid = createRewardGrid(frameCenter, mightGridBottomY);
            mightContainer.attachChild(rewardGrid);
        }
        scrollMightBottomY = CONTAINER_INIT_Y - mightGridBottomY + gridHeight * 0.5f;

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private IEntity createMightButton() {
        final TextureEnum textureEnum = TextureEnum.ARENA_REWARD_MIGHT_BUTTON;
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(frameWidth * 0.5f, CLIP_HEIGHT - height * 0.5f, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {

                    return true;
                }
                return false;

            }
        };
        return sprite;

    }

    private IEntity createRewardGrid(final float x, final float y) {
        final TextureEnum textureEnum = TextureEnum.ARENA_REWARD_GRID;
        final Sprite rewardGrid = createACImageSprite(textureEnum, x, y);
        return rewardGrid;

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

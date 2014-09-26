package com.fight2.scene;

import java.io.IOException;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.clip.ClipEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.ArenaRanking;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.UserArenaInfo;
import com.fight2.util.ArenaUtils;
import com.fight2.util.ResourceManager;

public class ArenaRankingScene extends BaseScene implements IScrollDetectorListener {
    private final static float CLIP_HEIGHT = 407;
    private final static float CONTAINER_INIT_Y = CLIP_HEIGHT * 0.5f;
    private final static float GRID_HEIGHT = 60;
    private final float frameWidth = TextureEnum.ARENA_RANKING_BG.getWidth();
    private final float frameHeight = TextureEnum.ARENA_RANKING_BG.getHeight();
    private final float frameCenter = frameWidth * 0.5f;
    private final float frameY = simulatedHeight - frameHeight * 0.5f;
    private final SurfaceScrollDetector scrollDetector;
    private final IEntity rankContainer;
    private float scrollRankBottomY = 0;
    private final Font numFont;
    private final UserArenaInfo userArenaInfo;

    public ArenaRankingScene(final GameActivity activity, final UserArenaInfo userArenaInfo) throws IOException {
        super(activity);
        this.userArenaInfo = userArenaInfo;
        this.numFont = ResourceManager.getInstance().getFont(FontEnum.Bold, 26);
        scrollDetector = new SurfaceScrollDetector(this);
        rankContainer = new Rectangle(frameCenter, CONTAINER_INIT_Y, frameWidth, CLIP_HEIGHT, vbom);
        rankContainer.setAlpha(0);
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.5f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite frameSprite = createACImageSprite(TextureEnum.ARENA_RANKING_BG, cameraCenterX, frameY);
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

        final ClipEntity rankListTouchArea = new ClipEntity(frameCenter, 288, frameWidth, CLIP_HEIGHT) {
            @Override
            public boolean onAreaTouched(final TouchEvent touchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(touchEvent);
                return true;
            }
        };
        frameSprite.attachChild(rankListTouchArea);
        this.registerTouchArea(rankListTouchArea);
        rankListTouchArea.attachChild(rankContainer);

        final float initX = 54;
        final float myRankY = 46;
        final Text myNumText = new Text(initX, myRankY, numFont, String.valueOf(userArenaInfo.getRankNumber()), vbom);
        frameSprite.attachChild(myNumText);
        final Text nameText = new Text(100, myRankY, numFont, GameUserSession.getInstance().getName(), vbom);
        this.leftAlignText(nameText, initX + 40);
        frameSprite.attachChild(nameText);
        final IEntity mightIcon = this.createACImageSprite(TextureEnum.COMMON_MIGHT_ICON, initX + 272, myRankY);
        frameSprite.attachChild(mightIcon);
        final Text mightText = new Text(380, myRankY, numFont, String.valueOf(userArenaInfo.getMight()), vbom);
        mightText.setColor(0XFFE5B978);
        this.leftAlignText(mightText, initX + 302);
        frameSprite.attachChild(mightText);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private IEntity createRankGrid(final int index, final float x, final float y) {
        final IEntity rankGrid = new Rectangle(x, y, 554, GRID_HEIGHT, vbom);
        final int color = (index % 2 == 0 ? 0XFF200900 : 0XFF2E1100);
        rankGrid.setColor(color);
        return rankGrid;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        final List<ArenaRanking> rankings = ArenaUtils.getArenaRanking(activity);
        for (int i = 0; i < rankings.size(); i++) {
            final ArenaRanking ranking = rankings.get(i);
            final IEntity rankGrid = createRankGrid(i, frameCenter, CLIP_HEIGHT - 30 - GRID_HEIGHT * i);
            final float numberX = 28;
            if (i == 0) {
                final IEntity numIcon = this.createACImageSprite(TextureEnum.ARENA_RANKING_NUMBER_1, numberX, GRID_HEIGHT * 0.5f + 3);
                rankGrid.attachChild(numIcon);
            } else if (i == 1) {
                final IEntity numIcon = this.createACImageSprite(TextureEnum.ARENA_RANKING_NUMBER_2, numberX, GRID_HEIGHT * 0.5f + 3);
                rankGrid.attachChild(numIcon);
            } else if (i == 2) {
                final IEntity numIcon = this.createACImageSprite(TextureEnum.ARENA_RANKING_NUMBER_3, numberX, GRID_HEIGHT * 0.5f + 3);
                rankGrid.attachChild(numIcon);
            } else {
                final Text numText = new Text(numberX, GRID_HEIGHT * 0.5f, numFont, String.valueOf(ranking.getRankNumber()), vbom);
                rankGrid.attachChild(numText);
            }

            final Text nameText = new Text(75, GRID_HEIGHT * 0.5f, numFont, ranking.getUser().getName(), vbom);
            this.leftAlignText(nameText, 68);
            rankGrid.attachChild(nameText);

            final IEntity mightIcon = this.createACImageSprite(TextureEnum.COMMON_MIGHT_ICON, 300, GRID_HEIGHT * 0.5f);
            rankGrid.attachChild(mightIcon);
            final Text mightText = new Text(335, GRID_HEIGHT * 0.5f, numFont, String.valueOf(ranking.getMight()), vbom);
            mightText.setColor(0XFFE5B978);
            this.leftAlignText(mightText, 330);
            rankGrid.attachChild(mightText);
            rankContainer.attachChild(rankGrid);
        }
        scrollRankBottomY = rankings.size() * GRID_HEIGHT - CONTAINER_INIT_Y;
        if (scrollRankBottomY < CONTAINER_INIT_Y) {
            scrollRankBottomY = CONTAINER_INIT_Y;
        }

    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    private void handleScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        final float rankToY = rankContainer.getY() - pDistanceY;
        if (rankToY < CONTAINER_INIT_Y) {
            rankContainer.setY(CONTAINER_INIT_Y);
        } else {
            if (rankToY > scrollRankBottomY) {
                rankContainer.setY(scrollRankBottomY);
            } else {
                rankContainer.setY(rankToY);
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

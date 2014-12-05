package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.Constants;
import org.andengine.util.adt.color.ColorUtils;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.MusicEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestTile;
import com.fight2.entity.QuestTreasureData;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.F2MusicManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TiledTextureFactory;

public class QuestScene extends BaseScene implements IScrollDetectorListener {
    private final SurfaceScrollDetector mScrollDetector;
    private float minY;
    private float maxY;
    private float minX;
    private float maxX;
    private boolean isScroolling;
    private final TimerHandler timerHandler;
    private TMXTiledMap tmxTiledMap;
    private int direction = -1;
    private QuestGoStatus goStatus;
    private QuestResult questResult;
    private final List<Sprite> treasureSprites = new ArrayList<Sprite>();
    private QuestTreasureData questTreasureData = new QuestTreasureData();
    private static int GID = 0;
    private final float SCALE = 1.5f;
    private final ITiledTextureRegion playerTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.HERO);
    private final float playerHeight = playerTextureRegion.getHeight();

    public QuestScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.getBackground().setColor(ColorUtils.convertABGRPackedIntToColor(0XFF205218));
        init();
        timerHandler = new TimerHandler(10, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                if (ResourceManager.getInstance().getCurrentSceneEnum() == SceneEnum.Quest) {
                    if (goStatus == QuestGoStatus.Stopped) {
                        final QuestTreasureData newTreasureData = QuestUtils.getQuestTreasure(questTreasureData);
                        refreshTreasureSprites(newTreasureData);
                    }
                }
                pTimerHandler.reset();
            }
        });
        activity.getEngine().registerUpdateHandler(timerHandler);
    }

    @Override
    protected void init() throws IOException {
        try {
            final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, vbom);
            this.tmxTiledMap = tmxLoader.loadFromAsset("tmx/fight2.tmx");
            tmxTiledMap.setScale(SCALE);
            final float mapWidth = tmxTiledMap.getWidth() * SCALE;
            final float mapHeight = tmxTiledMap.getHeight() * SCALE;
            minY = this.simulatedHeight - mapHeight * 0.5f;
            maxY = mapHeight * 0.5f;
            minX = this.simulatedWidth - mapWidth * 0.5f;
            maxX = mapWidth * 0.5f;
        } catch (final TMXLoadException e) {
            Debug.e(e);
        }
        // tmxTiledMap.setPosition(this.cameraCenterX - 300, this.cameraCenterY - 250);
        this.attachChild(this.tmxTiledMap);
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        final QuestTreasureData newTreasureData = QuestUtils.getQuestTreasure(questTreasureData);
        refreshTreasureSprites(newTreasureData);

        final float playerX = tmxLayer.getTileX(32) + 0.5f * tmxTiledMap.getTileWidth();
        final float playerY = tmxLayer.getTileY(22) + playerHeight * 0.5f;
        final AnimatedSprite player = new AnimatedSprite(playerX, playerY, playerTextureRegion, vbom);
        player.setCurrentTileIndex(53);
        player.setZIndex(100);

        final float playerSceneX = playerX - tmxTiledMap.getWidth() * 0.5f;
        final float playerSceneY = playerY - tmxTiledMap.getHeight() * 0.5f;
        offsetMap(this.simulatedWidth * 0.5f - playerSceneX * SCALE, playerSceneY * SCALE - this.simulatedHeight * 0.5f);

        tmxTiledMap.attachChild(player);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                mScrollDetector.onTouchEvent(pSceneTouchEvent);
                if (isScroolling) {
                    if (pSceneTouchEvent.isActionUp()) {
                        isScroolling = false;
                        return true;
                    } else {
                        return true;
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {

                    final float sceneX = pSceneTouchEvent.getX();
                    final float sceneY = pSceneTouchEvent.getY();
                    final TMXTile tmxTile = tmxLayer.getTMXTileAt(sceneX, sceneY);
                    if (tmxTile != null && tmxTile.getGlobalTileID() == GID) {
                        final float startX = player.getX();
                        final float startY = player.getY();
                        final float[] playerSceneCordinates = player.getSceneCenterCoordinates();
                        final TMXTile currentTile = tmxLayer.getTMXTileAt(playerSceneCordinates[Constants.VERTEX_INDEX_X],
                                playerSceneCordinates[Constants.VERTEX_INDEX_Y]);
                        // Debug.e("Found currentTile:" + currentTile.getTileColumn() + "," + currentTile.getTileRow());
                        final Path path = findPath(currentTile, tmxTile, tmxLayer);
                        go(tmxTile);
                        final float[] xs = path.getCoordinatesX();
                        final float[] ys = path.getCoordinatesY();
                        player.registerEntityModifier(new PathModifier(path.getSize() * 0.6f, path, null, new IPathModifierListener() {
                            @Override
                            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
//                                F2MusicManager.getInstance().playMusic(MusicEnum.HORSE, true);
                            }

                            @Override
                            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
                                if (pWaypointIndex + 1 < xs.length) {
                                    final float x1 = xs[pWaypointIndex];
                                    final float y1 = ys[pWaypointIndex];
                                    final float x2 = xs[pWaypointIndex + 1];
                                    final float y2 = ys[pWaypointIndex + 1];
                                    if (x1 > x2 && y1 < y2) { // left up
                                        changeDirection(player, 0);
                                    } else if (x1 == x2 && y1 < y2) { // up
                                        changeDirection(player, 1);
                                    } else if (x1 < x2 && y1 < y2) { // right up
                                        changeDirection(player, 2);
                                    } else if (x1 > x2 && y1 == y2) {// left
                                        changeDirection(player, 3);
                                    } else if (x1 < x2 && y1 == y2) {// right
                                        changeDirection(player, 4);
                                    } else if (x1 > x2 && y1 > y2) {// left down
                                        changeDirection(player, 5);
                                    } else if (x1 == x2 && y1 > y2) {// down
                                        changeDirection(player, 6);
                                    } else if (x1 < x2 && y1 > y2) {// down
                                        changeDirection(player, 7);
                                    }
                                }
                            }

                            @Override
                            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {

                            }

                            @Override
                            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {
                                player.stopAnimation();
                                direction = -1;
                                if (goStatus == QuestGoStatus.Failed) {
                                    player.setPosition(startX, startY);
                                    goStatus = QuestGoStatus.Stopped;
                                } else if (goStatus == QuestGoStatus.Arrived) {
                                    if (questResult.isTreasureUpdated()) {
                                        refreshTreasureSprites(questResult.getQuestTreasureData());
                                    }
                                    goStatus = QuestGoStatus.Stopped;
                                } else if (goStatus == QuestGoStatus.Treasure) {
                                    if (questResult.isTreasureUpdated()) {
                                        refreshTreasureSprites(questResult.getQuestTreasureData());
                                    }
                                    try {
                                        final QuestTreasureScene treasureScene = new QuestTreasureScene(questResult, activity);
                                        activity.getEngine().setScene(treasureScene);
                                        treasureScene.updateScene();
                                    } catch (final IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    removeTreasureSprite();
                                    goStatus = QuestGoStatus.Stopped;
                                } else if (goStatus == QuestGoStatus.Enemy) {
                                    if (questResult.isTreasureUpdated()) {
                                        refreshTreasureSprites(questResult.getQuestTreasureData());
                                    }
                                    try {
                                        final PreBattleScene preBattleScene = new PreBattleScene(activity, questResult.getEnemy(), false);
                                        activity.getEngine().setScene(preBattleScene);
                                        preBattleScene.updateScene();
                                    } catch (final IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    goStatus = QuestGoStatus.Stopped;
                                }
                                F2MusicManager.getInstance().stopMusic();
//                                F2MusicManager.getInstance().playMusic(MusicEnum.HORSE8);
                            }
                        }));
                        return true;
                    }
                }

                return false;
            }
        });

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);
    }

    private synchronized void removeTreasureSprite() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                treasureSprites.get(questResult.getTreasureIndex()).detachSelf();
                treasureSprites.remove(questResult.getTreasureIndex());
            }
        });
    }

    private synchronized void refreshTreasureSprites(final QuestTreasureData newTreasureData) {
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        if (newTreasureData.getVersion() > questTreasureData.getVersion()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (final Sprite treasureSprite : treasureSprites) {
                        treasureSprite.detachSelf();
                    }
                    treasureSprites.clear();
                    questTreasureData = newTreasureData;
                    for (final QuestTile treasure : questTreasureData.getQuestTiles()) {
                        final float treasureX = tmxLayer.getTileX(treasure.getCol()) + 0.5f * tmxTiledMap.getTileWidth();
                        final float treasureY = tmxLayer.getTileY(treasure.getRow()) + 0.5f * tmxTiledMap.getTileHeight();
                        final Sprite treasureSprite = createACImageSprite(TextureEnum.QUEST_TREASURE_BOX, treasureX, treasureY);
                        tmxTiledMap.attachChild(treasureSprite);
                        treasureSprites.add(treasureSprite);
                    }
                    tmxTiledMap.sortChildren();

                }

            });

        }

    }

    private void go(final TMXTile destTile) {
        goStatus = QuestGoStatus.Started;
        final IAsyncCallback callback = new IAsyncCallback() {

            @Override
            public void workToDo() {
                questResult = QuestUtils.go(destTile.getTileRow(), destTile.getTileColumn(), questTreasureData);

            }

            @Override
            public void onComplete() {
                if (questResult.getStatus() == 0) {
                    goStatus = QuestGoStatus.Arrived;
                } else if (questResult.getStatus() == 1) {
                    goStatus = QuestGoStatus.Treasure;
                } else if (questResult.getStatus() == 2) {
                    goStatus = QuestGoStatus.Enemy;
                } else {
                    goStatus = QuestGoStatus.Failed;
                }

            }
        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskLoader().execute(callback);
            }
        });
    }

    public enum QuestGoStatus {
        Started,
        Arrived,
        Treasure,
        Enemy,
        Stopped,
        Failed
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(true);
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    private void changeDirection(final AnimatedSprite player, final int newDirection) {
        if (this.direction != newDirection) {
            this.direction = newDirection;
            switch (newDirection) {
                case 0:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 0, 7, true);
                    break;
                case 1:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 9, 16, true);
                    break;
                case 2:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 18, 25, true);
                    break;
                case 3:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 27, 34, true);
                    break;
                case 4:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 36, 43, true);
                    break;
                case 5:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 45, 52, true);
                    break;
                case 6:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 54, 61, true);
                    break;
                case 7:
                    player.animate(new long[] { 100, 100, 100, 100, 100, 100, 100, 100 }, 63, 70, true);
                    break;
            }
        }
    }

    private Path findPath(final TMXTile startTile, final TMXTile desTile, final TMXLayer tmxLayer) {
        TMXTilePoint currentPoint = new TMXTilePoint(startTile, null);
        final Queue<TMXTilePoint> queue = new LinkedList<TMXTilePoint>();
        queue.add(currentPoint);
        final Set<TMXTile> visitedTiles = new HashSet<TMXTile>();
        visitedTiles.add(startTile);
        while (!queue.isEmpty()) {
            currentPoint = queue.poll();
            final TMXTile pointTMXTile = currentPoint.getTmxTile();
            final int row = pointTMXTile.getTileRow();
            final int col = pointTMXTile.getTileColumn();
            if (pointTMXTile == desTile) {
                break;
            }

            final TMXTile upTile = tmxLayer.getTMXTile(col, row - 1);
            if (upTile != null && upTile.getGlobalTileID() == GID && !visitedTiles.contains(upTile)) {/* up */
                visit(upTile, currentPoint, queue);
                visitedTiles.add(upTile);
            }
            final TMXTile leftTile = tmxLayer.getTMXTile(col - 1, row);
            if (leftTile != null && leftTile.getGlobalTileID() == GID && !visitedTiles.contains(leftTile)) {/* left */
                visit(leftTile, currentPoint, queue);
                visitedTiles.add(leftTile);
            }
            final TMXTile rightTile = tmxLayer.getTMXTile(col + 1, row);
            if (rightTile != null && rightTile.getGlobalTileID() == GID && !visitedTiles.contains(rightTile)) { /* right */
                visit(rightTile, currentPoint, queue);
                visitedTiles.add(rightTile);
            }
            final TMXTile downTile = tmxLayer.getTMXTile(col, row + 1);
            if (downTile != null && downTile.getGlobalTileID() == GID && !visitedTiles.contains(downTile)) { /* down */
                visit(downTile, currentPoint, queue);
                visitedTiles.add(downTile);
            }
            final TMXTile leftUpTile = tmxLayer.getTMXTile(col - 1, row - 1);
            if (leftUpTile != null && leftUpTile.getGlobalTileID() == GID && !visitedTiles.contains(leftUpTile)) {/* left up */
                visit(leftUpTile, currentPoint, queue);
                visitedTiles.add(leftUpTile);
            }
            final TMXTile rightUpTile = tmxLayer.getTMXTile(col + 1, row - 1);
            if (rightUpTile != null && rightUpTile.getGlobalTileID() == GID && !visitedTiles.contains(rightUpTile)) {/* right up */
                visit(rightUpTile, currentPoint, queue);
                visitedTiles.add(rightUpTile);
            }

            final TMXTile leftDownTile = tmxLayer.getTMXTile(col - 1, row + 1);
            if (leftDownTile != null && leftDownTile.getGlobalTileID() == GID && !visitedTiles.contains(leftDownTile)) { /* left down */
                visit(leftDownTile, currentPoint, queue);
                visitedTiles.add(leftDownTile);
            }

            final TMXTile rightDownTile = tmxLayer.getTMXTile(col + 1, row + 1);
            if (rightDownTile != null && rightDownTile.getGlobalTileID() == GID && !visitedTiles.contains(rightDownTile)) { /* right down */
                visit(rightDownTile, currentPoint, queue);
                visitedTiles.add(rightDownTile);
            }

        }

        final Stack<TMXTile> stack = new Stack<TMXTile>();
        stack.push(currentPoint.getTmxTile());
        while (currentPoint.getPredecessor() != null) {
            currentPoint = currentPoint.getPredecessor();
            stack.push(currentPoint.getTmxTile());
            if (currentPoint.getTmxTile() == startTile) {
                break;
            }
        }

        final Path path = new Path(stack.size());
        while (!stack.isEmpty()) {
            final TMXTile pathTMXTile = stack.pop();
            path.to(tmxLayer.getTileX(pathTMXTile.getTileColumn()) + 0.5f * tmxTiledMap.getTileWidth(), tmxLayer.getTileY(pathTMXTile.getTileRow())
                    + playerHeight * 0.5f);
        }
        return path;
    }

    private void visit(final TMXTile pointTmxTile, final TMXTilePoint predecessor, final Queue<TMXTilePoint> queue) {
        final TMXTilePoint visitPoint = new TMXTilePoint(pointTmxTile, predecessor);
        queue.add(visitPoint);
    }

    private static class TMXTilePoint {
        private final TMXTile tmxTile;
        private final TMXTilePoint predecessor;

        public TMXTilePoint(final TMXTile tmxTile, final TMXTilePoint predecessor) {
            super();
            this.tmxTile = tmxTile;
            this.predecessor = predecessor;
        }

        public TMXTile getTmxTile() {
            return tmxTile;
        }

        public TMXTilePoint getPredecessor() {
            return predecessor;
        }

    }

    private void offsetMap(final float pDistanceX, final float pDistanceY) {
        float offSetX = pDistanceX;
        final float tiledMapX = tmxTiledMap.getX();
        final float tiledMapY = tmxTiledMap.getY();
        if (offSetX > 0 && tiledMapX + offSetX >= maxX) {
            offSetX = maxX - tiledMapX;
        }
        if (offSetX < 0 && tiledMapX + offSetX <= minX) {
            offSetX = minX - tiledMapX;
        }
        float offSetY = -pDistanceY;
        if (offSetY > 0 && tiledMapY + offSetY >= maxY) {
            offSetY = maxY - tiledMapY;
        }
        if (offSetY < 0 && tiledMapY + offSetY <= minY) {
            offSetY = minY - tiledMapY;
        }
        tmxTiledMap.setPosition(tiledMapX + offSetX, tiledMapY + offSetY);
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        isScroolling = true;
        offsetMap(pDistanceX, pDistanceY);
    }

    @Override
    public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        offsetMap(pDistanceX, pDistanceY);
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        offsetMap(pDistanceX, pDistanceY);
    }

}

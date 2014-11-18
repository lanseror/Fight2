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
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.Constants;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestTile;
import com.fight2.entity.QuestTreasureData;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TiledTextureFactory;

public class QuestScene extends BaseScene {
    private final TimerHandler timerHandler;
    private TMXTiledMap tmxTiledMap;
    private int direction = -1;
    private QuestGoStatus goStatus;
    private QuestResult questResult;
    private final List<Sprite> treasureSprites = new ArrayList<Sprite>();
    private QuestTreasureData questTreasureData = new QuestTreasureData();
    private static int GID = 11;

    public QuestScene(final GameActivity activity) throws IOException {
        super(activity);
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
            this.tmxTiledMap = tmxLoader.loadFromAsset("tmx/my2.tmx");
            this.tmxTiledMap.setOffsetCenter(0, 0);
            tmxTiledMap.setScale(0.3f);
        } catch (final TMXLoadException e) {
            Debug.e(e);
        }
//        tmxTiledMap.setPosition(this.cameraCenterX - 300, this.cameraCenterY - 250);
        this.attachChild(this.tmxTiledMap);
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        final QuestTreasureData newTreasureData = QuestUtils.getQuestTreasure(questTreasureData);
        refreshTreasureSprites(newTreasureData);

        final float playerX = tmxLayer.getTileX(1) + 0.5f * tmxTiledMap.getTileWidth();
        final float playerY = tmxLayer.getTileY(5) + 0.5f * tmxTiledMap.getTileHeight();
        final ITiledTextureRegion playerTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.PLAYER);
        final AnimatedSprite player = new AnimatedSprite(playerX, playerY, playerTextureRegion, vbom);
        player.setCurrentTileIndex(4);
        player.setZIndex(100);

        tmxTiledMap.attachChild(player);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
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
                        player.registerEntityModifier(new PathModifier(path.getSize() * 1f, path, null, new IPathModifierListener() {
                            @Override
                            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {

                            }

                            @Override
                            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int pWaypointIndex) {
                                if (pWaypointIndex + 1 < xs.length) {
                                    final float x1 = xs[pWaypointIndex];
                                    final float y1 = ys[pWaypointIndex];
                                    final float x2 = xs[pWaypointIndex + 1];
                                    final float y2 = ys[pWaypointIndex + 1];

                                    if (x1 == x2 && y1 < y2) { // up
                                        changeDirection(player, 0);
                                    } else if (x1 < x2 && y1 == y2) {// right
                                        changeDirection(player, 1);
                                    } else if (x1 == x2 && y1 > y2) {// down
                                        changeDirection(player, 2);
                                    } else if (x1 > x2 && y1 == y2) {// left
                                        changeDirection(player, 3);
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
                    player.animate(new long[] { 200, 200, 200 }, 0, 2, true);
                    break;
                case 1:
                    player.animate(new long[] { 200, 200, 200 }, 3, 5, true);
                    break;
                case 2:
                    player.animate(new long[] { 200, 200, 200 }, 6, 8, true);
                    break;
                case 3:
                    player.animate(new long[] { 200, 200, 200 }, 9, 11, true);
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
            final TMXTile leftTile = tmxLayer.getTMXTile(col - 1, row);
            if (leftTile != null && leftTile.getGlobalTileID() == GID && !visitedTiles.contains(leftTile)) {/* left */
                visit(leftTile, currentPoint, queue);
                visitedTiles.add(leftTile);
            }
            final TMXTile upTile = tmxLayer.getTMXTile(col, row - 1);
            if (upTile != null && upTile.getGlobalTileID() == GID && !visitedTiles.contains(upTile)) {/* up */
                visit(upTile, currentPoint, queue);
                visitedTiles.add(upTile);
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
            path.to(tmxLayer.getTileX(pathTMXTile.getTileColumn()) + 0.5f * tmxTiledMap.getTileWidth(), tmxLayer.getTileY(pathTMXTile.getTileRow()) + 0.5f
                    * tmxTiledMap.getTileHeight());
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

}

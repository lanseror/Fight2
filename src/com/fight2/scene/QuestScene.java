package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
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

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.Hero;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.QuestTile;
import com.fight2.entity.QuestTreasureData;
import com.fight2.entity.User;
import com.fight2.entity.battle.BattleType;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.CardUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ICallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TaskUtils;
import com.fight2.util.TiledTextureFactory;
import com.fight2.util.TmxUtils;

public class QuestScene extends BaseScene implements IScrollDetectorListener {
    private final static String[] D = { "2", "UP", "RIGHT", "DOWN", "LEFT" };
    private final SurfaceScrollDetector mScrollDetector;
    private float minY;
    private float maxY;
    private float minX;
    private float maxX;
    private boolean isScroolling;
    private final TimerHandler timerHandler;
    private TMXTiledMap tmxTiledMap;
    private QuestGoStatus goStatus = QuestGoStatus.Stopped;
    private QuestResult questResult;
    private final List<Sprite> treasureSprites = new ArrayList<Sprite>();
    private QuestTreasureData questTreasureData = new QuestTreasureData();
    private static int GID = 0;
    private final float SCALE = 1.5f;
    private final Queue<Sprite> pathTags = new LinkedList<Sprite>();
    private final Hero hero = new Hero(0, 0, vbom);
    private TMXTile destTile;
    private Path path;
    private final IEntity destTouchArea = new Rectangle(0, 0, 60, 60, vbom);
    private final F2ButtonSprite cancelButton = createCancelButton();
    private AnimatedSprite flagSprite;

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
            throw new RuntimeException(e);
        }
        // tmxTiledMap.setPosition(this.cameraCenterX - 300, this.cameraCenterY - 250);
        this.attachChild(this.tmxTiledMap);
        tmxTiledMap.attachChild(destTouchArea);
        destTouchArea.setAlpha(0);
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        final TmxUtils tmxUtils = new TmxUtils(tmxTiledMap);
        final QuestTreasureData newTreasureData = QuestUtils.getQuestTreasure(questTreasureData);
        refreshTreasureSprites(newTreasureData);

        final float playerX = tmxLayer.getTileX(32) + 0.5f * tmxTiledMap.getTileWidth();
        final float playerY = tmxLayer.getTileY(22) + TmxUtils.HERO_OFFSET_Y;
        hero.setPosition(playerX, playerY);
        hero.setCurrentTileIndex(53);
        hero.setZIndex(100);
        hero.setScale(0.6f);

        final float playerSceneX = playerX - tmxTiledMap.getWidth() * 0.5f;
        final float playerSceneY = playerY - tmxTiledMap.getHeight() * 0.5f;
        offsetMap(this.simulatedWidth * 0.5f - playerSceneX * SCALE, playerSceneY * SCALE - this.simulatedHeight * 0.5f);

        showTaskFlag();
        tmxTiledMap.attachChild(hero);
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
                    if (goStatus == QuestGoStatus.Stopped) {
                        destTile = tmxLayer.getTMXTileAt(sceneX, sceneY);
                        if (destTile != null && destTile.getGlobalTileID() == GID) {
                            final float[] playerSceneCordinates = hero.getSceneCenterCoordinates();
                            final TMXTile currentTile = tmxLayer.getTMXTileAt(playerSceneCordinates[Constants.VERTEX_INDEX_X],
                                    playerSceneCordinates[Constants.VERTEX_INDEX_Y]);
                            // Debug.e("Found currentTile:" + currentTile.getTileColumn() + "," + currentTile.getTileRow());
                            path = tmxUtils.findPath(currentTile, destTile, tmxLayer);
                            showPathTags(path);
                            goStatus = QuestGoStatus.Ready;
                            return true;
                        }
                    } else if (goStatus == QuestGoStatus.Ready && destTouchArea.contains(sceneX, sceneY)) {
                        go(tmxUtils.getPathTiles(), path);
                        return true;
                    }
                }

                return false;
            }
        });

        final F2ButtonSprite townButton = createALBF2ButtonSprite(TextureEnum.QUEST_TOWN, TextureEnum.QUEST_TOWN, this.simulatedLeftX + 2, this.cameraHeight
                - TextureEnum.QUEST_TOWN.getHeight());
        townButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(townButton);
        this.registerTouchArea(townButton);

        final Sprite experienceBox = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX, this.simulatedLeftX + 100, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight() + 7);
        this.attachChild(experienceBox);
        experienceBox.setScale(0.8f);
        final Sprite experienceStick = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_STICK, 52, 0);
        experienceBox.attachChild(experienceStick);
        final Sprite experienceBoxStar = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX_STAR, this.simulatedLeftX + 100, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight() + 7);
        experienceBoxStar.setScale(0.8f);
        this.attachChild(experienceBoxStar);

        final Sprite staminaBox = createALBImageSprite(TextureEnum.COMMON_STAMINA_BOX, this.simulatedLeftX + 400, this.simulatedHeight
                - TextureEnum.COMMON_STAMINA_BOX.getHeight() + 8);
        staminaBox.setScale(0.8f);
        this.attachChild(staminaBox);
        final Sprite staminaStick = createALBImageSprite(TextureEnum.COMMON_STAMINA_STICK, 56, 11);
        staminaBox.attachChild(staminaStick);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        cancelButton.setVisible(false);
        this.attachChild(cancelButton);
        this.registerTouchArea(cancelButton);
    }

    private void showTaskFlag() {
        final ITiledTextureRegion flagTiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.QUEST_FLAG);
        flagSprite = new AnimatedSprite(0, 0, flagTiledTextureRegion, vbom);
        flagSprite.setVisible(false);
        tmxTiledMap.attachChild(flagSprite);
    }

    private F2ButtonSprite createCancelButton() {
        final F2ButtonSprite cancelButton = createALBF2ButtonSprite(TextureEnum.QUEST_CANCEL_BUTTON, TextureEnum.QUEST_CANCEL_BUTTON_FCS,
                this.simulatedRightX - 135, 220);
        cancelButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (goStatus == QuestGoStatus.Ready) {
                    clearPathTages();
                    goStatus = QuestGoStatus.Stopped;
                    cancelButton.setVisible(false);
                }
            }
        });
        return cancelButton;
    }

    private void showPathTags(final Path path) {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                final int pathSize = path.getSize();
                for (int i = 1; i < path.getSize(); i++) {
                    Sprite tag = null;
                    if (i == pathSize - 1) {
                        tag = createPathEndTag(path);
                    } else {
                        tag = createPathTage(i, path);
                    }
                    tmxTiledMap.attachChild(tag);
                    pathTags.add(tag);
                }
            }
        });
    }

    private Sprite createPathTage(final int index, final Path path) {
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        final float x1 = xs[index - 1];
        final float y1 = ys[index - 1];
        final float x2 = xs[index];
        final float y2 = ys[index];
        final float x3 = xs[index + 1];
        final float y3 = ys[index + 1];
        final StringBuilder sb = new StringBuilder(5);
        String previousDirection = "test";
        if (x1 > x2 && y1 < y2) { // left up
            previousDirection = "41";
        } else if (x1 == x2 && y1 < y2) { // up
            previousDirection = "1";
        } else if (x1 < x2 && y1 < y2) { // right up
            previousDirection = "21";
        } else if (x1 > x2 && y1 == y2) {// left
            previousDirection = "4";
        } else if (x1 < x2 && y1 == y2) {// right
            previousDirection = "2";
        } else if (x1 > x2 && y1 > y2) {// left down
            previousDirection = "43";
        } else if (x1 == x2 && y1 > y2) {// down
            previousDirection = "3";
        } else if (x1 < x2 && y1 > y2) {// right down
            previousDirection = "23";
        }
        String nextDirection = "test";
        if (x2 > x3 && y2 < y3) { // left up
            nextDirection = "41";
        } else if (x2 == x3 && y2 < y3) { // up
            nextDirection = "1";
        } else if (x2 < x3 && y2 < y3) { // right up
            nextDirection = "21";
        } else if (x2 > x3 && y2 == y3) {// left
            nextDirection = "4";
        } else if (x2 < x3 && y2 == y3) {// right
            nextDirection = "2";
        } else if (x2 > x3 && y2 > y3) {// left down
            nextDirection = "43";
        } else if (x2 == x3 && y2 > y3) {// down
            nextDirection = "3";
        } else if (x2 < x3 && y2 > y3) {// right down
            nextDirection = "23";
        }

        if (!nextDirection.equals(previousDirection)) {
            if (previousDirection.length() == 2 && nextDirection.length() == 2) {
                String testDirection = null;
                for (int i = 0; i < previousDirection.length(); i++) {
                    testDirection = previousDirection.substring(i, i + 1);
                    if (nextDirection.indexOf(testDirection) >= 0) {
                        previousDirection = testDirection;
                        break;
                    }
                }
                if (previousDirection.length() == 2) {
                    throw new RuntimeException("Found unexpected direction:" + previousDirection + "to" + nextDirection);
                }
            }
            sb.append(previousDirection);
            sb.append(0);
        }
        sb.append(nextDirection);
        final StringBuilder directionSb = new StringBuilder(25);
        directionSb.append("PATH_");
        for (int i = 0; i < sb.length(); i++) {
            final String directionIndexString = sb.substring(i, i + 1);
            final int directionIndex = Integer.parseInt(directionIndexString);
            directionSb.append(D[directionIndex]);
        }

        final TextureEnum pathTextureEnum = TextureEnum.valueOf(directionSb.toString());
        final Sprite tag = createACImageSprite(pathTextureEnum, xs[index], ys[index] - 10);
        return tag;
    }

    private void clearPathTages() {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                final Iterator<Sprite> it = pathTags.iterator();
                while (it.hasNext()) {
                    it.next().detachSelf();
                }
                pathTags.clear();
            }
        });
    }

    private Sprite createPathEndTag(final Path path) {
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        final int endIndex = path.getSize() - 1;
        final float x1 = xs[endIndex - 1];
        final float y1 = ys[endIndex - 1];
        final float x2 = xs[endIndex];
        final float y2 = ys[endIndex];
        TextureEnum textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        if (x1 > x2 && y1 < y2) { // left up
            textureEnum = TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 == x2 && y1 < y2) { // up
            textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 < x2 && y1 < y2) { // right up
            textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 > x2 && y1 == y2) {// left
            textureEnum = TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 < x2 && y1 == y2) {// right
            textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 > x2 && y1 > y2) {// left down
            textureEnum = TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 == x2 && y1 > y2) {// down
            textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 < x2 && y1 > y2) {// right down
            textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        }
        final Sprite tag = createACImageSprite(textureEnum, x2, y2 - 5);
        destTouchArea.setPosition(tag);
        cancelButton.setVisible(true);
        return tag;

    }

    private synchronized void removeTreasureSprite() {
        activity.runOnUpdateThread(new Runnable() {
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
            activity.runOnUpdateThread(new Runnable() {
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
                        final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.TREASURE_BOX);
                        final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                        treasureSprite.animate(500, true);
                        tmxTiledMap.attachChild(treasureSprite);
                        treasureSprites.add(treasureSprite);
                    }
                    tmxTiledMap.sortChildren();

                }

            });

        }

    }

    private void go(final List<TMXTile> pathTiles, final Path path) {
        goStatus = QuestGoStatus.Started;
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        hero.registerEntityModifier(new PathModifier(path.getSize() * 0.55f, path, null, new IPathModifierListener() {
            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
                // F2SoundManager.getInstance().play(SoundEnum.HORSE, true);
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int waypointIndex) {
                if (waypointIndex % 5 == 0) {
                    final int targetTileIndex = waypointIndex + 5 >= pathTiles.size() ? pathTiles.size() - 1 : waypointIndex + 5;
                    final int endTargetFlag = (targetTileIndex == pathTiles.size() - 1) ? 0 : 1;
                    sendToServer(pathTiles.get(targetTileIndex), endTargetFlag);// server need to validate if target tile count >5 to avoid hack;
                }

                if (waypointIndex + 1 < path.getSize()) {
                    hero.onGoing(path, waypointIndex);
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            pathTags.poll().detachSelf();
                        }
                    });
                }
            }

            @Override
            public void onPathWaypointFinished(final PathModifier pPathModifier, final IEntity entity, final int waypointIndex) {
                if ((waypointIndex + 1) % 5 == 0 || waypointIndex == path.getSize() - 2) {
                    // F2SoundManager.getInstance().stop();
                    if (goStatus == QuestGoStatus.Failed) {
                        final int backStep = waypointIndex > 4 ? waypointIndex - 4 : 0;
                        goStatus = QuestGoStatus.Stopped;
                        cancelButton.setVisible(false);
                        activity.runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                entity.clearEntityModifiers();
                                hero.setPosition(xs[backStep], ys[backStep]);
                            }

                        });
                    } else if (goStatus == QuestGoStatus.Arrived) {
                        if (questResult.isTreasureUpdated()) {
                            refreshTreasureSprites(questResult.getQuestTreasureData());
                        }
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
                    } else if (goStatus == QuestGoStatus.Enemy) {
                        if (questResult.isTreasureUpdated()) {
                            refreshTreasureSprites(questResult.getQuestTreasureData());
                        }
                        try {
                            final PreBattleScene preBattleScene = new PreBattleScene(activity, questResult.getEnemy(), BattleType.Quest);
                            activity.getEngine().setScene(preBattleScene);
                            preBattleScene.updateScene();
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (goStatus == QuestGoStatus.Task) {
                        if (questResult.isTreasureUpdated()) {
                            refreshTreasureSprites(questResult.getQuestTreasureData());
                        }
                        final QuestTask task = TaskUtils.getTask();
                        final User boss = questResult.getEnemy();
                        final PartyInfo bossPartyInfo = CardUtils.getPartyByUserId(activity, boss.getId());
                        final Card bossLeader = bossPartyInfo.getParties()[0].getCards()[0];
                        final DialogFrame dialogFrame = new HeroDialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity, bossLeader, boss.getName(), task
                                .getBossDialog());
                        dialogFrame.bind(QuestScene.this, new ICallback() {
                            @Override
                            public void onCallback() {
                                try {
                                    dialogFrame.unbind(QuestScene.this);
                                    final PreBattleScene preBattleScene = new PreBattleScene(activity, boss, BattleType.Task);
                                    preBattleScene.updateScene();
                                    setChildScene(preBattleScene, false, false, true);
                                } catch (final IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });

                    }

                }

                if (waypointIndex == path.getSize() - 2) {
                    goStatus = QuestGoStatus.Stopped;
                    cancelButton.setVisible(false);
                    hero.stopAnimation();
                    F2SoundManager.getInstance().play(SoundEnum.HORSE8);
                }
            }

            @Override
            public void onPathFinished(final PathModifier pPathModifier, final IEntity pEntity) {

            }
        }));

    }

    private void sendToServer(final TMXTile targetTile, final int endTargetFlag) {
        final IAsyncCallback callback = new IAsyncCallback() {

            @Override
            public void workToDo() {
                questResult = QuestUtils.go(targetTile.getTileRow(), targetTile.getTileColumn(), questTreasureData, endTargetFlag);

            }

            @Override
            public void onComplete() {
                if (questResult.getStatus() == 0) {
                    goStatus = QuestGoStatus.Arrived;
                } else if (questResult.getStatus() == 1) {
                    goStatus = QuestGoStatus.Treasure;
                } else if (questResult.getStatus() == 2) {
                    goStatus = QuestGoStatus.Enemy;
                } else if (questResult.getStatus() == 3) {
                    goStatus = QuestGoStatus.Task;
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
        Ready,
        Started,
        Arrived,
        Treasure,
        Enemy,
        Task,
        Stopped,
        Failed
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(true);
        final QuestTask task = TaskUtils.getTask();
        if (task.getStatus() == UserTaskStatus.Started) {
            final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
            final float flagX = tmxLayer.getTileX(task.getX()) + 0.5f * tmxTiledMap.getTileWidth();
            final float flagY = tmxLayer.getTileY(task.getY()) + 0.5f * tmxTiledMap.getTileHeight();
            final float flagHeight = flagSprite.getHeight();
            flagSprite.setPosition(flagX + 5, flagY + flagHeight * 0.5f);
            flagSprite.animate(500, true);
            flagSprite.setVisible(true);
        } else {
            flagSprite.stopAnimation();
            flagSprite.setVisible(false);
        }
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

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

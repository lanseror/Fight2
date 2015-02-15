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
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.Constants;
import org.andengine.util.adt.color.ColorUtils;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Hero;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.QuestResult;
import com.fight2.entity.QuestResult.TileItem;
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.QuestTile;
import com.fight2.entity.QuestTreasureData;
import com.fight2.entity.User;
import com.fight2.entity.UserProperties;
import com.fight2.entity.UserStoreroom;
import com.fight2.entity.battle.BattleType;
import com.fight2.entity.engine.CommonStick;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.CardUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IParamCallback;
import com.fight2.util.IRCallback;
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
    private CommonStick staminaStick;
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
    private final Text cointText;
    private final Text diamonText;
    private final Text guildContribText;
    private boolean handlingFailure;

    public QuestScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.getBackground().setColor(ColorUtils.convertABGRPackedIntToColor(0XFF205218));
        cointText = new Text(123, 24, font, "", 8, vbom);
        diamonText = new Text(123, 24, font, "", 8, vbom);
        guildContribText = new Text(123, 24, font, "", 8, vbom);
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
                            if (destTile != currentTile) {
                                path = tmxUtils.findPath(currentTile, destTile, tmxLayer);
                                showPathTags(path);
                                goStatus = QuestGoStatus.Ready;
                                return true;
                            }
                        }
                    } else if (goStatus == QuestGoStatus.Ready && destTouchArea.contains(sceneX, sceneY)) {
                        if (staminaEnough(path)) {
                            go(tmxUtils.getPathTiles(), path);
                        } else {
                            if (GameUserSession.getInstance().getStoreroom().getStamina() > 0) {
                                try {
                                    activity.getGameHub().setSmallChatRoomEnabled(false);
                                    final BaseScene useStaminaScene = new UseStaminaScene(activity, new IParamCallback() {
                                        @Override
                                        public void onCallback(final Object param) {
                                            final Boolean isOk = (Boolean) param;
                                            if (isOk) {
                                                staminaStick.setValue(UserProperties.MAX_STAMINA);
                                                go(tmxUtils.getPathTiles(), path);
                                            }
                                            activity.getGameHub().setSmallChatRoomEnabled(true);
                                        }
                                    });
                                    setChildScene(useStaminaScene, false, false, true);
                                } catch (final IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }
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
                ResourceManager.getInstance().sceneBack();
            }
        });
        this.attachChild(townButton);
        this.registerTouchArea(townButton);

        final Sprite guildContribBox = createALBImageSprite(TextureEnum.COMMON_GUILD_CONTRIB_BAR, this.simulatedLeftX + 150, this.simulatedHeight
                - TextureEnum.COMMON_GUILD_CONTRIB_BAR.getHeight());
        this.attachChild(guildContribBox);
        guildContribBox.attachChild(guildContribText);

        final Sprite staminaBox = createALBImageSprite(TextureEnum.COMMON_STAMINA_BOX, this.simulatedLeftX + 425, this.simulatedHeight
                - TextureEnum.COMMON_STAMINA_BOX.getHeight());
        this.attachChild(staminaBox);
        staminaStick = new CommonStick(127, 24, TextureEnum.COMMON_STAMINA_STICK, TextureEnum.COMMON_STAMINA_STICK_RIGHT, activity, 100);
        // final Sprite staminaStick = createALBImageSprite(TextureEnum.COMMON_STAMINA_STICK, 56, 11);
        staminaBox.attachChild(staminaStick);

        final Sprite coinBox = createALBImageSprite(TextureEnum.COMMON_COIN_BOX, this.simulatedLeftX + 700,
                this.simulatedHeight - TextureEnum.COMMON_COIN_BOX.getHeight());
        this.attachChild(coinBox);
        coinBox.attachChild(cointText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);
        rechargeSprite.attachChild(diamonText);

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

    private boolean staminaEnough(final Path path) {
        final int availStamina = GameUserSession.getInstance().getUserProps().getStamina();
        return path.getSize() - 1 <= availStamina;
    }

    private void go(final List<TMXTile> pathTiles, final Path path) {
        handlingFailure = false;
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        hero.registerEntityModifier(new PathModifier(path.getSize() * 0.55f, path, null, new IPathModifierListener() {
            @Override
            public void onPathStarted(final PathModifier pPathModifier, final IEntity pEntity) {
                // F2SoundManager.getInstance().play(SoundEnum.HORSE, true);
            }

            @Override
            public void onPathWaypointStarted(final PathModifier pPathModifier, final IEntity pEntity, final int waypointIndex) {
                if (waypointIndex % 5 == 0 && !handlingFailure) {
                    goStatus = QuestGoStatus.Started;
                    final int startTileIndex = waypointIndex;
                    final int targetTileIndex = waypointIndex + 5 >= pathTiles.size() ? pathTiles.size() - 1 : waypointIndex + 5;
                    final int endTargetFlag = (targetTileIndex == pathTiles.size() - 1) ? 0 : 1;
                    sendToServer(pathTiles.get(startTileIndex), pathTiles.get(targetTileIndex), endTargetFlag);// server need to validate if target tile count
                                                                                                               // >5 to avoid hack;
                }

                if (waypointIndex + 1 < path.getSize() && !handlingFailure) {
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

                    if (goStatus == QuestGoStatus.Started) {
                        handleWait();
                    } else {
                        final int backStep = waypointIndex > 4 ? waypointIndex - 4 : 0;
                        handleResult(xs[backStep], ys[backStep], questResult);
                    }

                }

                if (waypointIndex == path.getSize() - 2 && goStatus != QuestGoStatus.Waitting) {
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

    private void handleWait() {
        goStatus = QuestGoStatus.Waitting;
        cancelButton.setVisible(false);
        try {
            final LoadingScene loadingScene = new LoadingScene(activity);
            this.setChildScene(loadingScene, false, false, true);
            activity.getGameHub().setSmallChatRoomEnabled(false);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                hero.clearEntityModifiers();
            }

        });
    }

    private void handleCatchUp() {
        cancelButton.setVisible(false);
        hero.stopAnimation();
        F2SoundManager.getInstance().play(SoundEnum.HORSE8);
        this.getChildScene().back();
        activity.getGameHub().setSmallChatRoomEnabled(true);
    }

    private void sendToServer(final TMXTile startTile, final TMXTile targetTile, final int endTargetFlag) {
        final IAsyncCallback callback = new IAsyncCallback() {

            @Override
            public void workToDo() {
                questResult = QuestUtils.go(targetTile.getTileRow(), targetTile.getTileColumn(), questTreasureData, endTargetFlag);

            }

            @Override
            public void onComplete() {
                if (goStatus == QuestGoStatus.Started) {
                    changeQuestStatus(questResult);
                } else if (goStatus == QuestGoStatus.Waitting) {
                    handleCatchUp();
                    changeQuestStatus(questResult);
                    final TMXLayer tmxLayer = tmxTiledMap.getTMXLayers().get(0);
                    final float playerBackX = tmxLayer.getTileX(startTile.getTileColumn()) + 0.5f * tmxTiledMap.getTileWidth();
                    final float playerBackY = tmxLayer.getTileY(startTile.getTileRow()) + TmxUtils.HERO_OFFSET_Y;
                    Debug.e("Waitting!");
                    handleResult(playerBackX, playerBackY, questResult);
                    goStatus = QuestGoStatus.Stopped;
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

    private void changeQuestStatus(final QuestResult questResult) {
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

    private void handleResult(final float playerBackX, final float playerBackY, final QuestResult questResult) {
        staminaStick.setValue(questResult.getStamina());
        GameUserSession.getInstance().getUserProps().setStamina(questResult.getStamina());
        if (goStatus == QuestGoStatus.Failed) {
            goStatus = QuestGoStatus.Stopped;
            cancelButton.setVisible(false);
            handlingFailure = true;
            hero.stopAnimation();
            clearPathTages();
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    hero.clearEntityModifiers();
                    hero.setPosition(playerBackX, playerBackY);
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
            receiveQuestTreasure(questResult);
            updateQuestPropsBar();
            ResourceManager.getInstance().setCurrentScene(SceneEnum.QuestTreasure, new IRCallback<BaseScene>() {

                @Override
                public BaseScene onCallback() {
                    try {
                        return new QuestTreasureScene(questResult, activity);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
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
            final DialogFrame dialogFrame = new HeroDialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity, bossLeader, boss.getName(),
                    task.getBossDialog());
            dialogFrame.bind(QuestScene.this, new IParamCallback() {
                @Override
                public void onCallback(final Object param) {
                    dialogFrame.unbind(QuestScene.this);
                    ResourceManager.getInstance().setChildScene(QuestScene.this, new IRCallback<BaseScene>() {
                        @Override
                        public BaseScene onCallback() {
                            try {
                                return new PreBattleScene(activity, boss, BattleType.Task);
                            } catch (final IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            });

        }
    }

    public enum QuestGoStatus {
        Ready,
        Started,
        Waitting,
        Timeout,
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
        final UserProperties userProps = QuestUtils.getUserProperties(activity);
        GameUserSession.getInstance().setUserProps(userProps);
        staminaStick.setValue(userProps.getStamina(), true);
        cointText.setText(String.valueOf(userProps.getCoin()));
        guildContribText.setText(String.valueOf(userProps.getGuildContrib()));
        diamonText.setText(String.valueOf(userProps.getDiamon()));
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

    private void updateQuestPropsBar() {
        final UserProperties userProps = QuestUtils.getUserProperties(activity);
        cointText.setText(String.valueOf(userProps.getCoin()));
        guildContribText.setText(String.valueOf(userProps.getGuildContrib()));
        diamonText.setText(String.valueOf(userProps.getDiamon()));
    }

    private void receiveQuestTreasure(final QuestResult questResult) {
        final TileItem tileItem = questResult.getItem();
        final UserStoreroom storeroom = GameUserSession.getInstance().getStoreroom();
        final UserProperties userProps = GameUserSession.getInstance().getUserProps();

        if (tileItem == TileItem.Card) {
            // Noting to do.
        } else if (tileItem == TileItem.Stamina) {
            storeroom.setStamina(storeroom.getStamina() + 1);
        } else if (tileItem == TileItem.Ticket) {
            storeroom.setTicket(storeroom.getTicket() + 1);
        } else if (tileItem == TileItem.CoinBag) {
            userProps.setCoin(userProps.getCoin() + 500);
        } else if (tileItem == TileItem.SummonCharm) {
            userProps.setSummonCharm(userProps.getSummonCharm() + 50);
        } else if (tileItem == TileItem.Diamon) {
            userProps.setDiamon(userProps.getDiamon() + 1);
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

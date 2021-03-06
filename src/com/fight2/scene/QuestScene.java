package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.CostConstants;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameMine;
import com.fight2.entity.GameMine.MineType;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Hero;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.User;
import com.fight2.entity.UserProperties;
import com.fight2.entity.UserStoreroom;
import com.fight2.entity.battle.BattleType;
import com.fight2.entity.engine.CommonStick;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.entity.quest.QuestGoStatus;
import com.fight2.entity.quest.QuestHeroStatus;
import com.fight2.entity.quest.QuestResult;
import com.fight2.entity.quest.QuestTask;
import com.fight2.entity.quest.QuestTask.UserTaskStatus;
import com.fight2.entity.quest.QuestTile;
import com.fight2.entity.quest.QuestTile.TileItem;
import com.fight2.entity.quest.QuestTreasureData;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.CardUtils;
import com.fight2.util.ChatUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IParamCallback;
import com.fight2.util.IRCallback;
import com.fight2.util.MineUtils;
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
    private QuestHeroStatus heroStatus = QuestHeroStatus.Stopped;
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
    private final Map<Sprite, GameMine> spriteMineMap = new HashMap<Sprite, GameMine>();
    private final SparseArray<GameMine> minesMap = new SparseArray<GameMine>();
    private final SparseArray<Text> mineMarkMap = new SparseArray<Text>();

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
            public void onTimePassed(final TimerHandler timerHandler) {
                if (ResourceManager.getInstance().getCurrentSceneEnum() == SceneEnum.Quest) {
                    if (heroStatus == QuestHeroStatus.Stopped) {
                        final IAsyncCallback callback = new IAsyncCallback() {

                            @Override
                            public void workToDo() {
                                final QuestTreasureData newTreasureData = QuestUtils.getQuestTreasure(questTreasureData);
                                refreshTreasureSprites(newTreasureData);
                                refreshMineStatus();
                            }

                            @Override
                            public void onComplete() {
                            }

                        };
                        QuestScene.this.exeAsyncTask(callback);
                    }
                }
                timerHandler.reset();
            }
        });
        activity.getEngine().registerUpdateHandler(timerHandler);
    }

    private void createMine() {
        final List<GameMine> mines = MineUtils.list();
        for (final GameMine mine : mines) {
            final User owner = mine.getOwner();
            final MineType type = mine.getType();
            final Sprite mark = createALBImageSprite(TextureEnum.MAIN_TIPS, 0, 0);
            final Font markFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 20);
            final Text markText = new Text(mark.getWidth() * 0.5f, mark.getHeight() * 0.5f, markFont, owner.getName(), 15, vbom);
            markText.setColor(0XFFDFDCD7);
            mark.attachChild(markText);
            mineMarkMap.put(mine.getId(), markText);
            minesMap.put(mine.getId(), mine);
            mark.setScale(0.6f);
            mark.setAlpha(0.7f);

            if (type == MineType.Crystal) {
                final ITiledTextureRegion crystalMineTiledTexture = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.MINE_CRYSTAL);
                final AnimatedSprite mineSprite = new AnimatedSprite(0, 0, crystalMineTiledTexture, vbom);
                mineSprite.animate(500, true);
                setMapElementPosition(mineSprite, mine.getCol(), mine.getRow());
                mark.setPosition(mineSprite.getWidth() * 0.5f, mineSprite.getHeight() + 5);
                mineSprite.attachChild(mark);
                tmxTiledMap.attachChild(mineSprite);
                spriteMineMap.put(mineSprite, mine);
            } else if (type == MineType.Diamon) {
                final ITiledTextureRegion diamonMineTiledTexture = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.MINE_DIAMON);
                final AnimatedSprite mineSprite = new AnimatedSprite(0, 0, diamonMineTiledTexture, vbom);
                mineSprite.animate(500, true);
                setMapElementPosition(mineSprite, mine.getCol(), mine.getRow());
                mark.setPosition(mineSprite.getWidth() * 0.5f, mineSprite.getHeight() + 5);
                mineSprite.attachChild(mark);
                tmxTiledMap.attachChild(mineSprite);
                spriteMineMap.put(mineSprite, mine);
            } else {
                final TextureEnum textureEnum = (type == MineType.Wood ? TextureEnum.QUEST_MINE_WOOD : TextureEnum.QUEST_MINE_MINERAL);
                final Sprite mineSprite = this.createACImageSprite(textureEnum, 0, 0);
                setMapElementPosition(mineSprite, mine.getCol(), mine.getRow());
                mark.setPosition(mineSprite.getWidth() * 0.5f, mineSprite.getHeight() + 5);
                mineSprite.attachChild(mark);
                tmxTiledMap.attachChild(mineSprite);
                spriteMineMap.put(mineSprite, mine);
            }
        }
    }

    private void createTown() {
        final Sprite townSprite = this.createACImageSprite(TextureEnum.QUEST_TOWN_BIG, 0, 0);
        setMapElementPosition(townSprite, 30, 20);
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(4);
        tmxLayer.attachChild(townSprite);
    }

    private void setMapElementPosition(final IEntity entity, final int tileX, final int tileY) {
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        final float x = tmxLayer.getTileX(tileX) + 0.5f * entity.getWidth();
        final float y = tmxLayer.getTileY(tileY) + 0.5f * entity.getHeight();
        entity.setPosition(x, y);
    }

    private TMXTile getTileByMineSprite(final Sprite mineSprite) {
        if (mineSprite == null) {
            return null;
        }
        final GameMine gameMine = spriteMineMap.get(mineSprite);
        final MineType mineType = gameMine.getType();
        final int standTileCol = gameMine.getCol() + mineType.getxOffset();
        final int standTileRow = gameMine.getRow() + mineType.getyOffset();
        final TMXLayer tmxLayer = this.tmxTiledMap.getTMXLayers().get(0);
        return tmxLayer.getTMXTile(standTileCol, standTileRow);
    }

    private Sprite getMineSpriteIfTouch(final float x, final float y) {
        for (final Entry<Sprite, GameMine> mineEntry : spriteMineMap.entrySet()) {
            final Sprite mineSprite = mineEntry.getKey();
            if (mineSprite.contains(x, y)) {
                return mineSprite;
            }
        }
        return null;
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
        createTown();
        createMine();

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
                        setScroolling(false);
                        return true;
                    } else {
                        return true;
                    }
                }
                if (pSceneTouchEvent.isActionUp()) {
                    final float sceneX = pSceneTouchEvent.getX();
                    final float sceneY = pSceneTouchEvent.getY();
                    if (heroStatus == QuestHeroStatus.Stopped) {
                        final Sprite mineSprite = getMineSpriteIfTouch(sceneX, sceneY);
                        final TMXTile mineStandTile = getTileByMineSprite(mineSprite);
                        if (mineSprite == null) {
                            destTile = tmxLayer.getTMXTileAt(sceneX, sceneY);
                        } else {
                            destTile = mineStandTile;
                        }

                        if (destTile != null && destTile.getGlobalTileID() == GID) {
                            final float[] playerSceneCordinates = hero.getSceneCenterCoordinates();
                            final TMXTile currentTile = tmxLayer.getTMXTileAt(playerSceneCordinates[Constants.VERTEX_INDEX_X],
                                    playerSceneCordinates[Constants.VERTEX_INDEX_Y]);
                            if (destTile != currentTile) {
                                path = tmxUtils.findPath(currentTile, destTile, tmxLayer);
                                showPathTags(path, destTile);
                                changeHeroStatus(QuestHeroStatus.Ready);
                                return true;
                            } else if (currentTile == mineStandTile) {
                                final GameMine curentMine = spriteMineMap.get(mineSprite);
                                handleMine(curentMine);
                            }
                        }
                    } else if (heroStatus == QuestHeroStatus.Ready && destTouchArea.contains(sceneX, sceneY)) {
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
                            } else {
                                alert("精力不够！");
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

    private void changeHeroStatus(final QuestHeroStatus heroStatus) {
        this.heroStatus = heroStatus;
        if (heroStatus == QuestHeroStatus.Stopped) {
            ChatUtils.startGetMsg(activity);
        } else {
            ChatUtils.stopGetMsg(activity);
        }
    }

    private F2ButtonSprite createCancelButton() {
        final F2ButtonSprite cancelButton = createALBF2ButtonSprite(TextureEnum.QUEST_CANCEL_BUTTON, TextureEnum.QUEST_CANCEL_BUTTON_FCS,
                this.simulatedRightX - 135, 220);
        cancelButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (heroStatus == QuestHeroStatus.Ready) {
                    clearPathTages();
                    changeHeroStatus(QuestHeroStatus.Stopped);
                    cancelButton.setVisible(false);
                }
            }
        });
        return cancelButton;
    }

    private void showPathTags(final Path path, final TMXTile destTile) {
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                final int pathSize = path.getSize();
                for (int i = 1; i < path.getSize(); i++) {
                    Sprite tag = null;
                    if (i == pathSize - 1) {
                        tag = createPathEndTag(path, destTile);
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

    private Sprite createPathEndTag(final Path path, final TMXTile destTile) {
        final float[] xs = path.getCoordinatesX();
        final float[] ys = path.getCoordinatesY();
        final int endIndex = path.getSize() - 1;
        final float x1 = xs[endIndex - 1];
        final float y1 = ys[endIndex - 1];
        final float x2 = xs[endIndex];
        final float y2 = ys[endIndex];
        TextureEnum textureEnum = TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        boolean isTreasure = false;
        for (final QuestTile treasure : questTreasureData.getQuestTiles()) {
            if (treasure.getCol() == destTile.getTileColumn() && treasure.getRow() == destTile.getTileRow()) {
                isTreasure = true;
                break;
            }
        }
        if (!isTreasure) {
            for (int i = 0; i < minesMap.size(); i++) {
                final GameMine mine = minesMap.valueAt(i);
                final MineType mineType = mine.getType();
                if ((mine.getCol() + mineType.getxOffset()) == destTile.getTileColumn() && (mine.getRow() + mineType.getyOffset()) == destTile.getTileRow()) {
                    isTreasure = true;
                    break;
                }
            }
        }

        if (x1 > x2 && y1 < y2) { // left up
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_LEFT_TREASURE : TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 == x2 && y1 < y2) { // up
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_RIGHT_TREASURE : TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 < x2 && y1 < y2) { // right up
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_RIGHT_TREASURE : TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 > x2 && y1 == y2) {// left
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_LEFT_TREASURE : TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 < x2 && y1 == y2) {// right
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_RIGHT_TREASURE : TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 > x2 && y1 > y2) {// left down
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_LEFT_TREASURE : TextureEnum.QUEST_PATH_TAG_LEFT_END;
        } else if (x1 == x2 && y1 > y2) {// down
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_RIGHT_TREASURE : TextureEnum.QUEST_PATH_TAG_RIGHT_END;
        } else if (x1 < x2 && y1 > y2) {// right down
            textureEnum = isTreasure ? TextureEnum.QUEST_PATH_TAG_RIGHT_TREASURE : TextureEnum.QUEST_PATH_TAG_RIGHT_END;
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
                        final TileItem item = treasure.getItem();
                        final float treasureX = tmxLayer.getTileX(treasure.getCol()) + 0.5f * tmxTiledMap.getTileWidth();
                        final float treasureY = tmxLayer.getTileY(treasure.getRow()) + 0.5f * tmxTiledMap.getTileHeight();
                        if (item.isInBox()) {
                            final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.TREASURE_BOX);
                            final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                            treasureSprite.animate(500, true);
                            tmxTiledMap.attachChild(treasureSprite);
                            treasureSprites.add(treasureSprite);
                        } else {
                            if (item == TileItem.Wood) {
                                final Sprite treasureSprite = createACImageSprite(TextureEnum.QUEST_TREASURE_WOOD_BIG, treasureX, treasureY);
                                treasureSprite.setScale(0.6f);
                                tmxTiledMap.attachChild(treasureSprite);
                                treasureSprites.add(treasureSprite);
                            } else if (item == TileItem.Mineral) {
                                final Sprite treasureSprite = createACImageSprite(TextureEnum.QUEST_TREASURE_MINERAL_BIG, treasureX, treasureY);
                                treasureSprite.setScale(0.6f);
                                tmxTiledMap.attachChild(treasureSprite);
                                treasureSprites.add(treasureSprite);
                            } else if (item == TileItem.Crystal) {
                                final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(
                                        TiledTextureEnum.TREASURE_CRYSTAL);
                                final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                                treasureSprite.animate(500, true);
                                treasureSprite.setScale(0.6f);
                                tmxTiledMap.attachChild(treasureSprite);
                                treasureSprites.add(treasureSprite);
                            } else if (item == TileItem.PileOfDiamon) {
                                final ITiledTextureRegion tiledTextureRegion = TiledTextureFactory.getInstance().getIextureRegion(
                                        TiledTextureEnum.TREASURE_PILE_DIAMON);
                                final AnimatedSprite treasureSprite = new AnimatedSprite(treasureX, treasureY, tiledTextureRegion, vbom);
                                treasureSprite.animate(600, true);
                                treasureSprite.setScale(0.6f);
                                tmxTiledMap.attachChild(treasureSprite);
                                treasureSprites.add(treasureSprite);
                            }
                        }
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
                    changeHeroStatus(QuestHeroStatus.Started);
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

                    if (heroStatus == QuestHeroStatus.Started) {
                        handleWait();
                    } else {
                        final int backStep = waypointIndex > 4 ? waypointIndex - 4 : 0;
                        handleResult(xs[backStep], ys[backStep], questResult);
                    }

                }

                if (waypointIndex == path.getSize() - 2 && heroStatus != QuestHeroStatus.Waitting) {
                    changeHeroStatus(QuestHeroStatus.Stopped);
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
        changeHeroStatus(QuestHeroStatus.Waitting);
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
                if (heroStatus == QuestHeroStatus.Started) {
                    changeQuestStatus(questResult);
                } else if (heroStatus == QuestHeroStatus.Waitting) {
                    handleCatchUp();
                    changeQuestStatus(questResult);
                    final TMXLayer tmxLayer = tmxTiledMap.getTMXLayers().get(0);
                    final float playerBackX = tmxLayer.getTileX(startTile.getTileColumn()) + 0.5f * tmxTiledMap.getTileWidth();
                    final float playerBackY = tmxLayer.getTileY(startTile.getTileRow()) + TmxUtils.HERO_OFFSET_Y;
                    Debug.e("Waitting!");
                    handleResult(playerBackX, playerBackY, questResult);
                    changeHeroStatus(QuestHeroStatus.Stopped);
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
        final QuestGoStatus goStatus = questResult.getStatus();
        if (goStatus == QuestGoStatus.Arrived) {
            changeHeroStatus(QuestHeroStatus.Arrived);
        } else if (goStatus == QuestGoStatus.Treasure) {
            changeHeroStatus(QuestHeroStatus.Treasure);
        } else if (goStatus == QuestGoStatus.InvalidTreasure) {
            changeHeroStatus(QuestHeroStatus.InvalidTreasure);
        } else if (goStatus == QuestGoStatus.Enemy) {
            changeHeroStatus(QuestHeroStatus.Enemy);
        } else if (goStatus == QuestGoStatus.Task) {
            changeHeroStatus(QuestHeroStatus.Task);
        } else if (goStatus == QuestGoStatus.Mine) {
            changeHeroStatus(QuestHeroStatus.Mine);
        } else {
            changeHeroStatus(QuestHeroStatus.Failed);
        }
    }

    private void handleResult(final float playerBackX, final float playerBackY, final QuestResult questResult) {
        staminaStick.setValue(questResult.getStamina());
        GameUserSession.getInstance().getUserProps().setStamina(questResult.getStamina());
        if (heroStatus != QuestHeroStatus.Failed) {
            if (questResult.isTreasureUpdated()) {
                refreshTreasureSprites(questResult.getQuestTreasureData());
            }
        }
        if (heroStatus == QuestHeroStatus.Failed) {
            changeHeroStatus(QuestHeroStatus.Stopped);
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
        } else if (heroStatus == QuestHeroStatus.Arrived) {
        } else if (heroStatus == QuestHeroStatus.InvalidTreasure) {
            ResourceManager.getInstance().setChildScene(QuestScene.this, new IRCallback<BaseScene>() {

                @Override
                public BaseScene onCallback() {
                    try {
                        return new QuestTreasureScene(questResult, true, activity);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        } else if (heroStatus == QuestHeroStatus.Treasure) {
            receiveQuestTreasure(questResult);
            updateQuestPropsBar();
            ResourceManager.getInstance().setChildScene(QuestScene.this, new IRCallback<BaseScene>() {

                @Override
                public BaseScene onCallback() {
                    try {
                        return new QuestTreasureScene(questResult, false, activity);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
            removeTreasureSprite();
        } else if (heroStatus == QuestHeroStatus.Enemy) {
            ResourceManager.getInstance().setChildScene(QuestScene.this, new IRCallback<BaseScene>() {
                @Override
                public BaseScene onCallback() {
                    try {
                        return new QuestEnemyScene(activity, questResult);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, true);
        } else if (heroStatus == QuestHeroStatus.Task) {
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

        } else if (heroStatus == QuestHeroStatus.Mine) {
            final GameMine curentMine = minesMap.get(questResult.getMineId());
            handleMine(curentMine);
        }
    }

    private void handleMine(final GameMine curentMine) {
        if (curentMine.getOwner().getId() != GameUserSession.getInstance().getId()) {
            attackMine(curentMine.getId());
        } else {
            gatherMine(curentMine.getId());
        }
    }

    private void attackMine(final int id) {
        activity.getGameHub().setSmallChatRoomEnabled(false);
        ResourceManager.getInstance().setChildScene(this, new IRCallback<BaseScene>() {
            @Override
            public BaseScene onCallback() {
                try {
                    final BaseScene attackMineScene = new MineAttackScene(activity, minesMap.get(id), new IParamCallback() {
                        @Override
                        public void onCallback(final Object param) {
                            final User mineOwner = (User) param;
                            if (mineOwner != null) {
                                final UserProperties userProps = GameUserSession.getInstance().getUserProps();
                                if (userProps.getDiamon() >= CostConstants.MINE_ATTACK_COST) {
                                    userProps.setDiamon(userProps.getDiamon() - 2);
                                    diamonText.setText(String.valueOf(userProps.getDiamon()));
                                    ResourceManager.getInstance().setChildScene(QuestScene.this, new IRCallback<BaseScene>() {
                                        @Override
                                        public BaseScene onCallback() {
                                            try {
                                                return new PreBattleScene(activity, mineOwner, BattleType.Mine);
                                            } catch (final IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                    activity.getGameHub().setSmallChatRoomEnabled(true);
                                } else {
                                    QuestScene.this.alert("你的钻石不够！");
                                }
                            } else {
                                activity.getGameHub().setSmallChatRoomEnabled(true);
                            }

                        }
                    });
                    return attackMineScene;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private void gatherMine(final int id) {
        activity.getGameHub().setSmallChatRoomEnabled(false);

        ResourceManager.getInstance().setChildScene(this, new IRCallback<BaseScene>() {
            @Override
            public BaseScene onCallback() {
                try {
                    final BaseScene gatherMineScene = new MineGatherScene(activity, minesMap.get(id), new IParamCallback() {
                        @Override
                        public void onCallback(final Object param) {
                            activity.getGameHub().setSmallChatRoomEnabled(true);
                            updateQuestPropsBar();
                        }
                    });
                    return gatherMineScene;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    private synchronized void refreshMineStatus() {
        final List<GameMine> mines = MineUtils.list();
        for (final GameMine mine : mines) {
            final User owner = mine.getOwner();
            minesMap.get(mine.getId()).update(mine);
            final Text mark = mineMarkMap.get(mine.getId());
            mark.setText(owner.getName());
        }
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
        final UserProperties userProps = GameUserSession.getInstance().getUserProps();
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
        } else if (tileItem == TileItem.Wood) {
            userProps.setGuildContrib(userProps.getGuildContrib() + 5);
        } else if (tileItem == TileItem.Mineral) {
            userProps.setGuildContrib(userProps.getGuildContrib() + 5);
        } else if (tileItem == TileItem.Crystal) {
            userProps.setGuildContrib(userProps.getGuildContrib() + 10);
        } else if (tileItem == TileItem.PileOfDiamon) {
            userProps.setGuildContrib(userProps.getGuildContrib() + 10);
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

    private void setScroolling(final boolean isScroolling) {
        this.isScroolling = isScroolling;
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        setScroolling(true);
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

package com.fight2.scene;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.algorithm.collision.BaseCollisionChecker;

import com.fight2.constant.TextureEnum;
import com.fight2.util.TextureFactory;

public class MainScene extends Scene {
    private final VertexBufferObjectManager vbom;
    private final int cameraWidth;
    private final int cameraHeight;
    private final float cameraCenterX;
    private final float cameraCenterY;
    private final TiledTextureRegion effectRegion;
    private static final float[] GATE_VERTICES = { 20, 468, 548, 475, 506, 382, 451, 338, 60, 344 };
    private static final float[] CONGRESS_VERTICES = { 559, 416, 862, 633, 997, 496, 1003, 451, 930, 380 };
    private static final float[] ARENA_VERTICES = { 518, 392, 863, 386, 861, 283, 534, 270 };
    private static final float[] CAMP_VERTICES = { 619, 154, 680, 220, 745, 231, 831, 158, 715, 112 };
    private static final float[] GUILD_VERTICES = { 933, 381, 1013, 394, 1130, 360, 1129, 246, 1027, 167, 927, 166, 928, 285 };
    private static final float[] MAIL_VERTICES = { 753, 1, 877, 152, 1027, 129, 1131, 186, 1131, 1 };
    private static final float[] SUMMON_VERTICES = { 414, 1, 572, 106, 675, 60, 745, 1 };
    private static final float[] HOTEL_VERTICES = { 196, 303, 440, 311, 456, 172, 352, 109, 202, 121 };
    private static final float[] BILLBOARD_VERTICES = { 425, 91, 485, 258, 616, 238, 609, 94, 570, 132, 525, 89 };

    private final Map<Sprite, Sprite> buttonSprites = new HashMap<Sprite, Sprite>();

    public MainScene(final BaseGameActivity activity, final VertexBufferObjectManager vbom, final int cameraWidth, final int cameraHeight) {
        super();
        this.vbom = vbom;
        this.cameraWidth = cameraWidth;
        this.cameraHeight = cameraHeight;
        this.cameraCenterX = cameraWidth * 0.5f;
        this.cameraCenterY = cameraHeight * 0.5f;
        ITexture texture = null;
        try {
            texture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), "images/main_summon_stone_effect.png");
        } catch (final IOException e) {
            e.printStackTrace();
        }
        effectRegion = TextureRegionFactory.extractTiledFromTexture(texture, 8, 1);
        texture.load();
        init();
    }

    private void init() {
        final Sprite bgSprite = createImageSprite(TextureEnum.MAIN_BG, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite gateSprite = createImageSprite(TextureEnum.MAIN_GATE, 0, 248, 1136, 198);
        final Sprite gateFcsSprite = createImageSprite(TextureEnum.MAIN_GATE_FCS, 0, 248, 1136, 198);
        gateFcsSprite.setVisible(false);
        this.attachChild(gateSprite);
        this.attachChild(gateFcsSprite);
        this.registerTouchArea(gateSprite);
        buttonSprites.put(gateSprite, gateFcsSprite);

        final Sprite townSprite = createImageSprite(TextureEnum.MAIN_TOWN, 2);
        this.attachChild(townSprite);

        final Sprite congressSprite = createImageSprite(TextureEnum.MAIN_CONGRESS, 517, 157, 513, 483);
        this.attachChild(congressSprite);
        final Sprite congressFcsSprite = createImageSprite(TextureEnum.MAIN_CONGRESS_FCS, 517, 157, 513, 483);
        congressFcsSprite.setVisible(false);
        this.attachChild(congressFcsSprite);
        this.registerTouchArea(congressSprite);
        buttonSprites.put(congressSprite, congressFcsSprite);

        final Sprite arenaSprite = createImageSprite(TextureEnum.MAIN_ARENA, 496, 161, 422, 252);
        this.attachChild(arenaSprite);
        final Sprite arenaFcsSprite = createImageSprite(TextureEnum.MAIN_ARENA_FCS, 496, 161, 422, 252);
        arenaFcsSprite.setVisible(false);
        this.attachChild(arenaFcsSprite);
        this.registerTouchArea(arenaSprite);
        buttonSprites.put(arenaSprite, arenaFcsSprite);

        final Sprite treeSprite = createImageSprite(TextureEnum.MAIN_TREE, 5);
        this.attachChild(treeSprite);

        final Sprite houseCenterSprite = createImageSprite(TextureEnum.MAIN_HOUSE_CENTER, 6);
        this.attachChild(houseCenterSprite);

        final Sprite hotelSprite = createImageSprite(TextureEnum.MAIN_HOTEL, 194, 94, 270, 234);
        this.attachChild(hotelSprite);
        final Sprite hotelFcsSprite = createImageSprite(TextureEnum.MAIN_HOTEL_FCS, 194, 94, 270, 234);
        hotelFcsSprite.setVisible(false);
        this.attachChild(hotelFcsSprite);
        this.registerTouchArea(hotelSprite);
        buttonSprites.put(hotelSprite, hotelFcsSprite);

        final Sprite trainingCampSprite = createImageSprite(TextureEnum.MAIN_TRAINING_CAMP, 562, 94, 387, 219);
        this.attachChild(trainingCampSprite);
        final Sprite trainingCampFcsSprite = createImageSprite(TextureEnum.MAIN_TRAINING_CAMP_FCS, 562, 94, 387, 219);
        trainingCampFcsSprite.setVisible(false);
        this.attachChild(trainingCampFcsSprite);
        this.registerTouchArea(trainingCampSprite);
        buttonSprites.put(trainingCampSprite, trainingCampFcsSprite);

        final Sprite guildSprite = createImageSprite(TextureEnum.MAIN_GUILD, 918, 64, 218, 450);
        this.attachChild(guildSprite);
        final Sprite guildFcsSprite = createImageSprite(TextureEnum.MAIN_GUILD_FCS, 918, 64, 218, 450);
        guildFcsSprite.setVisible(false);
        this.attachChild(guildFcsSprite);
        this.registerTouchArea(guildSprite);
        buttonSprites.put(guildSprite, guildFcsSprite);

        final Sprite billboardSprite = createImageSprite(TextureEnum.MAIN_BILLBOARD, 419, 70, 264, 228);
        this.attachChild(billboardSprite);
        final Sprite billboardFcsSprite = createImageSprite(TextureEnum.MAIN_BILLBOARD_FCS, 419, 70, 264, 228);
        billboardFcsSprite.setVisible(false);
        this.attachChild(billboardFcsSprite);
        this.registerTouchArea(billboardSprite);
        buttonSprites.put(billboardSprite, billboardFcsSprite);

        final Sprite peopleSprite = createImageSprite(TextureEnum.MAIN_PEOPLE, 11);
        this.attachChild(peopleSprite);

        final Sprite summonStoneSprite = createImageSprite(TextureEnum.MAIN_SUMMON_STONE, 412, 0, 345, 230);
        this.attachChild(summonStoneSprite);
        final Sprite summonStoneFcsSprite = createImageSprite(TextureEnum.MAIN_SUMMON_STONE_FCS, 412, 0, 345, 230);
        summonStoneFcsSprite.setVisible(false);
        this.attachChild(summonStoneFcsSprite);
        this.registerTouchArea(summonStoneSprite);
        buttonSprites.put(summonStoneSprite, summonStoneFcsSprite);

        final Sprite mailBoxSprite = createImageSprite(TextureEnum.MAIN_MAIL_BOX, 747, 0, 389, 267);
        this.attachChild(mailBoxSprite);
        final Sprite mailBoxFcsSprite = createImageSprite(TextureEnum.MAIN_MAIL_BOX_FCS, 747, 0, 389, 267);
        mailBoxFcsSprite.setVisible(false);
        this.attachChild(mailBoxFcsSprite);
        buttonSprites.put(mailBoxSprite, mailBoxFcsSprite);

        final Sprite houseLeftSprite = createImageSprite(TextureEnum.MAIN_HOUSE_LEFT, 14);
        this.attachChild(houseLeftSprite);

        // final Sprite sunshineSprite = createImageSprite(TextureEnum.MAIN_SUNSHINE, 15);
        // this.attachChild(sunshineSprite);

        final Sprite pigeonSprite = createImageSprite(TextureEnum.MAIN_PIGEON, 16);
        this.attachChild(pigeonSprite);

        final AnimatedSprite summonStoneEffect = new AnimatedSprite(560, 120, effectRegion, vbom);
        summonStoneEffect.animate(125);
        this.attachChild(summonStoneEffect);

        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                final float x = pSceneTouchEvent.getX();
                final float y = pSceneTouchEvent.getY();
                resetButtons();
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove()) {
                    if (checkContains(MAIL_VERTICES, x, y)) {
                        focusSprite(mailBoxSprite);
                    } else if (checkContains(SUMMON_VERTICES, x, y)) {
                        focusSprite(summonStoneSprite);
                    } else if (checkContains(BILLBOARD_VERTICES, x, y)) {
                        focusSprite(billboardSprite);
                    } else if (checkContains(GUILD_VERTICES, x, y)) {
                        focusSprite(guildSprite);
                    } else if (checkContains(CAMP_VERTICES, x, y)) {
                        focusSprite(trainingCampSprite);
                    } else if (checkContains(HOTEL_VERTICES, x, y)) {
                        focusSprite(hotelSprite);
                    } else if (checkContains(ARENA_VERTICES, x, y)) {
                        focusSprite(arenaSprite);
                    } else if (checkContains(CONGRESS_VERTICES, x, y)) {
                        focusSprite(congressSprite);
                    } else if (checkContains(GATE_VERTICES, x, y)) {
                        focusSprite(gateSprite);
                    }
                } else if (pSceneTouchEvent.isActionUp()) {
                    if (checkContains(MAIL_VERTICES, x, y)) {
                        unfocusSprite(mailBoxSprite);
                    } else if (checkContains(SUMMON_VERTICES, x, y)) {
                        unfocusSprite(summonStoneSprite);
                    } else if (checkContains(BILLBOARD_VERTICES, x, y)) {
                        unfocusSprite(billboardSprite);
                    } else if (checkContains(GUILD_VERTICES, x, y)) {
                        unfocusSprite(guildSprite);
                    } else if (checkContains(CAMP_VERTICES, x, y)) {
                        unfocusSprite(trainingCampSprite);
                    } else if (checkContains(HOTEL_VERTICES, x, y)) {
                        unfocusSprite(hotelSprite);
                    } else if (checkContains(ARENA_VERTICES, x, y)) {
                        unfocusSprite(arenaSprite);
                    } else if (checkContains(CONGRESS_VERTICES, x, y)) {
                        unfocusSprite(congressSprite);
                    } else if (checkContains(GATE_VERTICES, x, y)) {
                        unfocusSprite(gateSprite);
                    }
                }
                return true;
            }

            private void resetButtons() {
                unfocusSprite(mailBoxSprite);
                unfocusSprite(summonStoneSprite);
                unfocusSprite(billboardSprite);
                unfocusSprite(guildSprite);
                unfocusSprite(trainingCampSprite);
                unfocusSprite(hotelSprite);
                unfocusSprite(arenaSprite);
                unfocusSprite(congressSprite);
                unfocusSprite(gateSprite);
            }
        });
    }

    private Sprite createImageSprite(final TextureEnum textureEnum, final int pZIndex) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final Sprite sprite = new Sprite(cameraCenterX, cameraCenterY, cameraWidth, cameraHeight, texture, vbom);
        sprite.setZIndex(pZIndex);
        return sprite;
    }

    private Sprite createImageSprite(final TextureEnum textureEnum, final float x, final float y, final float width, final float height) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    private void focusSprite(final Sprite sprite) {
        final Sprite spriteFcs = buttonSprites.get(sprite);
        sprite.setVisible(false);
        spriteFcs.setVisible(true);
    }

    private void unfocusSprite(final Sprite sprite) {
        final Sprite spriteFcs = buttonSprites.get(sprite);
        sprite.setVisible(true);
        spriteFcs.setVisible(false);
    }

    private boolean checkContains(final float[] pVertices, final float pX, final float pY) {
        return BaseCollisionChecker.checkContains(pVertices, pVertices.length / 2, pX, pY);
    }
}

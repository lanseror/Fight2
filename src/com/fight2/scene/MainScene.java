package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.particle.BatchedPseudoSpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.AccelerationParticleInitializer;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.ExpireParticleInitializer;
import org.andengine.entity.particle.initializer.ScaleParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.util.algorithm.collision.BaseCollisionChecker;

import android.opengl.GLES20;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.UserProperties;
import com.fight2.entity.engine.CardAvatar;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.quest.QuestTask;
import com.fight2.entity.quest.QuestTask.UserTaskStatus;
import com.fight2.util.ICallback;
import com.fight2.util.IRCallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TaskUtils;
import com.fight2.util.TextureFactory;

public class MainScene extends BaseScene {
    private static final float[] GATE_VERTICES = { 350, 468, 548, 475, 506, 382, 451, 338, 350, 344 };
    private static final float[] CONGRESS_VERTICES = { 559, 416, 862, 633, 997, 496, 1003, 451, 930, 380 };
    private static final float[] ARENA_VERTICES = { 518, 392, 863, 386, 861, 283, 534, 270 };
    private static final float[] CAMP_VERTICES = { 619, 154, 680, 220, 745, 231, 831, 158, 715, 112 };
    private static final float[] GUILD_VERTICES = { 933, 381, 1013, 394, 1130, 360, 1129, 246, 1027, 167, 927, 166, 928, 285 };
    private static final float[] MAIL_VERTICES = { 753, 1, 877, 152, 1027, 129, 1131, 186, 1131, 1 };
    private static final float[] SUMMON_VERTICES = { 414, 1, 572, 106, 675, 60, 745, 1 };
    private static final float[] HOTEL_VERTICES = { 196, 303, 440, 311, 456, 172, 352, 109, 202, 121 };
    // private static final float[] BILLBOARD_VERTICES = { 425, 91, 485, 258, 616, 238, 609, 94, 570, 132, 525, 89 };
    private static final float[] STOREROOM_VERTICES = { 0, 420, 50, 360, 150, 190, 150, 90, 0, 90 };

    private final Map<Sprite, Sprite> buttonSprites = new HashMap<Sprite, Sprite>();

    private final Font mFont;
    private final Sprite summonTip;
    private final Sprite arenaTip;
    private final Sprite campTip;
    private final Sprite gateTip;
    private final Sprite storeroomTip;
    private final Sprite guildTip;
    private final Sprite congressTip;
    private final Sprite hotelTip;
    private final Sprite mailBoxTip;
    private final List<IEntity> tips = new ArrayList<IEntity>();

    private final PartyInfo myPartyInfo = GameUserSession.getInstance().getPartyInfo();
    private final Party[] myParties = myPartyInfo.getParties();
    private IEntity avatarBox;
    private final int avatarCardId = -1;
    private final float avatarSize = 90;
    private final float avatarHalfSize = avatarSize * 0.5f;
    private final Sprite smallMsgNewSprite = createALBImageSprite(TextureEnum.MAIN_MSG_NEW_SMALL, 0, 0);

    public MainScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 20);
        final float tipTextX = TextureEnum.MAIN_TIPS.getWidth() * 0.5f;
        final float tipTextY = TextureEnum.MAIN_TIPS.getHeight() * 0.5f;
        final float tipTextX2 = TextureEnum.MAIN_TIPS2.getWidth() * 0.5f;
        final float tipTextY2 = TextureEnum.MAIN_TIPS2.getHeight() * 0.5f;
        final Text summonText = new Text(tipTextX, tipTextY, mFont, "召唤石", vbom);
        summonText.setColor(0XFFDFDCD7);
        summonTip = createALBImageSprite(TextureEnum.MAIN_TIPS, 505, 25);
        summonTip.attachChild(summonText);
        tips.add(summonTip);

        final Text arenaText = new Text(tipTextX, tipTextY, mFont, "竞技场", vbom);
        arenaText.setColor(0XFFDFDCD7);
        arenaTip = createALBImageSprite(TextureEnum.MAIN_TIPS, 630, 365);
        arenaTip.attachChild(arenaText);
        tips.add(arenaTip);

        final Text campText = new Text(tipTextX, tipTextY, mFont, "训练营", vbom);
        campText.setColor(0XFFDFDCD7);
        campTip = createALBImageSprite(TextureEnum.MAIN_TIPS, 652, 210);
        campTip.attachChild(campText);
        tips.add(campTip);

        final Text gateText = new Text(tipTextX2, tipTextY2, mFont, "出城", vbom);
        gateText.setColor(0XFFDFDCD7);
        gateTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 362, 438);
        gateTip.attachChild(gateText);
        tips.add(gateTip);

        final Text storeroomText = new Text(tipTextX2, tipTextY2, mFont, "仓库", vbom);
        storeroomText.setColor(0XFFDFDCD7);
        storeroomTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 40, 230);
        storeroomTip.attachChild(storeroomText);
        tips.add(storeroomTip);

        final Text guildText = new Text(tipTextX2, tipTextY2, mFont, "公会", vbom);
        guildText.setColor(0XFFDFDCD7);
        guildTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 960, 300);
        guildTip.attachChild(guildText);
        tips.add(guildTip);

        final Text congressText = new Text(tipTextX2, tipTextY2, mFont, "国会", vbom);
        congressText.setColor(0XFFDFDCD7);
        congressTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 800, 500);
        congressTip.attachChild(congressText);
        tips.add(congressTip);

        final Text hotelText = new Text(tipTextX2, tipTextY2, mFont, "酒馆", vbom);
        hotelText.setColor(0XFFDFDCD7);
        hotelTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 290, 285);
        hotelTip.attachChild(hotelText);
        tips.add(hotelTip);

        final Text mailBoxText = new Text(tipTextX2, tipTextY2, mFont, "信箱", vbom);
        mailBoxText.setColor(0XFFDFDCD7);
        mailBoxTip = createALBImageSprite(TextureEnum.MAIN_TIPS2, 990, 130);
        mailBoxTip.attachChild(mailBoxText);
        tips.add(mailBoxTip);

        init();
    }

    @Override
    protected void init() {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.MAIN_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite gateSprite = createALBImageSprite(TextureEnum.MAIN_GATE, 0, 248);
        final Sprite gateFcsSprite = createALBImageSprite(TextureEnum.MAIN_GATE_FCS, 0, 248);
        gateFcsSprite.setVisible(false);
        this.attachChild(gateSprite);
        this.attachChild(gateFcsSprite);
        this.registerTouchArea(gateSprite);
        buttonSprites.put(gateSprite, gateFcsSprite);

        final Sprite townSprite = createALBImageSprite(TextureEnum.MAIN_TOWN, 0, 0);
        this.attachChild(townSprite);

        final Sprite congressSprite = createALBImageSprite(TextureEnum.MAIN_CONGRESS, 517, 157);
        this.attachChild(congressSprite);
        final Sprite congressFcsSprite = createALBImageSprite(TextureEnum.MAIN_CONGRESS_FCS, 517, 157);
        congressFcsSprite.setVisible(false);
        this.attachChild(congressFcsSprite);
        this.registerTouchArea(congressSprite);
        buttonSprites.put(congressSprite, congressFcsSprite);

        final Sprite arenaSprite = createALBImageSprite(TextureEnum.MAIN_ARENA, 496, 161);
        this.attachChild(arenaSprite);
        final Sprite arenaFcsSprite = createALBImageSprite(TextureEnum.MAIN_ARENA_FCS, 496, 161);
        arenaFcsSprite.setVisible(false);
        this.attachChild(arenaFcsSprite);
        this.registerTouchArea(arenaSprite);
        buttonSprites.put(arenaSprite, arenaFcsSprite);

        final Sprite treeSprite = createALBImageSprite(TextureEnum.MAIN_TREE, 0, 0);
        this.attachChild(treeSprite);

        final Sprite houseCenterSprite = createALBImageSprite(TextureEnum.MAIN_HOUSE_CENTER, 0, 0);
        this.attachChild(houseCenterSprite);

        final Sprite hotelSprite = createALBImageSprite(TextureEnum.MAIN_HOTEL, 194, 94);
        this.attachChild(hotelSprite);
        final Sprite hotelFcsSprite = createALBImageSprite(TextureEnum.MAIN_HOTEL_FCS, 194, 94);
        hotelFcsSprite.setVisible(false);
        this.attachChild(hotelFcsSprite);
        this.registerTouchArea(hotelSprite);
        buttonSprites.put(hotelSprite, hotelFcsSprite);

        final Sprite trainingCampSprite = createALBImageSprite(TextureEnum.MAIN_TRAINING_CAMP, 562, 94);
        this.attachChild(trainingCampSprite);
        final Sprite trainingCampFcsSprite = createALBImageSprite(TextureEnum.MAIN_TRAINING_CAMP_FCS, 562, 94);
        trainingCampFcsSprite.setVisible(false);
        this.attachChild(trainingCampFcsSprite);
        this.registerTouchArea(trainingCampSprite);
        buttonSprites.put(trainingCampSprite, trainingCampFcsSprite);

        final Sprite guildSprite = createALBImageSprite(TextureEnum.MAIN_GUILD, 918, 64);
        this.attachChild(guildSprite);
        final Sprite guildFcsSprite = createALBImageSprite(TextureEnum.MAIN_GUILD_FCS, 918, 64);
        guildFcsSprite.setVisible(false);
        this.attachChild(guildFcsSprite);
        this.registerTouchArea(guildSprite);
        buttonSprites.put(guildSprite, guildFcsSprite);

        // final Sprite billboardSprite = createALBImageSprite(TextureEnum.MAIN_BILLBOARD, 419, 70);
        // this.attachChild(billboardSprite);
        // final Sprite billboardFcsSprite = createALBImageSprite(TextureEnum.MAIN_BILLBOARD_FCS, 419, 70);
        // billboardFcsSprite.setVisible(false);
        // this.attachChild(billboardFcsSprite);
        // this.registerTouchArea(billboardSprite);
        // buttonSprites.put(billboardSprite, billboardFcsSprite);

        // final Sprite peopleSprite = createALBImageSprite(TextureEnum.MAIN_PEOPLE, 0, 0);
        // this.attachChild(peopleSprite);

        final Sprite summonStoneSprite = createALBImageSprite(TextureEnum.MAIN_SUMMON_STONE, 412, 0);
        this.attachChild(summonStoneSprite);
        final Sprite summonStoneFcsSprite = createALBImageSprite(TextureEnum.MAIN_SUMMON_STONE_FCS, 412, 0);
        summonStoneFcsSprite.setVisible(false);
        this.attachChild(summonStoneFcsSprite);
        this.registerTouchArea(summonStoneSprite);
        buttonSprites.put(summonStoneSprite, summonStoneFcsSprite);

        final Sprite mailBoxSprite = createALBImageSprite(TextureEnum.MAIN_MAIL_BOX, 747, 0);
        this.attachChild(mailBoxSprite);
        final Sprite mailBoxFcsSprite = createALBImageSprite(TextureEnum.MAIN_MAIL_BOX_FCS, 747, 0);
        mailBoxFcsSprite.setVisible(false);
        this.attachChild(mailBoxFcsSprite);
        buttonSprites.put(mailBoxSprite, mailBoxFcsSprite);

        final Sprite storeroomSprite = createALBImageSprite(TextureEnum.MAIN_STOREROOM, 0, 0);
        this.attachChild(storeroomSprite);
        final Sprite storeroomFcsSprite = createALBImageSprite(TextureEnum.MAIN_STOREROOM_FCS, 0, 0);
        storeroomFcsSprite.setVisible(false);
        this.attachChild(storeroomFcsSprite);
        buttonSprites.put(storeroomSprite, storeroomFcsSprite);

        // final Sprite sunshineSprite = createImageSprite(TextureEnum.MAIN_SUNSHINE, 15);
        // this.attachChild(sunshineSprite);

        final Sprite pigeonSprite = createALBImageSprite(TextureEnum.MAIN_PIGEON, 0, 0);
        this.attachChild(pigeonSprite);

        // final ITiledTextureRegion summonEffect = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.MAIN_SUMMON_STONE_EFFECT);
        // final AnimatedSprite summonStoneEffect = new AnimatedSprite(560, 120, summonEffect, vbom);
        // summonStoneEffect.animate(125);
        // this.attachChild(summonStoneEffect);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);
        final UserProperties userProps = QuestUtils.getUserProperties(activity);
        GameUserSession.getInstance().setUserProps(userProps);
        final Font font = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        final Text diamonText = new Text(123, 24, font, String.valueOf(userProps.getDiamon()), 8, vbom);
        rechargeSprite.attachChild(diamonText);

        final TextureEnum playerInfoEnum = TextureEnum.MAIN_PLAYER_INFO;
        avatarBox = new Rectangle(this.simulatedLeftX + avatarHalfSize + 18, this.simulatedHeight - avatarHalfSize - 20, avatarSize, avatarSize, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.PlayerInfo);
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.attachChild(avatarBox);
        this.registerTouchArea(avatarBox);
        final Sprite playerInfoSprite = createALBImageSprite(playerInfoEnum, this.simulatedLeftX, this.simulatedHeight - playerInfoEnum.getHeight());

        this.attachChild(playerInfoSprite);
        final Sprite playerInfoStaminaSprite = createALBImageSprite(TextureEnum.MAIN_PLAYER_INFO_STAMINA, 114, 86);
        playerInfoSprite.attachChild(playerInfoStaminaSprite);
        final Sprite playerInfoStaminaBoxSprite = createALBImageSprite(TextureEnum.MAIN_PLAYER_INFO_STAMINA_BOX, 100, 83);
        playerInfoSprite.attachChild(playerInfoStaminaBoxSprite);

        for (final IEntity tip : tips) {
            // tip.setVisible(false);
            tip.setAlpha(0.75f);
            this.attachChild(tip);
        }

        createMsgSprite();

        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                final float x = pSceneTouchEvent.getX();
                final float y = pSceneTouchEvent.getY();
                resetButtons();
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove()) {
                    // for (final IEntity tip : tips) {
                    // tip.setVisible(true);
                    // }
                    if (checkContains(MAIL_VERTICES, x, y)) {
                        focusSprite(mailBoxSprite);
                    } else if (checkContains(SUMMON_VERTICES, x, y)) {
                        focusSprite(summonStoneSprite);
                        // } else if (checkContains(BILLBOARD_VERTICES, x, y)) {
                        // focusSprite(billboardSprite);
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
                    } else if (checkContains(STOREROOM_VERTICES, x, y)) {
                        focusSprite(storeroomSprite);
                    }
                } else if (pSceneTouchEvent.isActionUp()) {
                    // for (final IEntity tip : tips) {
                    // tip.setVisible(false);
                    // }

                    if (checkContains(MAIL_VERTICES, x, y)) {
                        unfocusSprite(mailBoxSprite);
                        alert("未开启！");
                    } else if (checkContains(SUMMON_VERTICES, x, y)) {
                        unfocusSprite(summonStoneSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Summon);
                        // } else if (checkContains(BILLBOARD_VERTICES, x, y)) {
                        // unfocusSprite(billboardSprite);
                    } else if (checkContains(GUILD_VERTICES, x, y)) {
                        unfocusSprite(guildSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Guild);
                    } else if (checkContains(CAMP_VERTICES, x, y)) {
                        unfocusSprite(trainingCampSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Party);
                    } else if (checkContains(HOTEL_VERTICES, x, y)) {
                        unfocusSprite(hotelSprite);
                        alert("未开启！");
                    } else if (checkContains(ARENA_VERTICES, x, y)) {
                        unfocusSprite(arenaSprite);
                        final Card myLeader = myParties[0].getCards()[0];
                        if (myLeader != null) {
                            ResourceManager.getInstance().setCurrentScene(SceneEnum.ArenaList);
                        } else {
                            alert("必须要有领军人物才能进去竞技场！");
                        }
                    } else if (checkContains(CONGRESS_VERTICES, x, y)) {
                        unfocusSprite(congressSprite);
                        alert("未开启！");
                    } else if (checkContains(GATE_VERTICES, x, y)) {
                        unfocusSprite(gateSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Quest);
                    } else if (checkContains(STOREROOM_VERTICES, x, y)) {
                        unfocusSprite(storeroomSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Storeroom);
                    }
                }
                return true;
            }

            private void resetButtons() {
                unfocusSprite(mailBoxSprite);
                unfocusSprite(summonStoneSprite);
                // unfocusSprite(billboardSprite);
                unfocusSprite(guildSprite);
                unfocusSprite(trainingCampSprite);
                unfocusSprite(hotelSprite);
                unfocusSprite(arenaSprite);
                unfocusSprite(congressSprite);
                unfocusSprite(gateSprite);
                unfocusSprite(storeroomSprite);
            }
        });

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        createSummonAnimation();
    }

    private void createSummonAnimation() {
        {
            final BatchedPseudoSpriteParticleSystem particleSystem = new BatchedPseudoSpriteParticleSystem(
                    new PointParticleEmitter(this.cameraCenterX + 5, 100), 0.45f, 0.45f, 5, TextureFactory.getInstance().getAssetTextureRegion(
                            TextureEnum.COMMON_PARTICLE_POINT), vbom);
            particleSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
            particleSystem.addParticleInitializer(new VelocityParticleInitializer<Entity>(12, 16, 16, 18));
            particleSystem.addParticleInitializer(new AccelerationParticleInitializer<Entity>(-5f, 0));
            // particleSystem.addParticleInitializer(new RotationParticleInitializer<Entity>(0.0f, 360.0f));color="#ffe679"color="#ffe679"
            particleSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(0.8f));
            particleSystem.addParticleInitializer(new ColorParticleInitializer<Entity>(1f, 0.9f, 0.5f));
            particleSystem.addParticleInitializer(new AlphaParticleInitializer<Entity>(0.3f));
            particleSystem.addParticleInitializer(new ExpireParticleInitializer<Entity>(6f));

            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0, 0.5f, 0.3f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0.5f, 1.25f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(2f, 2.75f, 0.7f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(3.25f, 4f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4f, 4.5f, 0.7f, 1f));
            // particleSystem.addParticleModifier(new ColorParticleModifier<Entity>(2.5f, 5.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4.5f, 6f, 1.0f, 0.0f));

            this.attachChild(particleSystem);
        }
        {
            final BatchedPseudoSpriteParticleSystem particleSystem = new BatchedPseudoSpriteParticleSystem(
                    new PointParticleEmitter(this.cameraCenterX - 5, 100), 0.45f, 0.45f, 5, TextureFactory.getInstance().getAssetTextureRegion(
                            TextureEnum.COMMON_PARTICLE_POINT), vbom);
            particleSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
            particleSystem.addParticleInitializer(new VelocityParticleInitializer<Entity>(-16, -12, 16, 18));
            particleSystem.addParticleInitializer(new AccelerationParticleInitializer<Entity>(5f, 0));
            // particleSystem.addParticleInitializer(new RotationParticleInitializer<Entity>(0.0f, 360.0f));color="#ffe679"color="#ffe679"
            particleSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(0.8f));
            particleSystem.addParticleInitializer(new ColorParticleInitializer<Entity>(1f, 0.9f, 0.5f));
            particleSystem.addParticleInitializer(new AlphaParticleInitializer<Entity>(0.3f));
            particleSystem.addParticleInitializer(new ExpireParticleInitializer<Entity>(6f));

            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0, 0.5f, 0.3f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0.5f, 1.25f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(2f, 2.75f, 0.7f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(3.25f, 4f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4f, 4.5f, 0.7f, 1f));
            // particleSystem.addParticleModifier(new ColorParticleModifier<Entity>(2.5f, 5.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4.5f, 6f, 1.0f, 0.0f));
            this.attachChild(particleSystem);
        }

        {
            final BatchedPseudoSpriteParticleSystem particleSystem = new BatchedPseudoSpriteParticleSystem(new PointParticleEmitter(this.cameraCenterX, 110),
                    0.4f, 0.4f, 5, TextureFactory.getInstance().getAssetTextureRegion(TextureEnum.COMMON_PARTICLE_POINT), vbom);
            particleSystem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
            particleSystem.addParticleInitializer(new VelocityParticleInitializer<Entity>(0, 0, 17, 19));
            // particleSystem.addParticleInitializer(new AccelerationParticleInitializer<Entity>(5f, 0));
            // particleSystem.addParticleInitializer(new RotationParticleInitializer<Entity>(0.0f, 360.0f));color="#ffe679"color="#ffe679"
            particleSystem.addParticleInitializer(new ScaleParticleInitializer<Entity>(0.8f));
            particleSystem.addParticleInitializer(new ColorParticleInitializer<Entity>(1f, 0.9f, 0.5f));
            particleSystem.addParticleInitializer(new AlphaParticleInitializer<Entity>(0.3f));
            particleSystem.addParticleInitializer(new ExpireParticleInitializer<Entity>(6f));

            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0, 0.5f, 0.3f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(0.5f, 1.25f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(2f, 2.75f, 0.7f, 1f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(3.25f, 4f, 1f, 0.7f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4f, 4.5f, 0.7f, 1f));
            // particleSystem.addParticleModifier(new ColorParticleModifier<Entity>(2.5f, 5.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f));
            particleSystem.addParticleModifier(new AlphaParticleModifier<Entity>(4.5f, 6f, 1.0f, 0.0f));
            this.attachChild(particleSystem);
        }
    }

    private void createMsgSprite() {
        final F2ButtonSprite smallMsgSprite = this.createACF2ButtonSprite(TextureEnum.MAIN_MSG_SMALL, TextureEnum.MAIN_MSG_SMALL, 170, 475);
        this.attachChild(smallMsgSprite);
        this.registerTouchArea(smallMsgSprite);

        smallMsgNewSprite.setVisible(false);
        smallMsgSprite.attachChild(smallMsgNewSprite);
        smallMsgSprite.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                smallMsgSprite.setVisible(false);
                ResourceManager.getInstance().setChildScene(MainScene.this, new IRCallback<BaseScene>() {

                    @Override
                    public BaseScene onCallback() {
                        try {
                            final BaseScene taskGuideScene = new TaskGuideScene(activity, new ICallback() {
                                @Override
                                public void onCallback() {
                                    smallMsgSprite.setVisible(true);
                                    MainScene.this.updateScene();
                                }
                            });
                            return taskGuideScene;
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }

                    }

                });
            }

        });
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

    @Override
    protected void playAnimation() {
        activity.getGameHub().needSmallChatRoom(true);
    }

    @Override
    public void updateScene() {
        final Card myLeader = myParties[0].getCards()[0];
        if (myLeader != null && myLeader.getId() != this.avatarCardId) {
            final Sprite avatarCoverSprite = new CardAvatar(avatarHalfSize, avatarHalfSize, avatarSize, avatarSize, myLeader, activity);
            avatarBox.attachChild(avatarCoverSprite);
        }
        final QuestTask task = TaskUtils.getTask();
        if (task.getStatus() == UserTaskStatus.Ready || task.getStatus() == UserTaskStatus.Finished) {
            smallMsgNewSprite.setVisible(true);
        } else {
            smallMsgNewSprite.setVisible(false);
        }
    }

    @Override
    public void leaveScene() {
    }
}

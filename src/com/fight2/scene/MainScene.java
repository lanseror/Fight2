package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.algorithm.collision.BaseCollisionChecker;

import android.graphics.Color;
import android.view.View;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.constant.TiledTextureEnum;
import com.fight2.entity.ChatMessage;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ChatTextHandler;
import com.fight2.util.ChatUtils;
import com.fight2.util.ChatUtils.DisplayChannel;
import com.fight2.util.ResourceManager;
import com.fight2.util.TiledTextureFactory;

public class MainScene extends BaseScene {
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

    private IEntity smallChatRoom;
    private final Timer displayChatTimer = new Timer();
    private Text chatText;
    private final Text chatTimeText;
    private final ChatTextHandler chatTextHandler;
    private final static int CHAT_SIZE = 500;
    private final static String SAMPLE_CHAT_STRING = "\n";
    private final static String SAMPLE_CHAT_TIME_STRING = "\n";
    private final static String TEST_CHAT_TIME_STRING = "16:30\n16:30";
    private final StringBuilder chatString = new StringBuilder(SAMPLE_CHAT_STRING);
    private final StringBuilder chatTimeString = new StringBuilder(SAMPLE_CHAT_TIME_STRING);

    private final IEntity chatBox;

    private final Font mFont;
    private final Text summonText;
    private final Text arenaText;
    private final Text campText;
    private final List<Text> tipTexts = new ArrayList<Text>();

    public MainScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().getFont(FontEnum.Main);
        summonText = new Text(this.simulatedLeftX + 570, 25, mFont, "召唤石", vbom);
        arenaText = new Text(this.simulatedLeftX + 670, 345, mFont, "竞技场", vbom);
        campText = new Text(this.simulatedLeftX + 720, 200, mFont, "训练营", vbom);
        tipTexts.add(summonText);
        tipTexts.add(arenaText);
        tipTexts.add(campText);

        chatBox = new Rectangle(this.simulatedLeftX + 755 * 0.5f, 640 * 0.5f, 755, 640, vbom);
        chatBox.setColor(Color.BLACK);
        chatBox.setAlpha(0.5f);
        final Font chatFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        chatText = new Text(0, 0, chatFont, SAMPLE_CHAT_STRING, CHAT_SIZE, vbom);
        chatText.setColor(0XFFE8BD80);
        final Font chatTimeFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        chatTimeText = new Text(0, 0, chatTimeFont, SAMPLE_CHAT_TIME_STRING, CHAT_SIZE, vbom);
        chatTimeText.setColor(0XFFE8BD80);

        chatTextHandler = new ChatTextHandler(CHAT_SIZE, vbom);
        init();
        createChatRoom();
    }

    private void createChatRoom() {
        smallChatRoom = new Rectangle(this.simulatedLeftX + 900 * 0.5f, 75 * 0.5f, 900, 75, vbom);
        smallChatRoom.setColor(Color.BLACK);
        smallChatRoom.setAlpha(0.3f);
        this.attachChild(smallChatRoom);

        final F2ButtonSprite openButton = createALBF2ButtonSprite(TextureEnum.CHAT_INPUT_OPEN, TextureEnum.CHAT_INPUT_OPEN, 0, 0);
        openButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Chat);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.getChatText().setVisibility(View.VISIBLE);
                    }

                });

            }
        });
        smallChatRoom.attachChild(openButton);
        adjustChatTextPosition();
        smallChatRoom.attachChild(chatText);
        final Font testChatFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
        final Text testChatTimeText = new Text(0, 0, testChatFont, TEST_CHAT_TIME_STRING, CHAT_SIZE, vbom);
        final float chatTimeTextWidth = testChatTimeText.getWidth();
        final float chatTimeTextHeight = testChatTimeText.getHeight();

        chatTimeText.setPosition(smallChatRoom.getWidth() - chatTimeTextWidth * 0.5f - 10, chatTimeTextHeight * 0.5f + 5);
        smallChatRoom.attachChild(chatTimeText);
        this.registerTouchArea(openButton);
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

        final Sprite billboardSprite = createALBImageSprite(TextureEnum.MAIN_BILLBOARD, 419, 70);
        this.attachChild(billboardSprite);
        final Sprite billboardFcsSprite = createALBImageSprite(TextureEnum.MAIN_BILLBOARD_FCS, 419, 70);
        billboardFcsSprite.setVisible(false);
        this.attachChild(billboardFcsSprite);
        this.registerTouchArea(billboardSprite);
        buttonSprites.put(billboardSprite, billboardFcsSprite);

        final Sprite peopleSprite = createALBImageSprite(TextureEnum.MAIN_PEOPLE, 0, 0);
        this.attachChild(peopleSprite);

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

        final Sprite houseLeftSprite = createALBImageSprite(TextureEnum.MAIN_HOUSE_LEFT, 0, 0);
        this.attachChild(houseLeftSprite);

        // final Sprite sunshineSprite = createImageSprite(TextureEnum.MAIN_SUNSHINE, 15);
        // this.attachChild(sunshineSprite);

        final Sprite pigeonSprite = createALBImageSprite(TextureEnum.MAIN_PIGEON, 0, 0);
        this.attachChild(pigeonSprite);

        final ITiledTextureRegion summonEffect = TiledTextureFactory.getInstance().getIextureRegion(TiledTextureEnum.MAIN_SUMMON_STONE_EFFECT);
        final AnimatedSprite summonStoneEffect = new AnimatedSprite(560, 120, summonEffect, vbom);
        summonStoneEffect.animate(125);
        this.attachChild(summonStoneEffect);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        for (final Text tipText : tipTexts) {
            tipText.setVisible(false);
            this.attachChild(tipText);
        }

        this.setOnSceneTouchListener(new IOnSceneTouchListener() {
            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                final float x = pSceneTouchEvent.getX();
                final float y = pSceneTouchEvent.getY();
                resetButtons();
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove()) {
                    for (final Text tipText : tipTexts) {
                        tipText.setVisible(true);
                    }
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
                    for (final Text tipText : tipTexts) {
                        tipText.setVisible(false);
                    }

                    if (checkContains(MAIL_VERTICES, x, y)) {
                        unfocusSprite(mailBoxSprite);
                    } else if (checkContains(SUMMON_VERTICES, x, y)) {
                        unfocusSprite(summonStoneSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Summon);
                    } else if (checkContains(BILLBOARD_VERTICES, x, y)) {
                        unfocusSprite(billboardSprite);
                    } else if (checkContains(GUILD_VERTICES, x, y)) {
                        unfocusSprite(guildSprite);
                    } else if (checkContains(CAMP_VERTICES, x, y)) {
                        unfocusSprite(trainingCampSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Party);
                    } else if (checkContains(HOTEL_VERTICES, x, y)) {
                        unfocusSprite(hotelSprite);
                    } else if (checkContains(ARENA_VERTICES, x, y)) {
                        unfocusSprite(arenaSprite);
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
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

        scheduleGetChatMessage();
        scheduleDisplayChat();
    }

    private void adjustChatTextPosition() {
        final float openButtonWidth = TextureEnum.CHAT_INPUT_OPEN.getWidth();
        final float chatTextWidth = chatText.getWidth();
        final float chatTextHeight = chatText.getHeight();
        chatText.setPosition(openButtonWidth + chatTextWidth * 0.5f + 5, chatTextHeight * 0.5f + 5);
    }

    private void scheduleDisplayChat() {
        displayChatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                final ChatMessage chatMessage = ChatUtils.getDisplayMessage(DisplayChannel.MiniChatRoom);
                if (chatMessage != null) {
                    final int deleteIndex = chatString.indexOf("\n");
                    chatString.delete(0, deleteIndex + 1);
                    final String sender = chatMessage.getSender();
                    final String content = chatMessage.getContent();
                    chatString.append(chatTextHandler.handle(sender, content));

                    final String date = chatMessage.getDate();
                    final int deleteChatTimeIndex = chatTimeString.indexOf("\n");
                    chatTimeString.delete(0, deleteChatTimeIndex + 1);
                    chatTimeString.append("\n");
                    chatTimeString.append(date);
                    chatTimeText.setText(chatTimeString);

                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            chatText.detachSelf();
                            final Font chatFont = ResourceManager.getInstance().getFont(FontEnum.Default, 28);
                            chatText = new Text(0, 0, chatFont, chatString, vbom);
                            chatText.setColor(0XFFE8BD80);
                            smallChatRoom.attachChild(chatText);
                            adjustChatTextPosition();
                        }
                    });

                }

            }
        }, 0, 1000);// Update text every second
    }

    private void scheduleGetChatMessage() {
        final Timer getChatTimer = new Timer();
        getChatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ChatUtils.get(activity);
            }
        }, 0, 1000);// Update text every second
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
    public void updateScene() {
    }

    @Override
    public void leaveScene() {
    }
}

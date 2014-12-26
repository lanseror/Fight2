package com.fight2.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.EntityBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.ChatMessage;
import com.fight2.entity.engine.ChatBoxCloseButton;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ChatUtils;
import com.fight2.util.ChatUtils.DisplayChannel;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class ChatScene extends BaseScene implements IScrollDetectorListener {
    private final ChatBoxCloseButton closeButton;
    private final EditText editText = activity.getChatText();
    private final IEntity chatContainer;
    private final float chatContainerInitY;
    private final IEntity chatScrollArea;
    private float allMessageBoxY = 0;
    private final TimerHandler timerHandler;
    private final SurfaceScrollDetector scrollDetector;
    private boolean shouldDisplay;

    public ChatScene(final GameActivity activity) throws IOException {
        super(activity);

        // Chat message box container.
        final float chatContainerWidth = simulatedWidth;
        final float chatContainerHeight = 10;
        chatContainerInitY = TextureEnum.CHAT_INPUT_BG.getHeight() + chatContainerHeight * 0.5f + 10;
        chatContainer = new Rectangle(cameraCenterX, chatContainerInitY, chatContainerWidth, chatContainerHeight, vbom);
        chatContainer.setColor(Color.TRANSPARENT);
        this.attachChild(chatContainer);
        // chat scroll touch area
        scrollDetector = new SurfaceScrollDetector(this);
        final float chatScrollAreaWidth = 850;
        final float chatScrollAreaHeight = simulatedHeight - TextureEnum.CHAT_INPUT_BG.getHeight() - 10;
        chatScrollArea = new Rectangle(chatScrollAreaWidth * 0.5f, simulatedHeight - chatScrollAreaHeight * 0.5f, chatScrollAreaWidth, chatScrollAreaHeight,
                vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                scrollDetector.onTouchEvent(pSceneTouchEvent);
                return true;
            }
        };
        chatScrollArea.setColor(Color.TRANSPARENT);
        this.attachChild(chatScrollArea);
        this.registerTouchArea(chatScrollArea);
        // Right-top close button.
        closeButton = createCloseButton(TextureEnum.CHAT_INPUT_CLOSE, this.simulatedRightX - 50, this.cameraHeight - 50);
        final IEntity closeTouchArea = new Rectangle(this.simulatedRightX - 75, this.cameraHeight - 75, 150, 150, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    goBack();
                    return true;
                }
                return false;

            }
        };
        closeTouchArea.setAlpha(0);
        closeButton.setUserData(closeTouchArea);
        this.attachChild(closeButton);
        this.attachChild(closeTouchArea);
        this.registerTouchArea(closeTouchArea);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        init();
        timerHandler = new TimerHandler(0.5f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                if (shouldDisplay) {
                    displayChat(true);
                    pTimerHandler.reset();
                }

            }
        });
        activity.getEngine().registerUpdateHandler(timerHandler);
    }

    private void goBack() {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        back();
        activity.getGameHub().needSmallChatRoom(true);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editText.setVisibility(View.INVISIBLE);
                closeButton.toggle();

            }

        });
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, simulatedWidth, simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.5f);
        final Background background = new EntityBackground(bgEntity);
        this.setBackground(background);

        final Sprite chatInputSprite = createALBImageSprite(TextureEnum.CHAT_INPUT_BG, this.simulatedLeftX, 0);
        this.attachChild(chatInputSprite);

        final IEntity leftCloseTouchArea = new Rectangle(this.simulatedLeftX, TextureEnum.CHAT_INPUT_BG.getHeight() * 0.5f, 180,
                TextureEnum.CHAT_INPUT_BG.getHeight(), vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    goBack();
                    return true;
                }
                return false;

            }
        };
        leftCloseTouchArea.setAlpha(0);
        this.attachChild(leftCloseTouchArea);
        this.registerTouchArea(leftCloseTouchArea);

        final TextureEnum sendTextureEnum = TextureEnum.CHAT_INPUT_SEND;
        final F2ButtonSprite sendSprite = createALBF2ButtonSprite(sendTextureEnum, sendTextureEnum, chatInputSprite.getWidth() - sendTextureEnum.getWidth()
                - 20, 10);
        chatInputSprite.attachChild(sendSprite);
        this.registerTouchArea(sendSprite);
        sendSprite.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final Editable inputEditable = editText.getText();
                final String inputText = inputEditable.toString();
                if (inputText != null && !inputText.equals("")) {
                    ChatUtils.send(inputText);
                    chatContainer.setY(chatContainerInitY - allMessageBoxY);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inputEditable.clear();
                        }
                    });

                }
            }

        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeButton.initChatTextY();
            }

        });
        // scheduleDisplayChat();
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        shouldDisplay = true;
        timerHandler.reset();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeButton.toggle();
                final int diff = ChatUtils.displayedMiniMsg - ChatUtils.displayedFullMsg;
                if (chatContainer.getChildCount() > 30 && diff > 15) {
                    chatContainer.detachChildren();
                    allMessageBoxY = 0;
                    chatContainer.setY(chatContainerInitY);
                }
                for (int i = 0; i < diff; i++) {
                    displayChat(false);
                }
                activity.getChatText().setVisibility(View.VISIBLE);
            }

        });
    }

    private void displayChat(final boolean isDelay) {
        final ChatMessage chatMessage = ChatUtils.getDisplayMessage(DisplayChannel.FullChatRoom);
        if (chatMessage != null) {
            createChatMessageBox(chatMessage, isDelay);
        }
    }

    private void createChatMessageBox(final ChatMessage chatMessage, final boolean isDelay) {
        final int childCount = chatContainer.getChildCount();
        // Debug.e("Child:" + childCount);

        if (childCount == 75) {
            activity.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    final List<IEntity> children = new ArrayList<IEntity>(25);
                    for (int i = 0; i < 25; i++) {
                        final IEntity child = chatContainer.getChildByIndex(i);
                        children.add(child);
                    }
                    for (final IEntity child : children) {
                        chatContainer.detachChild(child);
                    }
                }
            });
        }

        final String name = chatMessage.getSender();
        final Font nameFont = ResourceManager.getInstance().newFont(FontEnum.Bold, 28, 128);
        final Text nameText = new Text(0, 0, nameFont, name, vbom);
        nameText.setColor(0XFFCF9030);
        final float nameWidth = nameText.getWidth();
        final float nameHeight = nameText.getHeight();

        final Font font = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final String time = chatMessage.getDate();
        final Text timeText = new Text(0, 0, font, time, vbom);
        timeText.setColor(0XFF808080);
        final float timeWidth = timeText.getWidth();

        final String content = chatMessage.getContent();
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 800);
        final Text contentText = new Text(0, 0, font, content, textOptions, vbom);
        final float contentWidth = contentText.getWidth();
        final float contentHeight = contentText.getHeight();

        final float messageBoxGap = 10;
        final float messageBoxWidth = 850;
        final float messageBoxHeight = nameHeight + contentHeight + 5;
        final IEntity messageBox = new Rectangle(messageBoxWidth * 0.5f + 15, allMessageBoxY - 0.5f * messageBoxHeight - messageBoxGap, messageBoxWidth,
                messageBoxHeight, vbom);
        messageBox.setColor(0XFF34251F);
        chatContainer.attachChild(messageBox);
        // Attache texts to messageBox.

        nameText.setPosition(nameWidth * 0.5f, messageBoxHeight - nameHeight * 0.5f);
        messageBox.attachChild(nameText);
        timeText.setPosition(messageBoxWidth - timeWidth * 0.5f - 10, messageBoxHeight - nameHeight * 0.5f);
        messageBox.attachChild(timeText);
        contentText.setPosition(contentWidth * 0.5f, contentHeight * 0.5f);
        messageBox.attachChild(contentText);

        final float x = chatContainer.getX();
        final float y = chatContainer.getY();
        final float toY = y + messageBoxHeight + messageBoxGap;
        if (isDelay) {
            final IEntityModifier moveModifier = new MoveModifier(0.1f, x, y, x, toY);
            chatContainer.registerEntityModifier(moveModifier);
        } else {
            chatContainer.setY(toY);
        }
        allMessageBoxY -= messageBoxHeight + messageBoxGap;
    }

    private ChatBoxCloseButton createCloseButton(final TextureEnum normalTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final float width = normalTextureEnum.getWidth();
        final float height = normalTextureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final ChatBoxCloseButton sprite = new ChatBoxCloseButton(pX, pY, normalTexture, activity);
        return sprite;
    }

    @Override
    public void leaveScene() {
        shouldDisplay = false;
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        handleScroll(pScollDetector, pPointerID, pDistanceX, pDistanceY);
    }

    private void handleScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX, final float pDistanceY) {
        final float checkY = chatContainerInitY - allMessageBoxY;
        final float toY = chatContainer.getY() - pDistanceY;
        float limitY = 0;
        if (chatContainer.getChildCount() > 50) {
            final IEntity child25th = chatContainer.getChildByIndex(24);
            limitY = child25th.getY() - child25th.getHeight() * 0.5f;
        } else if (chatContainer.getChildCount() > 0) {
            final IEntity fistChild = chatContainer.getChildByIndex(0);
            limitY = fistChild.getY() - fistChild.getHeight() * 0.5f;
        }
        if (toY > checkY) {
            chatContainer.setY(checkY);
        } else if (toY < chatContainerInitY - limitY) {
            chatContainer.setY(chatContainerInitY - limitY);
        } else {
            chatContainer.setY(toY);
        }
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

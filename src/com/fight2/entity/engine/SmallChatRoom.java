package com.fight2.entity.engine;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Color;
import android.view.View;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.ChatMessage;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ChatTextHandler;
import com.fight2.util.ChatUtils;
import com.fight2.util.ChatUtils.DisplayChannel;
import com.fight2.util.EntryFactory;
import com.fight2.util.ResourceManager;

public class SmallChatRoom extends Rectangle {
    private boolean isNeeded = false;
    private final VertexBufferObjectManager vbom;
    private final F2ButtonSprite openButton;
    private final Text chatText;
    private final Text chatTimeText;
    private final ChatTextHandler chatTextHandler;
    private final static int CHAT_SIZE = 150;
    private final static String SAMPLE_CHAT_STRING = "\n";
    private final static String SAMPLE_CHAT_TIME_STRING = "\n";
    private final static String TEST_CHAT_TIME_STRING = "16:30\n16:30";
    private final StringBuffer chatStringBuffer = new StringBuffer(SAMPLE_CHAT_STRING);
    private final StringBuffer chatTimeString = new StringBuffer(SAMPLE_CHAT_TIME_STRING);
    private final TimerHandler timerHandler;

    public SmallChatRoom(final float pX, final float pY, final float pWidth, final float pHeight, final GameActivity activity) {
        super(pX, pY, pWidth, pHeight, activity.getVertexBufferObjectManager());
        this.vbom = activity.getVertexBufferObjectManager();
        this.setColor(Color.BLACK);
        this.setAlpha(0.3f);

        final Font chatFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        chatText = new Text(0, 0, chatFont, SAMPLE_CHAT_STRING, CHAT_SIZE, vbom);
        chatText.setColor(0XFFE8BD80);
        final Font chatTimeFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        chatTimeText = new Text(0, 0, chatTimeFont, SAMPLE_CHAT_TIME_STRING, CHAT_SIZE, vbom);
        chatTimeText.setColor(0XFFE8BD80);

        chatTextHandler = new ChatTextHandler(CHAT_SIZE, vbom);

        final EntryFactory entryFactory = EntryFactory.getInstance();
        openButton = entryFactory.createALBF2ButtonSprite(TextureEnum.CHAT_INPUT_OPEN, TextureEnum.CHAT_INPUT_OPEN_FCS, 0, 0);
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
        this.attachChild(openButton);
        adjustChatTextPosition();
        this.attachChild(chatText);
        final Font testChatFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text testChatTimeText = new Text(0, 0, testChatFont, TEST_CHAT_TIME_STRING, CHAT_SIZE, vbom);
        final float chatTimeTextWidth = testChatTimeText.getWidth();
        final float chatTimeTextHeight = testChatTimeText.getHeight();

        chatTimeText.setPosition(this.getWidth() - chatTimeTextWidth * 0.5f - 10, chatTimeTextHeight * 0.5f + 5);
        this.attachChild(chatTimeText);
        timerHandler = new TimerHandler(0.25f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                if (isNeeded) {
                    final ChatMessage chatMessage = ChatUtils.getDisplayMessage(DisplayChannel.MiniChatRoom);
                    if (chatMessage != null) {
                        // final long start = System.currentTimeMillis();
                        final int deleteIndex = chatStringBuffer.indexOf("\n");
                        chatStringBuffer.delete(0, deleteIndex + 1);
                        // final long end1 = System.currentTimeMillis();
                        // final long rs1 = end1 - start;
                        // Debug.e("end1:" + rs1);
                        final String sender = chatMessage.getSender();
                        final String content = chatMessage.getContent();
                        final Font chatFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28, 512);
                        chatStringBuffer.append(chatTextHandler.handle(sender, content, chatFont));
                        // final long end2 = System.currentTimeMillis();
                        // final long rs2 = end2 - end1;
                        // Debug.e("end2:" + rs2);

                        final String date = chatMessage.getDate();
                        final int deleteChatTimeIndex = chatTimeString.indexOf("\n");
                        // final long end3 = System.currentTimeMillis();
                        // final long rs3 = end3 - end2;
                        // Debug.e("end3:" + rs3);
                        chatTimeString.delete(0, deleteChatTimeIndex + 1);
                        chatTimeString.append("\n");
                        chatTimeString.append(date);
                        // final long end4 = System.currentTimeMillis();
                        // final long rs4 = end4 - end3;
                        // Debug.e("end4:" + rs4);
                        chatTimeText.setText(chatTimeString);
                        // final long end5 = System.currentTimeMillis();
                        // final long rs5 = end5 - end4;
                        // Debug.e("end5:" + rs5);

                        final String chatString = chatStringBuffer.toString();
                        chatText.setFont(chatFont);
                        chatText.setText(chatString);
                        adjustChatTextPosition();
                        // final long end6 = System.currentTimeMillis();
                        // final long rs6 = end6 - end5;
                        // Debug.e("end6:" + rs6);
                        // activity.runOnUpdateThread(new Runnable() {
                        // @Override
                        // public void run() {
                        // chatText.detachSelf();
                        // chatText = new Text(0, 0, chatFont, chatString, vbom);
                        // chatText.setColor(0XFFE8BD80);
                        // attachChild(chatText);
                        // adjustChatTextPosition();
                        //
                        // }
                        // });
                    }
                    pTimerHandler.reset();
                }
            }
        });
        activity.getEngine().registerUpdateHandler(timerHandler);
    }

    public F2ButtonSprite getOpenButton() {
        return openButton;
    }

    private void adjustChatTextPosition() {
        final float openButtonWidth = TextureEnum.CHAT_INPUT_OPEN.getWidth();
        final float chatTextWidth = chatText.getWidth();
        final float chatTextHeight = chatText.getHeight();
        chatText.setPosition(openButtonWidth + chatTextWidth * 0.5f + 5, chatTextHeight * 0.5f + 5);
    }

    public boolean isNeeded() {
        return isNeeded;
    }

    public void setNeeded(final boolean isNeeded) {
        this.isNeeded = isNeeded;
        if (isNeeded) {
            timerHandler.reset();
        }

    }
}

package com.fight2.entity.engine;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.util.ConfigHelper;

public class ChatBoxCloseButton extends F2ButtonSprite {
    private final GameActivity activity;
    private final float initY;
    final int screenLocation[] = new int[2];
    private int chatTextInitY;
    private boolean isReposition = false;
    private int chatTextY;
    private int equalsTimes = 0;

    public ChatBoxCloseButton(final float pX, final float pY, final ITextureRegion pTextureRegion, final GameActivity activity) {
        super(pX, pY, pTextureRegion, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.initY = pY;
        init();
    }

    private void init() {
        this.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(final float pSecondsElapsed) {
                if (isReposition) {
                    activity.getChatText().getLocationInWindow(screenLocation);
                    final int chatTextCurrentY = screenLocation[1];
                    if (chatTextCurrentY == chatTextY) {
                        equalsTimes++;
                    } else {
                        equalsTimes = 0;
                    }
                    chatTextY = chatTextCurrentY;
                    if (equalsTimes == 10) {
                        final int diffPx = chatTextInitY - chatTextCurrentY;
                        final int simulatedDiffPx = ConfigHelper.getInstance().getSimulatedPxByRealPx(diffPx);
                        final float toX = getX();
                        final float toY = initY - simulatedDiffPx;
                        final IEntityModifier moveModifier = new MoveModifier(0.1f, getX(), getY(), toX, toY);
                        registerEntityModifier(moveModifier);
                        adjustTouchArea(toX, toY);
                    }
                } else {
                    if (getY() != initY) {
                        setY(initY);
                        adjustTouchArea(getX(), initY);
                    }
                }

            }

            @Override
            public void reset() {

            }

        });
    }

    private void adjustTouchArea(final float x, final float y) {
        final IEntity touchArea = (IEntity) ChatBoxCloseButton.this.getUserData();
        touchArea.setPosition(x, y);
    }

    public void initChatTextY() {
        final int screenLocation[] = new int[2];
        activity.getChatText().getLocationInWindow(screenLocation);
        chatTextInitY = screenLocation[1];
    }

    public void toggle() {
        isReposition = !isReposition;
    }
}

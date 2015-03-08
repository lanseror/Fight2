package com.fight2.entity.engine;

import org.andengine.engine.camera.hud.HUD;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.util.ConfigHelper;

public class GameHud extends HUD {
    private final float simulatedLeftX;

    private final SmallChatRoom smallChatRoom;

    public GameHud(final GameActivity activity) {
        super.setCamera(activity.getEngine().getCamera());
        final ConfigHelper configHelper = ConfigHelper.getInstance();
        this.simulatedLeftX = configHelper.getFloat(ConfigEnum.SimulatedLeftX);

        this.smallChatRoom = new SmallChatRoom(this.simulatedLeftX + 900 * 0.5f, 75 * 0.5f, 900, 75, activity);
        this.attachChild(smallChatRoom);

    }

    public void needSmallChatRoom(final boolean needed) {
        if (smallChatRoom.isNeeded() == needed) {
            return;
        }

        if (needed) {
            smallChatRoom.setVisible(true);
            this.registerTouchArea(smallChatRoom.getOpenButton());
        } else {
            smallChatRoom.setVisible(false);
            this.unregisterTouchArea(smallChatRoom.getOpenButton());
        }
        smallChatRoom.setNeeded(needed);
    }

    public void setSmallChatRoomEnabled(final boolean isEnabled) {
        smallChatRoom.setEnabled(isEnabled);
    }

    public SmallChatRoom getSmallChatRoom() {
        return smallChatRoom;
    }

}

package com.fight2.entity;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.util.ConfigHelper;

public class GameHud extends HUD {
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final float cameraCenterX;
    private final float cameraCenterY;
    private final int cameraWidth;
    private final int cameraHeight;
    private final int deviceWidth;
    private final int deviceHeight;
    private final int simulatedWidth;
    private final int simulatedHeight;
    private final float simulatedLeftX;
    private final float simulatedRightX;

    private final SmallChatRoom smallChatRoom;

    public GameHud(final GameActivity activity) {
        super.setCamera(activity.getEngine().getCamera());
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        final ConfigHelper configHelper = ConfigHelper.getInstance();
        this.cameraCenterX = configHelper.getFloat(ConfigEnum.CameraCenterX);
        this.cameraCenterY = configHelper.getFloat(ConfigEnum.CameraCenterY);
        this.cameraWidth = configHelper.getInt(ConfigEnum.CameraWidth);
        this.cameraHeight = configHelper.getInt(ConfigEnum.CameraHeight);
        this.deviceWidth = configHelper.getInt(ConfigEnum.DeviceWidth);
        this.deviceHeight = configHelper.getInt(ConfigEnum.DeviceHeight);
        this.simulatedWidth = configHelper.getInt(ConfigEnum.SimulatedWidth);
        this.simulatedHeight = configHelper.getInt(ConfigEnum.SimulatedHeight);
        this.simulatedLeftX = configHelper.getFloat(ConfigEnum.SimulatedLeftX);
        this.simulatedRightX = configHelper.getFloat(ConfigEnum.SimulatedRightX);

        this.smallChatRoom = new SmallChatRoom(this.simulatedLeftX + 900 * 0.5f, 75 * 0.5f, 900, 75, activity);
        this.attachChild(smallChatRoom);

    }

    public void needSmallChatRoom(final boolean needed) {
        if (needed) {
            smallChatRoom.setVisible(true);
            this.registerTouchArea(smallChatRoom.getOpenButton());
        } else {
            smallChatRoom.setVisible(false);
            this.unregisterTouchArea(smallChatRoom.getOpenButton());
        }
        smallChatRoom.setNeeded(needed);
    }

}

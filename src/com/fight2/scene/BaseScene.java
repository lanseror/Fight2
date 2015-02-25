package com.fight2.scene;

import java.io.IOException;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.engine.F2CommonButton;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.ConfigHelper;
import com.fight2.util.EntityUtils;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.TextureFactory;

public abstract class BaseScene extends Scene {
    protected final GameActivity activity;
    protected final float cameraCenterX;
    protected final float cameraCenterY;
    protected final int cameraWidth;
    protected final int cameraHeight;
    protected final int deviceWidth;
    protected final int deviceHeight;
    protected final int simulatedWidth;
    protected final int simulatedHeight;
    protected final float simulatedLeftX;
    protected final float simulatedRightX;
    private boolean isStarted = false;
    protected VertexBufferObjectManager vbom;
    protected GameUserSession session = GameUserSession.getInstance();

    public BaseScene(final GameActivity activity) throws IOException {
        super();
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
        this.registerUpdateHandler(new AnimationWorkarroundHandler(this));
    }

    private static class AnimationWorkarroundHandler implements IUpdateHandler {
        private final BaseScene baseScene;
        private int updates = 0;

        public AnimationWorkarroundHandler(final BaseScene baseScene) {
            this.baseScene = baseScene;
        }

        @Override
        public void reset() {
        }

        @Override
        public void onUpdate(final float pSecondsElapsed) {
            ++updates;
            if (updates > 10) {
                baseScene.unregisterUpdateHandler(this);
                baseScene.playAnimation();
            }
        }
    }

    protected void playAnimation() {
    }

    public boolean sceneBack() {
        return true;
    }

    protected abstract void init() throws IOException;

    public abstract void updateScene();

    public abstract void leaveScene();

    protected ITextureRegion createCardTexture(final String imageUrl) throws IOException {
        final ITexture texture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), imageUrl);
        final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
        texture.load();
        return textureRegion;
    }

    public F2ButtonSprite createALBF2ButtonSprite(final TextureEnum normalTextureEnum, final TextureEnum pressedTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final ITextureRegion pressedTexture = textureFactory.getAssetTextureRegion(pressedTextureEnum);
        final float width = normalTextureEnum.getWidth();
        final float height = normalTextureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final F2ButtonSprite sprite = new F2ButtonSprite(pX, pY, normalTexture, pressedTexture, vbom);
        return sprite;
    }

    public F2ButtonSprite createACF2ButtonSprite(final TextureEnum normalTextureEnum, final TextureEnum pressedTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final ITextureRegion pressedTexture = textureFactory.getAssetTextureRegion(pressedTextureEnum);
        final F2ButtonSprite sprite = new F2ButtonSprite(x, y, normalTexture, pressedTexture, vbom);
        return sprite;
    }

    public F2CommonButton createALBF2CommonButton(final float x, final float y, final String text) {
        final float width = TextureEnum.COMMON_BUTTON.getWidth();
        final float height = TextureEnum.COMMON_BUTTON.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final F2CommonButton sprite = new F2CommonButton(pX, pY, text, vbom);
        return sprite;
    }

    public F2CommonButton createACF2CommonButton(final float x, final float y, final String text) {
        final F2CommonButton sprite = new F2CommonButton(x, y, text, vbom);
        return sprite;
    }

    /**
     * Anchor left bottom sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createALBImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    /**
     * Anchor left bottom sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createALBImageSprite(final TextureEnum textureEnum, final float x, final float y, final F2OnClickListener onClickListener) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    onClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    /**
     * Anchor center sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createACImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(x, y, width, height, texture, vbom);
        return sprite;
    }

    public GameActivity getActivity() {
        return activity;
    }

    public VertexBufferObjectManager getVbom() {
        return vbom;
    }

    protected void onStarted() {
    }

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        if (!isStarted) {
            isStarted = true;
            onStarted();
        }
        super.onManagedUpdate(pSecondsElapsed);
    }

    public void alert(final String message) {
        activity.getGameHub().setSmallChatRoomEnabled(false);
        try {
            final Scene alertScene = new AlertScene(activity, message);
            final Scene scene = activity.getEngine().getScene();
            Scene childScene = scene;
            while (childScene.getChildScene() != null) {
                childScene = childScene.getChildScene();
            }
            if (childScene instanceof LoadingScene) {
                childScene.back();
                childScene = scene;
                while (childScene.getChildScene() != null) {
                    childScene = childScene.getChildScene();
                }
            }
            childScene.setChildScene(alertScene, false, false, true);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void exeAsyncTask(final IAsyncCallback callback) {
        try {
            final LoadingScene loadingScene = new LoadingScene(activity);
            final Scene scene = activity.getEngine().getScene();
            Scene childScene = scene;
            while (childScene.getChildScene() != null) {
                childScene = childScene.getChildScene();
            }
            childScene.setChildScene(loadingScene, false, false, true);

            final IAsyncCallback task = new IAsyncCallback() {

                @Override
                public void workToDo() {
                    callback.workToDo();
                }

                @Override
                public void onComplete() {
                    loadingScene.back();
                    callback.onComplete();
                }
            };
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AsyncTaskLoader().execute(task);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void confirm(final String message) {
        try {
            final Scene alertScene = new AlertScene(activity, message);
            final Scene scene = activity.getEngine().getScene();
            Scene childScene = scene;
            while (childScene.getChildScene() != null) {
                childScene = childScene.getChildScene();
            }
            if (childScene instanceof LoadingScene) {
                childScene.back();
                childScene = scene;
                while (childScene.getChildScene() != null) {
                    childScene = childScene.getChildScene();
                }
            }
            childScene.setChildScene(alertScene, false, false, true);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void topAlignEntity(final IEntity entity, final float y) {
        EntityUtils.topAlignEntity(entity, y);
    }

    public void bottomAlignEntity(final IEntity entity, final float y) {
        EntityUtils.bottomAlignEntity(entity, y);
    }

    public void leftAlignEntity(final IEntity entity, final float x) {
        EntityUtils.leftAlignEntity(entity, x);
    }

    public void rightAlignEntity(final IEntity entity, final float x) {
        EntityUtils.rightAlignEntity(entity, x);
    }
}

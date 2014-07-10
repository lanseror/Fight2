package com.fight2;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.CropResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.res.AssetManager;

import com.fight2.scene.MainScene;
import com.fight2.util.TextureFactory;

public class GameActivity extends BaseGameActivity {
    private static final int CAMERA_WIDTH = 1136;
    private static final int CAMERA_HEIGHT = 640;
    private static final float CAMERA_CENTER_X = CAMERA_WIDTH * 0.5f;
    private static final float CAMERA_CENTER_Y = CAMERA_HEIGHT * 0.5f;

    private Camera camera;

    private ITexture splashTexture;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;
    private Scene splashScene;
    private Scene mainScene;

    private ProgressBar progressBar;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new CropResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        final TextureManager textureManager = this.getTextureManager();
        final AssetManager assetManager = this.getAssets();
        this.splashTexture = new AssetBitmapTexture(textureManager, assetManager, "images/splashScreen.jpg");
        this.splashTextureRegion = TextureRegionFactory.extractFromTexture(this.splashTexture);
        this.splashTexture.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final VertexBufferObjectManager vbom = this.getVertexBufferObjectManager();
        initProgressBar(vbom);
        initSplashScene(vbom);
        loadAdditionResources();

        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
    }

    /**
     * Initialize progress bar.
     * 
     * @param vbom
     */
    private void initProgressBar(final VertexBufferObjectManager vbom) {
        progressBar = new ProgressBar(camera, CAMERA_CENTER_X, 48, 823, 15, vbom);
        progressBar.setFrameColor(0, 0, 0, 0.8f);
        progressBar.setProgressColor(1, 1, 0, 1);
        progressBar.setBackColor(0, 0, 0, 1);
        this.camera.setHUD(progressBar);
    }

    /**
     * Initialize the startup screen.
     * 
     * @param vbom
     */
    private void initSplashScene(final VertexBufferObjectManager vbom) {
        splashScene = new Scene();
        splash = new Sprite(CAMERA_CENTER_X, CAMERA_CENTER_Y, CAMERA_WIDTH, CAMERA_HEIGHT, splashTextureRegion, vbom) {
            @Override
            protected void preDraw(final GLState pGLState, final Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        splashScene.attachChild(splash);
    }

    /**
     * Load other resources, include remote resources.
     */
    private void loadAdditionResources() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadResources1();
                loadScenes();
                splashScene.detachChildren();
                splashScene.detachSelf();
                mEngine.setScene(mainScene);
                camera.setHUD(null);
            }
        }).start();
    }

    @Override
    public void onPopulateScene(final Scene pScene, final OnPopulateSceneCallback pOnPopulateSceneCallback) {
        mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);

            }
        }));

        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    protected void loadScenes() {
        mainScene = new MainScene(this, this.getVertexBufferObjectManager(), CAMERA_WIDTH, CAMERA_HEIGHT);
    }

    private void loadResources1() {
        try {
            TextureFactory.getInstance().loadResource(getTextureManager(), getAssets());
             for (int i = 0; i < 1000; i++) {
             progressBar.setProgress(i * 0.1f);
             Thread.sleep(10);
             }
             } catch (final InterruptedException e) {
             e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}

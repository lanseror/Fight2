package com.fight2;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.AudioOptions;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.CropResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.LayoutGameActivity;

import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.fight2.constant.ConfigEnum;
import com.fight2.constant.MusicEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.entity.engine.GameHud;
import com.fight2.entity.engine.ProgressBar;
import com.fight2.util.AccountUtils;
import com.fight2.util.ConfigHelper;
import com.fight2.util.EntityFactory;
import com.fight2.util.F2MusicManager;
import com.fight2.util.F2SoundManager;
import com.fight2.util.ImageOpenHelper;
import com.fight2.util.LogUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class GameActivity extends LayoutGameActivity {
    public static final int CAMERA_WIDTH = 1136;
    public static final int CAMERA_HEIGHT = 640;
    public static final float CAMERA_CENTER_X = CAMERA_WIDTH * 0.5f;
    public static final float CAMERA_CENTER_Y = CAMERA_HEIGHT * 0.5f;

    private Camera camera;

    private ITexture splashTexture;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;

    private Scene splashScene;
    private ProgressBar progressBar;
    private ImageOpenHelper dbHelper;
    private EditText chatText;

    @Override
    protected int getLayoutID() {
        return R.layout.game;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.game_rendersurfaceview;
    }

    @Override
    protected void onSetContentView() {
        super.onSetContentView();
        chatText = (EditText) this.findViewById(R.id.chat_text);
        chatText.setVisibility(View.INVISIBLE);
    }

    @Override
    public Engine onCreateEngine(final EngineOptions pEngineOptions) {
        dbHelper = new ImageOpenHelper(this);
        return super.onCreateEngine(pEngineOptions);
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final ConfigHelper configHelper = ConfigHelper.getInstance();
        final int deviceWidth = displayMetrics.widthPixels > displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels;
        final int deviceHeight = displayMetrics.widthPixels > displayMetrics.heightPixels ? displayMetrics.heightPixels : displayMetrics.widthPixels;
        configHelper.setConfig(ConfigEnum.DeviceWidth, deviceWidth);
        configHelper.setConfig(ConfigEnum.DeviceHeight, deviceHeight);
        configHelper.setConfig(ConfigEnum.CameraWidth, CAMERA_WIDTH);
        configHelper.setConfig(ConfigEnum.CameraHeight, CAMERA_HEIGHT);
        configHelper.setConfig(ConfigEnum.CameraCenterX, CAMERA_CENTER_X);
        configHelper.setConfig(ConfigEnum.CameraCenterY, CAMERA_CENTER_Y);
        configHelper.setConfig(ConfigEnum.X_DPI, displayMetrics.xdpi);
        final BigDecimal factor = BigDecimal.valueOf(CAMERA_HEIGHT).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final int simulatedWidth = BigDecimal.valueOf(deviceWidth).multiply(factor).intValue();
        configHelper.setConfig(ConfigEnum.SimulatedLeftX, CAMERA_CENTER_X - simulatedWidth * 0.5f);
        configHelper.setConfig(ConfigEnum.SimulatedRightX, CAMERA_CENTER_X + simulatedWidth * 0.5f);
        configHelper.setConfig(ConfigEnum.SimulatedWidth, simulatedWidth);
        configHelper.setConfig(ConfigEnum.SimulatedHeight, CAMERA_HEIGHT);
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new CropResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                camera);
        final AudioOptions audioOptions = engineOptions.getAudioOptions().setNeedsSound(true);
        audioOptions.getMusicOptions().setNeedsMusic(true);
        // engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_DIM);
        return engineOptions;
    }

    @Override
    public void onCreateResources(final OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
        checkInstallation();
        final TextureManager textureManager = this.getTextureManager();
        final AssetManager assetManager = this.getAssets();
        this.splashTexture = new AssetBitmapTexture(textureManager, assetManager, "images/common_splash_screen.png");
        this.splashTextureRegion = TextureRegionFactory.extractFromTexture(this.splashTexture);
        this.splashTexture.load();
        F2MusicManager.getInstance().prepare(this);
        F2SoundManager.getInstance().prepare(this);
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    private void checkInstallation() throws IOException {
        if (!AccountUtils.isInstalled(this)) {
            AccountUtils.installAndRegister(this);
        }
    }

    @Override
    public void onCreateScene(final OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        // this.mEngine.registerUpdateHandler(new FPSLogger());
        final VertexBufferObjectManager vbom = this.getVertexBufferObjectManager();
        initSplashScene(vbom);
        pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
        // F2MusicManager.getInstance().playMusic(MusicEnum.COMMON_LOADING, true);
    }

    /**
     * Initialize progress bar.
     * 
     * @param vbom
     */
    private void initProgressBar(final VertexBufferObjectManager vbom) {
        progressBar = new ProgressBar(CAMERA_CENTER_X, 58, this);
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

    /**
     * Load other resources, include remote resources.
     */
    private void loadAdditionResources() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                loadResources1();
                progressBar.setPercent(100);
                splashScene.detachChildren();
                splashScene.detachSelf();
                splashTexture.unload();
                splashTexture = null;
                final GameHud gameHud = new GameHud(GameActivity.this);
                camera.setHUD(gameHud);
                gameHud.needSmallChatRoom(false);
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
                F2SoundManager.getInstance().stop();
                F2MusicManager.getInstance().playMusic(MusicEnum.MAIN_BG);
            }

        }).start();
    }

    public GameHud getGameHub() {
        final HUD hud = camera.getHUD();
        if (hud instanceof GameHud) {
            return (GameHud) hud;
        } else {
            return null;
        }
    }

    @Override
    public synchronized void onGameCreated() {
        super.onGameCreated();
        EntityFactory.getInstance().init(this.getVertexBufferObjectManager());
        try {
            TextureFactory.getInstance().init(this.getTextureManager(), this.getAssets());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        initProgressBar(this.getVertexBufferObjectManager());
        F2SoundManager.getInstance().play(SoundEnum.LOADING, true);
        loadAdditionResources();
    }

    private void loadResources1() {
        try {
            EntityFactory.getInstance().init(this.getVertexBufferObjectManager());
            final ResourceManager resourceManager = ResourceManager.getInstance();
            resourceManager.loadGameResources(this, progressBar);

        } catch (final IOException e) {
            LogUtils.e(e);
        }

    }

    public ImageOpenHelper getDbHelper() {
        return dbHelper;
    }

    public EditText getChatText() {
        return chatText;
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        final ResourceManager resourceManager = ResourceManager.getInstance();
        if (keyCode == KeyEvent.KEYCODE_BACK && resourceManager.getCurrentSceneEnum() != SceneEnum.Main) {
            this.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                    resourceManager.sceneBack();
                }

            });
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPause() {
        TextureFactory.getInstance().clear();
        super.onPause();
    }

    @Override
    public void onGameDestroyed() {
        ResourceManager.destroy();
        super.onGameDestroyed();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(final Camera camera) {
        this.camera = camera;
    }

}

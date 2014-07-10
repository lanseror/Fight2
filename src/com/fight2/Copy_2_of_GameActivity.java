package com.fight2;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.CropResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.content.res.AssetManager;

public class Copy_2_of_GameActivity extends SimpleBaseGameActivity {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    protected static final int MENU_BACK = 1;

    private ITexture splashTexture;
    private ITextureRegion splashTextureRegion;
    private Sprite splash;

    private Scene splashScene;

    private Camera camera = null;

    private Shape spriteGroup = null;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new CropResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() throws IOException {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("images/");
        TextureManager textureManager = this.getTextureManager();
        AssetManager assetManager = this.getAssets();
        this.splashTexture = new AssetBitmapTexture(textureManager, assetManager, "images/splashScreen.jpg");
        this.splashTextureRegion = TextureRegionFactory.extractFromTexture(this.splashTexture);
        this.splashTexture.load();

    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final VertexBufferObjectManager vbom = this.getVertexBufferObjectManager();

        initSplashScene();
        return splashScene;
    }

    private void initSplashScene() {
        splashScene = new Scene();
        splash = new Sprite(0, 0, splashTextureRegion, mEngine.getVertexBufferObjectManager()) {
            @Override
            protected void preDraw(GLState pGLState, Camera pCamera) {
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
        };

        //splash.setScale(1.5f);
        splash.setPosition(CAMERA_WIDTH * 0.5f, CAMERA_HEIGHT * 0.5f);
        splashScene.attachChild(splash);
    }
}

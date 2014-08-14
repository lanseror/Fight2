package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.entity.ProgressBar;
import com.fight2.scene.ArenaScene;
import com.fight2.scene.BaseScene;
import com.fight2.scene.MainScene;
import com.fight2.scene.PartyScene;
import com.fight2.scene.SummonScene;

public class ResourceManager {
    private static ResourceManager INSTANCE = new ResourceManager();
    private boolean isResourceLoaded = false;
    private GameActivity activity;
    private TextureManager textureManager;
    private AssetManager assetManager;
    private FontManager fontManager;
    private BaseScene currentScene;

    private final Map<SceneEnum, BaseScene> scenes = new HashMap<SceneEnum, BaseScene>();

    private ResourceManager() {
        // Private the constructor;
    }

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public void loadGameResources(final GameActivity activity, final ProgressBar progressBar) throws IOException {
        this.activity = activity;
        this.textureManager = activity.getTextureManager();
        this.assetManager = activity.getAssets();
        this.fontManager = activity.getFontManager();
        FontFactory.setAssetBasePath("font/");
        // Start load resources
        TiledTextureFactory.getInstance().loadResource(textureManager, assetManager);
        final TextureFactory textureFactory = TextureFactory.getInstance();
        textureFactory.initImageData(activity);
        final String installUUID = AccountUtils.readInstallUUID(activity);
        AccountUtils.login(installUUID, activity);
        textureFactory.loadResource(textureManager, assetManager, progressBar);
        // progressBar.increase(90);

        loadScenes();
        // Resources loaded
        this.isResourceLoaded = true;
    }

    public boolean isResourceLoaded() {
        return isResourceLoaded;
    }

    public void setResourceLoaded(final boolean isResourceLoaded) {
        this.isResourceLoaded = isResourceLoaded;
    }

    public BaseScene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(final SceneEnum sceneEnum) {
        final BaseScene scene = scenes.get(sceneEnum);
        activity.getEngine().setScene(scene);
        scene.updateScene();
        this.currentScene = scene;
    }

    public Font getFont(final FontEnum fontEnum) {

        switch (fontEnum) {
            case Main:
                return getFont(fontEnum, 20);
            case Battle:
                return getFont(fontEnum, 36);
            case BoldFace:
                return getFont(fontEnum, 30);
            default:
                return getFont(fontEnum, 30);
        }

    }

    public Font getFont(final FontEnum fontEnum, final int size) {

        switch (fontEnum) {
            case Main:
                final Font mainFont = FontFactory.create(fontManager, textureManager, 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), size,
                        Color.WHITE);
                mainFont.load();
                return mainFont;
            case Battle:
                final Font battleFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 512, 512,
                        Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), size, Color.WHITE);
                battleFont.load();
                return battleFont;
            case BoldFace:
                final Font boldFace = FontFactory.createFromAsset(fontManager, textureManager, 256, 256, TextureOptions.BILINEAR, assetManager,
                        FontEnum.BoldFace.getFontUrl(), size, true, Color.WHITE);
                boldFace.load();
                return boldFace;
            default:
                final Font defaultFont = FontFactory.createFromAsset(fontManager, textureManager, 256, 256, TextureOptions.BILINEAR, assetManager,
                        FontEnum.BoldFace.getFontUrl(), size, true, Color.WHITE);
                defaultFont.load();
                return defaultFont;
        }

    }

    private void loadScenes() {
        try {
            final BaseScene mainScene = new MainScene(activity);
            scenes.put(SceneEnum.Main, mainScene);
            final BaseScene partyScene = new PartyScene(activity);
            scenes.put(SceneEnum.Party, partyScene);
            final BaseScene summonScene = new SummonScene(activity);
            scenes.put(SceneEnum.Summon, summonScene);
            final BaseScene arenaScene = new ArenaScene(activity);
            scenes.put(SceneEnum.Arena, arenaScene);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }
}

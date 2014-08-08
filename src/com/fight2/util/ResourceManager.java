package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.util.adt.color.Color;

import android.content.res.AssetManager;
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
    private BaseScene currentScene;

    private final Map<SceneEnum, BaseScene> scenes = new HashMap<SceneEnum, BaseScene>();
    private final Map<FontEnum, Font> fonts = new HashMap<FontEnum, Font>();

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
        loadFonts();
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

    private void loadFonts() {
        final Font mainFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 20, Color.WHITE_ARGB_PACKED_INT);
        mainFont.load();
        fonts.put(FontEnum.Main, mainFont);
        final Font battleFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 36, Color.WHITE_ARGB_PACKED_INT);
        battleFont.load();
        fonts.put(FontEnum.Battle, battleFont);
    }

    public Font getFont(final FontEnum fontEnum) {
        return fonts.get(fontEnum);
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

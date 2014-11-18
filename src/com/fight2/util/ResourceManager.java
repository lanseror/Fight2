package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

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
import com.fight2.entity.engine.ProgressBar;
import com.fight2.scene.ArenaListScene;
import com.fight2.scene.ArenaScene;
import com.fight2.scene.BaseScene;
import com.fight2.scene.ChatScene;
import com.fight2.scene.GuildScene;
import com.fight2.scene.MainScene;
import com.fight2.scene.PartyScene;
import com.fight2.scene.PlayerInfoScene;
import com.fight2.scene.QuestScene;
import com.fight2.scene.SummonScene;
import com.fight2.scene.UserStoreroomScene;

public class ResourceManager {
    private static ResourceManager INSTANCE = new ResourceManager();
    private boolean isResourceLoaded = false;
    private GameActivity activity;
    private TextureManager textureManager;
    private AssetManager assetManager;
    private FontManager fontManager;
    private BaseScene currentScene;
    private SceneEnum currentSceneEnum;
    private final Stack<SceneEnum> breadcrumbs = new Stack<SceneEnum>();

    private final Map<SceneEnum, BaseScene> scenes = new HashMap<SceneEnum, BaseScene>();

    private ResourceManager() {
        // Private the constructor;
    }

    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    public void loadGameResources(final GameActivity activity, final ProgressBar progressBar) throws IOException {
        LogUtils.init(activity);
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
        breadcrumbs.clear();
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

    public BaseScene getScene(final SceneEnum sceneEnum) {
        return scenes.get(sceneEnum);
    }

    public void setCurrentScene(final SceneEnum sceneEnum) {
        if (currentScene != null) {
            currentScene.leaveScene();
        }
        final BaseScene scene = scenes.get(sceneEnum);
        activity.getEngine().setScene(scene);
        scene.updateScene();
        currentScene = scene;
        currentSceneEnum = sceneEnum;
        breadcrumbs.push(currentSceneEnum);
    }

    public void sceneBack(final boolean isManaged) {
        if (currentScene != null && isManaged) {
            currentScene.leaveScene();
        }
        if (isManaged) {
            breadcrumbs.pop();
        }
        final SceneEnum sceneEnum = breadcrumbs.peek();

        final BaseScene scene = scenes.get(sceneEnum);
        activity.getEngine().setScene(scene);
        scene.updateScene();
        currentScene = scene;
        currentSceneEnum = sceneEnum;
    }

    public SceneEnum getCurrentSceneEnum() {
        return currentSceneEnum;
    }

    public Font getFont(final FontEnum fontEnum) {

        switch (fontEnum) {
            case Main:
                return getFont(fontEnum, 20);
            case Battle:
                return getFont(fontEnum, 36);
            case Default:
                return getFont(fontEnum, 30);
            case Bold:
                return getFont(fontEnum, 30);
            default:
                return getFont(fontEnum, 30);
        }

    }

    public Font getFont(final FontEnum fontEnum, final int size) {

        switch (fontEnum) {
            case Main:
                return getFont(fontEnum, size, 256);
            case Battle:
                return getFont(fontEnum, size, 512);
            case Default:
                return getFont(fontEnum, size, 256);
            case Bold:
                return getFont(fontEnum, size, 256);
        }
        return null;

    }

    public Font getFont(final FontEnum fontEnum, final int size, final int textureSize) {
        switch (fontEnum) {
            case Main:
                final Font mainFont = FontFactory.create(fontManager, textureManager, textureSize, textureSize, Typeface.DEFAULT, size, Color.WHITE);
                mainFont.load();
                return mainFont;
            case Battle:
                final Font battleFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), textureSize, textureSize, Typeface.DEFAULT,
                        size, Color.WHITE);
                battleFont.load();
                return battleFont;
            case Default:
                final Font defaultFont = FontFactory.create(fontManager, textureManager, textureSize, textureSize, TextureOptions.BILINEAR, Typeface.DEFAULT,
                        size, true, Color.WHITE);
                defaultFont.load();
                return defaultFont;
            case Bold:
                final Font boldFace = FontFactory.create(fontManager, textureManager, textureSize, textureSize, TextureOptions.BILINEAR, Typeface.DEFAULT_BOLD,
                        size, true, Color.WHITE);
                boldFace.load();
                return boldFace;
        }
        return null;

    }

    private void loadScenes() {
        try {
            final BaseScene chatScene = new ChatScene(activity);
            scenes.put(SceneEnum.Chat, chatScene);
            final BaseScene mainScene = new MainScene(activity);
            scenes.put(SceneEnum.Main, mainScene);
            final BaseScene partyScene = new PartyScene(activity);
            scenes.put(SceneEnum.Party, partyScene);
            final BaseScene summonScene = new SummonScene(activity);
            scenes.put(SceneEnum.Summon, summonScene);
            final BaseScene arenaListScene = new ArenaListScene(activity);
            scenes.put(SceneEnum.ArenaList, arenaListScene);
            final BaseScene questScene = new QuestScene(activity);
            scenes.put(SceneEnum.Quest, questScene);
            final BaseScene playerInfoScene = new PlayerInfoScene(activity);
            scenes.put(SceneEnum.PlayerInfo, playerInfoScene);
            final BaseScene guildScene = new GuildScene(activity);
            scenes.put(SceneEnum.Guild, guildScene);
            final BaseScene userStoreroomScene = new UserStoreroomScene(activity);
            scenes.put(SceneEnum.Storeroom, userStoreroomScene);

            final BaseScene arenaScene = new ArenaScene(activity);
            scenes.put(SceneEnum.Arena, arenaScene);

        } catch (final IOException e) {
            LogUtils.e(e);
        }

    }

}

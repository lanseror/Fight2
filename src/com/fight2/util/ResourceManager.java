package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseArray;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.entity.engine.ProgressBar;
import com.fight2.scene.ArenaListScene;
import com.fight2.scene.ArenaScene;
import com.fight2.scene.BaseScene;
import com.fight2.scene.CardEvolutionScene;
import com.fight2.scene.CardUpgradeScene;
import com.fight2.scene.ChatScene;
import com.fight2.scene.GuildScene;
import com.fight2.scene.LoadingScene;
import com.fight2.scene.MainScene;
import com.fight2.scene.PartyScene;
import com.fight2.scene.PlayerInfoScene;
import com.fight2.scene.QuestScene;
import com.fight2.scene.SummonScene;
import com.fight2.scene.UserStoreroomScene;

public class ResourceManager {
    private static ResourceManager INSTANCE;
    private boolean isResourceLoaded = false;
    private GameActivity activity;
    private TextureManager textureManager;
    private AssetManager assetManager;
    private FontManager fontManager;
    private BaseScene currentScene;
    private SceneEnum currentSceneEnum;
    private final Stack<SceneEnum> breadcrumbs = new Stack<SceneEnum>();

    private final Map<SceneEnum, BaseScene> scenes = new HashMap<SceneEnum, BaseScene>();

    private final Map<FontEnum, SparseArray<Font>> fontMap = new HashMap<FontEnum, SparseArray<Font>>();

    private ResourceManager() {
        // Private the constructor;
    }

    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceManager();
        }
        return INSTANCE;
    }

    public static void destroy() {
        INSTANCE = null;
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
        textureFactory.clear();
        textureFactory.initImageData(activity);
        progressBar.setPercent(10);
        final String installUUID = AccountUtils.readInstallUUID(activity);
        AccountUtils.login(installUUID, activity);
        progressBar.setPercent(30);

        for (final FontEnum fontEnum : FontEnum.values()) {
            fontMap.put(fontEnum, new SparseArray<Font>());
        }
        progressBar.setPercent(35);

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
        try {
            switch (sceneEnum) {
                case Chat:
                    return new ChatScene(activity);
                case Main:
                    return new MainScene(activity);
                case Party:
                    return new PartyScene(activity);
                case CardUpgrade:
                    return new CardUpgradeScene(activity);
                case CardEvolution:
                    return new CardEvolutionScene(activity);
                case Summon:
                    return new SummonScene(activity);
                case ArenaList:
                    return new ArenaListScene(activity);
                case PlayerInfo:
                    return new PlayerInfoScene(activity);
                case Guild:
                    return new GuildScene(activity);
                case Storeroom:
                    return new UserStoreroomScene(activity);
                case Arena:
                    return new ArenaScene(activity);
                case Quest:
                    return scenes.get(sceneEnum);
                default:
                    throw new RuntimeException("No Scene");
            }

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrentScene(final SceneEnum sceneEnum) {
        setCurrentScene(sceneEnum, false);
    }

    public void setCurrentScene(final SceneEnum sceneEnum, final boolean isBack) {
        setCurrentScene(sceneEnum, isBack, new IRCallback<BaseScene>() {
            @Override
            public BaseScene onCallback() {
                return getScene(sceneEnum);
            }
        });
    }

    public void setCurrentScene(final SceneEnum sceneEnum, final IRCallback<BaseScene> irCallback) {
        setCurrentScene(sceneEnum, false, irCallback);
    }

    public void setCurrentScene(final SceneEnum sceneEnum, final boolean isBack, final IRCallback<BaseScene> irCallback) {
        if (currentScene != null) {
            try {
                final LoadingScene loadingScene = new LoadingScene(activity);
                Scene childScene = currentScene;
                while (childScene.getChildScene() != null) {
                    childScene = childScene.getChildScene();
                }
                childScene.setChildScene(loadingScene, false, false, true);
                activity.getGameHub().setSmallChatRoomEnabled(false);
                currentScene.leaveScene();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        final IAsyncCallback callback = new IAsyncCallback() {
            private BaseScene scene;

            @Override
            public void workToDo() {
                scene = irCallback.onCallback();
                scene.updateScene();
            }

            @Override
            public void onComplete() {
                activity.getEngine().setScene(scene);
                if (currentScene != null) {
                    Scene childScene = currentScene;
                    while (childScene.getChildScene() != null) {
                        childScene = childScene.getChildScene();
                    }
                    childScene.back();
                }
                currentScene = scene;
                if (sceneEnum != null && currentSceneEnum != sceneEnum) {
                    currentSceneEnum = sceneEnum;
                    if (!isBack) {
                        breadcrumbs.push(currentSceneEnum);
                    }
                }
                activity.getGameHub().setSmallChatRoomEnabled(true);

            }

        };
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskLoader().execute(callback);
            }
        });

    }

    public void setChildScene(final BaseScene scene, final IRCallback<BaseScene> irCallback) {
        try {
            final LoadingScene loadingScene = new LoadingScene(activity);
            scene.setChildScene(loadingScene, false, false, true);
            activity.getGameHub().setSmallChatRoomEnabled(false);

            final IAsyncCallback callback = new IAsyncCallback() {
                private BaseScene childScene;

                @Override
                public void workToDo() {
                    childScene = irCallback.onCallback();
                    childScene.updateScene();
                }

                @Override
                public void onComplete() {
                    loadingScene.back();
                    scene.setChildScene(childScene, false, false, true);
                    activity.getGameHub().setSmallChatRoomEnabled(true);
                }

            };
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AsyncTaskLoader().execute(callback);
                }
            });
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sceneBack() {
        if (currentScene != null) {
            try {
                final LoadingScene loadingScene = new LoadingScene(activity);
                currentScene.setChildScene(loadingScene, false, false, true);
                activity.getGameHub().setSmallChatRoomEnabled(false);

                final IAsyncCallback callback = new IAsyncCallback() {
                    private boolean result;

                    @Override
                    public void workToDo() {
                        result = currentScene.sceneBack();
                    }

                    @Override
                    public void onComplete() {
                        loadingScene.back();
                        activity.getGameHub().setSmallChatRoomEnabled(true);
                        if (result) {
                            breadcrumbs.pop();
                            final SceneEnum sceneEnum = breadcrumbs.peek();
                            setCurrentScene(sceneEnum, true);
                        }
                    }

                };
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncTaskLoader().execute(callback);
                    }
                });
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unManagedSceneBack() {
        final SceneEnum sceneEnum = breadcrumbs.peek();
        setCurrentScene(sceneEnum);
    }

    public SceneEnum getCurrentSceneEnum() {
        return currentSceneEnum;
    }

    public Font newFont(final FontEnum fontEnum) {

        switch (fontEnum) {
            case Main:
                return newFont(fontEnum, 20);
            case Battle:
                return newFont(fontEnum, 36);
            case Default:
                return newFont(fontEnum, 30);
            case Bold:
                return newFont(fontEnum, 30);
            default:
                return newFont(fontEnum, 30);
        }

    }

    public Font newFont(final FontEnum fontEnum, final int size) {

        switch (fontEnum) {
            case Main:
                return newFont(fontEnum, size, 256);
            case Battle:
                return newFont(fontEnum, size, 512);
            case Default:
                return newFont(fontEnum, size, 256);
            case Bold:
                return newFont(fontEnum, size, 256);
        }
        return null;

    }

    public Font getFont(final FontEnum fontEnum, final int size) {

        final SparseArray<Font> fonts = fontMap.get(fontEnum);
        Font font = fonts.get(size);
        if (font == null) {
            font = newFont(fontEnum, size);
            fonts.put(size, font);
        }
        return font;

    }

    public Font newFont(final FontEnum fontEnum, final int size, final int textureSize) {
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
            final BaseScene scene = new QuestScene(activity);
            scenes.put(SceneEnum.Quest, scene);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

}

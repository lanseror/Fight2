package com.fight2;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.CropResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.batch.SpriteGroup;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseLinear;

public class CopyOfGameActivity extends SimpleBaseGameActivity implements IOnMenuItemClickListener {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    protected static final int MENU_BACK = 1;

    private ITexture bgTexture;
    private ITextureRegion bgTextureRegion;

    private ITexture card1Texture;
    private ITextureRegion card1TextureRegion;

    private ITexture backTexture;
    private ITextureRegion backTextureRegion;

    private Camera camera = null;

    private Shape spriteGroup = null;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new CropResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
    }

    @Override
    public void onCreateResources() throws IOException {
        this.bgTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "bg/bg_fight.jpg");
        this.bgTextureRegion = TextureRegionFactory.extractFromTexture(this.bgTexture);
        this.bgTexture.load();

        this.card1Texture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "card/card1.jpg");
        this.card1TextureRegion = TextureRegionFactory.extractFromTexture(this.card1Texture);
        this.card1Texture.load();

        this.backTexture = new AssetBitmapTexture(this.getTextureManager(), this.getAssets(), "button/button_back.jpg");
        this.backTextureRegion = TextureRegionFactory.extractFromTexture(this.backTexture);
        this.backTexture.load();

    }

    @Override
    public Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());
        final VertexBufferObjectManager vbom = this.getVertexBufferObjectManager();
        final Scene scene = new Scene();
        Sprite bgSprite = new Sprite(0, 0, bgTextureRegion, vbom);
        bgSprite.setOffsetCenter(0, 0);
        Background settingBackground = new SpriteBackground(bgSprite);
        scene.setBackground(settingBackground);
        scene.setChildScene(createSummonMenuScene());

        spriteGroup = new SpriteGroup(0, 0, card1Texture, 4, vbom);

        int xStart = 10;
        for (int i = 0; i < 12; i++) {
            Sprite card1Sprite = new Sprite(xStart, 400, 50, 80, card1TextureRegion, vbom);
            card1Sprite.setOffsetCenter(0, 0);
            scene.attachChild(card1Sprite);

            xStart += 50;
            if ((i + 1) % 4 == 0) {
                xStart += 50;
            }
        }
        
        xStart = 10;
        for (int i = 0; i < 4; i++) {
            Sprite card1Sprite = new Sprite(xStart, 20, 50, 80, card1TextureRegion, vbom);
            card1Sprite.setOffsetCenter(0, 0);

            xStart += 50;
            if ((i + 1) % 4 == 0) {
                xStart += 50;
            }
            spriteGroup.attachChild(card1Sprite);
        }
        scene.attachChild(spriteGroup);
        fight(spriteGroup);

        return scene;
    }

    private void fight(final Shape sprite) {
        sprite.clearEntityModifiers();

        final MoveModifier modifier2 = new MoveModifier(1, 250, 330, 0, 0, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
            }

        }, EaseLinear.getInstance());

        final MoveModifier modifier = new MoveModifier(0.08f, 0, 0, 250, 330, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                sprite.registerEntityModifier(modifier2);
            }

        }, EaseLinear.getInstance());
        
        final MoveModifier modifierBack = new MoveModifier(0.5f, 0, 0, 0, -10, new IEntityModifierListener() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
            }

            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                sprite.registerEntityModifier(modifier);
            }

        }, EaseLinear.getInstance());


        sprite.registerEntityModifier(modifierBack);

    }

    private Scene createSummonMenuScene() {
        MenuScene menuScene = new MenuScene(camera);
        menuScene.setPosition(600, 100);
        final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, 100, 100, this.backTextureRegion,
                this.getVertexBufferObjectManager()), 1.2f, 1);

        menuScene.addMenuItem(backMenuItem);

        menuScene.buildAnimations();
        menuScene.setBackgroundEnabled(false);
        backMenuItem.setPosition(100, 100);
        menuScene.setOnMenuItemClickListener(this);
        return menuScene;
    }

    @Override
    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
        int pid = pMenuItem.getID();
        switch (pid) {
        case MENU_BACK:
            fight(spriteGroup);
            return true;
        default:
            return false;
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

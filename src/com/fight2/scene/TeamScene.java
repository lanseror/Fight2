package com.fight2.scene;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.ConfigHelper;
import com.fight2.util.TextureFactory;

public class TeamScene extends Scene {
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final float cameraCenterX;
    private final float cameraCenterY;

    public TeamScene(final GameActivity activity, final VertexBufferObjectManager vbom) {
        super();
        this.activity = activity;
        this.vbom = vbom;
        final ConfigHelper configHelper = ConfigHelper.getInstance();
        this.cameraCenterX = configHelper.getFloat(ConfigEnum.CameraCenterX);
        this.cameraCenterY = configHelper.getFloat(ConfigEnum.CameraCenterY);
        init();
    }

    private void init() {
        final Sprite bgSprite = createImageSprite2(TextureEnum.TEAM_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite frameSprite = createImageSprite(TextureEnum.TEAM_frame, 75, 110);
        this.attachChild(frameSprite);

        final Sprite gridSprite = createImageSprite(TextureEnum.TEAM_FRAME_GRID, 215, 347);
        this.attachChild(gridSprite);

        final Sprite organizeSprite = createImageSprite(TextureEnum.TEAM_BUTTON_ORGANIZE, 680, 370);
        this.attachChild(organizeSprite);
    }

    private Sprite createImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    private Sprite createImageSprite2(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(cameraCenterX, cameraCenterY, width, height, texture, vbom);
        return sprite;
    }
}

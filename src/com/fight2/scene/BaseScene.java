package com.fight2.scene;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.ConfigEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.util.ConfigHelper;
import com.fight2.util.TextureFactory;

public abstract class BaseScene extends Scene {
    protected final GameActivity activity;
    protected final VertexBufferObjectManager vbom;
    protected final float cameraCenterX;
    protected final float cameraCenterY;
    protected final int cameraWidth;
    protected final int cameraHeight;
    protected final int deviceWidth;
    protected final int deviceHeight;

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
        init();
    }

    protected abstract void init() throws IOException;

    protected ITextureRegion createCardTexture(final String imageUrl) throws IOException {
        final ITexture texture = new AssetBitmapTexture(activity.getTextureManager(), activity.getAssets(), imageUrl);
        final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
        texture.load();
        return textureRegion;
    }

    protected Sprite createRealScreenImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final BigDecimal factor = BigDecimal.valueOf(this.cameraHeight).divide(BigDecimal.valueOf(deviceHeight), 2, RoundingMode.HALF_DOWN);
        final float fakeWidth = BigDecimal.valueOf(this.deviceWidth).multiply(factor).floatValue();
        final float pX = (this.cameraWidth - fakeWidth) / 2 + x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    protected Sprite createCameraImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getIextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }
}

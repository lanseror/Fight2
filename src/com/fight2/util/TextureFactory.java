package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.res.AssetManager;

import com.fight2.constant.TextureEnum;

public class TextureFactory {
    private static TextureFactory INSTANCE = new TextureFactory();
    private final Map<TextureEnum, ITextureRegion> datas = new HashMap<TextureEnum, ITextureRegion>();

    private TextureFactory() {
        // Private the constructor;
    }

    public static TextureFactory getInstance() {
        return INSTANCE;
    }

    public void loadResource(final TextureManager textureManager, final AssetManager assetManager) throws IOException {
        for (final TextureEnum textureEnum : TextureEnum.values()) {
            final ITexture texture = new AssetBitmapTexture(textureManager, assetManager, textureEnum.getUrl());
            final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
            texture.load();
            datas.put(textureEnum, textureRegion);
        }
    }

    public ITextureRegion getIextureRegion(final TextureEnum textureEnum) {
        return this.datas.get(textureEnum);
    }

}

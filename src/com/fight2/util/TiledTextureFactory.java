package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;

import android.content.res.AssetManager;

import com.fight2.constant.TiledTextureEnum;

public class TiledTextureFactory {
    private static TiledTextureFactory INSTANCE = new TiledTextureFactory();
    private final Map<TiledTextureEnum, ITiledTextureRegion> datas = new HashMap<TiledTextureEnum, ITiledTextureRegion>();
    private TextureManager textureManager;
    private AssetManager assetManager;

    private TiledTextureFactory() {
        // Private the constructor;
    }

    public static TiledTextureFactory getInstance() {
        return INSTANCE;
    }

    public void loadResource(final TextureManager textureManager, final AssetManager assetManager) throws IOException {
        this.textureManager = textureManager;
        this.assetManager = assetManager;
    }

    public ITiledTextureRegion getIextureRegion(final TiledTextureEnum textureEnum) {
        try {
            final ITexture texture = new AssetBitmapTexture(textureManager, assetManager, textureEnum.getUrl());
            final ITiledTextureRegion tiledTextureRegion = TextureRegionFactory.extractTiledFromTexture(texture, textureEnum.getTileColumns(),
                    textureEnum.getTileRows());
            texture.load();
            datas.put(textureEnum, tiledTextureRegion);
            return tiledTextureRegion;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}

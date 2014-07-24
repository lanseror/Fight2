package com.fight2.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;

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

    public ITextureRegion createIextureRegion(final TextureManager textureManager, final AssetManager assetManager, final String url) throws IOException {
        if (url.startsWith("http")) {
            final ITexture mTexture = new BitmapTexture(textureManager, new IInputStreamOpener() {
                @Override
                public InputStream open() throws IOException {

                    final URL webUrl = new URL(url);

                    final HttpURLConnection connection = (HttpURLConnection) webUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    final InputStream input = connection.getInputStream();
                    final BufferedInputStream in = new BufferedInputStream(input);
                    return in;
                }
            });
            mTexture.load();
            final TextureRegion MyImageFromWeb = TextureRegionFactory.extractFromTexture(mTexture);

            return MyImageFromWeb;
        } else {
            final ITexture texture = new AssetBitmapTexture(textureManager, assetManager, url);
            final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
            texture.load();
            return textureRegion;
        }
    }
}

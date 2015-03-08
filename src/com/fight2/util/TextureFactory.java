package com.fight2.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.ActivityLocalInputStreamOpener;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;

public class TextureFactory {
    private static TextureFactory INSTANCE = new TextureFactory();
    private final Map<TextureEnum, ITextureRegion> datas = new HashMap<TextureEnum, ITextureRegion>();
    private final Map<String, ITextureRegion> cardDatas = new HashMap<String, ITextureRegion>();
    private final Map<String, String> imageDatas = new HashMap<String, String>();
    private GameActivity activity;
    private TextureManager textureManager;
    private AssetManager assetManager;

    private TextureFactory() {
        // Private the constructor;
    }

    public static TextureFactory getInstance() {
        return INSTANCE;
    }

    public void clear() {

        // for (final Entry<TextureEnum, ITextureRegion> entry : datas.entrySet()) {
        // final TextureEnum textureEnum = entry.getKey();
        // final ITextureRegion textureRegion = entry.getValue();
        // final ITexture texture = textureRegion.getTexture();
        // if (textureEnum != TextureEnum.CHAT_INPUT_OPEN && textureEnum != TextureEnum.CHAT_INPUT_OPEN_FCS && !sceneTexture.get(sceneEnum).contains(texture)) {
        // texture.unload();
        // }
        // }
        datas.clear();
        // for (final ITextureRegion textureRegion : cardDatas.values()) {
        // final ITexture texture = textureRegion.getTexture();
        // if (!sceneTexture.get(sceneEnum).contains(texture)) {
        // texture.unload();
        // }
        // }
        cardDatas.clear();
        imageDatas.clear();
    }

    public void init(final TextureManager textureManager, final AssetManager assetManager) throws IOException {
        this.textureManager = textureManager;
        this.assetManager = assetManager;
    }

    public void initImageData(final GameActivity activity) throws IOException {
        final ImageOpenHelper dbHelper = activity.getDbHelper();
        final String selectQuery = "SELECT * FROM " + ImageOpenHelper.TABLE_NAME + " WHERE status=1";
        this.activity = activity;
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        final Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                imageDatas.put(cursor.getString(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        final Set<String> fileSet = new HashSet<String>();
        for (final String fileName : activity.fileList()) {
            fileSet.add(fileName);
        }
        final Iterator<Entry<String, String>> it = imageDatas.entrySet().iterator();
        while (it.hasNext()) {
            final Entry<String, String> entry = it.next();
            final String webUrl = entry.getKey();
            final String localUrl = entry.getValue();
            if (!fileSet.contains(localUrl)) {
                ImageUtils.invalidImage(webUrl, activity);
                it.remove();
            }
        }
    }

    public Map<String, String> getImageDatas() {
        return imageDatas;
    }

    public ITextureRegion getAssetTextureRegion(final TextureEnum textureEnum) {
        try {
            if (datas.containsKey(textureEnum)) {
                return datas.get(textureEnum);
            } else {
                final ITexture texture = new AssetBitmapTexture(textureManager, assetManager, textureEnum.getUrl());
                final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(texture);
                texture.load();
                datas.put(textureEnum, textureRegion);
                return textureRegion;
            }

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ITextureRegion newTextureRegion(final String imageString) {
        try {
            if (cardDatas.containsKey(imageString)) {
                return cardDatas.get(imageString);
            } else {
                // Debug.e("Image:"+imageString);
                final ITextureRegion textureRegion = createIextureRegion(activity, imageString);
                cardDatas.put(imageString, textureRegion);
                return textureRegion;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    private ITextureRegion createIextureRegion(final GameActivity activity, final String image) throws IOException {
        final ITexture mTexture = new BitmapTexture(activity.getTextureManager(), new ActivityLocalInputStreamOpener(activity, image));
        mTexture.load();
        final ITextureRegion textureRegion = TextureRegionFactory.extractFromTexture(mTexture);
        return textureRegion;
    }

}

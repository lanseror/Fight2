package com.fight2.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;

public class TextureFactory {
    private static TextureFactory INSTANCE = new TextureFactory();
    private final Map<TextureEnum, ITextureRegion> datas = new HashMap<TextureEnum, ITextureRegion>();
    private final Map<String, ITextureRegion> cardDatas = new HashMap<String, ITextureRegion>();
    private final Map<String, String> imageDatas = new HashMap<String, String>();

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

    public void initImageData(final GameActivity activity) throws IOException {
        final ImageOpenHelper dbHelper = activity.getDbHelper();
        final String selectQuery = "SELECT * FROM " + ImageOpenHelper.TABLE_NAME;

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                imageDatas.put(cursor.getString(0), cursor.getString(1));
            } while (cursor.moveToNext());
        }
    }

    public Map<String, String> getImageDatas() {
        return imageDatas;
    }

    public void loadCardsResource(final GameActivity activity) throws IOException {
        final GameUserSession session = GameUserSession.getInstance();
        final List<Card> cards = session.getCards();
        for (final Card card : cards) {
            final String image = card.getImage();
            if (!cardDatas.containsKey(image)) {
                final ITextureRegion textureRegion = createIextureRegion(activity, image);
                cardDatas.put(image, textureRegion);
            }
        }

    }

    public ITextureRegion getIextureRegion(final TextureEnum textureEnum) {
        return this.datas.get(textureEnum);
    }

    public ITextureRegion getIextureRegion(final String imageString) {
        return this.cardDatas.get(imageString);
    }

    private ITextureRegion createIextureRegion(final GameActivity activity, final String image) throws IOException {
        final ITexture mTexture = new BitmapTexture(activity.getTextureManager(), new IInputStreamOpener() {
            @Override
            public InputStream open() throws IOException {
                return activity.openFileInput(image);
            }
        });

        mTexture.load();
        return TextureRegionFactory.extractFromTexture(mTexture);
    }
}

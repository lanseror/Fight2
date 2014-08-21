package com.fight2.util;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;

public class EntryFactory {
    private static EntryFactory INSTANCE = new EntryFactory();
    private VertexBufferObjectManager vbom;

    private EntryFactory() {
        // Private the constructor;
    }

    public static EntryFactory getInstance() {
        return INSTANCE;
    }

    public void init(final VertexBufferObjectManager vbom) {
        this.vbom = vbom;
    }

    public F2ButtonSprite createALBF2ButtonSprite(final TextureEnum normalTextureEnum, final TextureEnum pressedTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final ITextureRegion pressedTexture = textureFactory.getAssetTextureRegion(pressedTextureEnum);
        final float width = normalTextureEnum.getWidth();
        final float height = normalTextureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final F2ButtonSprite sprite = new F2ButtonSprite(pX, pY, normalTexture, pressedTexture, vbom);
        return sprite;
    }

    public F2ButtonSprite createACF2ButtonSprite(final TextureEnum normalTextureEnum, final TextureEnum pressedTextureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion normalTexture = textureFactory.getAssetTextureRegion(normalTextureEnum);
        final ITextureRegion pressedTexture = textureFactory.getAssetTextureRegion(pressedTextureEnum);
        final F2ButtonSprite sprite = new F2ButtonSprite(x, y, normalTexture, pressedTexture, vbom);
        return sprite;
    }

    /**
     * Anchor left bottom sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createALBImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom);
        return sprite;
    }

    /**
     * Anchor left bottom sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createALBImageSprite(final TextureEnum textureEnum, final float x, final float y, final F2OnClickListener onClickListener) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final float pX = x + width * 0.5f;
        final float pY = y + height * 0.5f;
        final Sprite sprite = new Sprite(pX, pY, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    onClickListener.onClick(this, pTouchAreaLocalX, pTouchAreaLocalY);
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    /**
     * Anchor center sprite
     * 
     * @param textureEnum
     * @param x
     * @param y
     * @return
     */
    public Sprite createACImageSprite(final TextureEnum textureEnum, final float x, final float y) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(x, y, width, height, texture, vbom);
        return sprite;
    }

}

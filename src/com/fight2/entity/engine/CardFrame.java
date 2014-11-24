package com.fight2.entity.engine;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardFrame extends Rectangle {
    private final static float CARD_WIDTH = 289.5f;
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final IEntity cardSprite;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;

    public CardFrame(final float x, final float y, final float width, final float height, final Card card, final GameActivity activity) {
        super(x, y, width, height, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        final BigDecimal decWidth = BigDecimal.valueOf(width);
        final BigDecimal decBaseWidth = BigDecimal.valueOf(CARD_WIDTH);
        final float scale = decWidth.divide(decBaseWidth, 6, RoundingMode.HALF_UP).floatValue();

        final ITextureRegion coverTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        cardSprite = new Sprite(width * 0.5f, height * 0.5f, width + 3 * scale, height + 3 * scale, coverTexture, vbom);
        this.attachChild(cardSprite);

        loadImageFromServer(card);

        final Font mFont = ResourceManager.getInstance().getFont(FontEnum.Default, (int) (27 * scale));
        final ITextureRegion hpAtkFrameTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_HPATK);
        final Sprite hpAtkFrame = new Sprite(width - 90 * scale, 75 * scale, hpAtkFrameTexture.getWidth() * scale, hpAtkFrameTexture.getHeight() * scale,
                hpAtkFrameTexture, vbom);
        this.attachChild(hpAtkFrame);
        final Text hpText = new Text(70 * scale, 70 * scale, mFont, "0123456789", vbom);
        hpText.setColor(0XFFFFE3B0);
        final Text atkText = new Text(70 * scale, 23 * scale, mFont, "0123456789", vbom);
        atkText.setColor(0XFFFFE3B0);
        hpText.setText(String.valueOf(card.getHp()));
        atkText.setText(String.valueOf(card.getAtk()));
        hpAtkFrame.attachChild(hpText);
        hpAtkFrame.attachChild(atkText);

        final ITextureRegion cardFrameTexture = getTexture(card);
        final float frameY = (cardFrameTexture.getHeight() * 0.5f - 11f) * scale;
        final Sprite cardFrameSprite = new Sprite(width * 0.5f, frameY, cardFrameTexture, vbom);
        cardFrameSprite.setScale(scale);
        this.attachChild(cardFrameSprite);

        final Font levelFont = ResourceManager.getInstance().getFont(FontEnum.Bold, (int) (32 * scale));
        final Text levelText = new Text(31.5f * scale, 25.5f * scale, levelFont, String.valueOf(card.getLevel()), vbom);
        levelText.setColor(0XFFFFE3B0);
        this.attachChild(levelText);
        
        final ITextureRegion starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR);
        for (int i = 0; i < card.getStar(); i++) {
            final Sprite star = new Sprite((87 + 27f * i) * scale, height - 24 * scale, starTexture.getWidth() * scale, starTexture.getHeight() * scale,
                    starTexture, vbom);
            this.attachChild(star);
        }
    }

    private static ITextureRegion getTexture(final Card card) {
        TextureEnum textureEnum;
        switch (card.getRace()) {
            case Human:
                textureEnum = TextureEnum.COMMON_CARD_FRAME_HUMAN;
                break;
            case Angel:
                textureEnum = TextureEnum.COMMON_CARD_FRAME_ANGEL;
                break;
            case Elf:
                textureEnum = TextureEnum.COMMON_CARD_FRAME_ELF;
                break;
            case Devil:
                textureEnum = TextureEnum.COMMON_CARD_FRAME_DEVIL;
                break;
            default:
                textureEnum = TextureEnum.COMMON_CARD_FRAME_HUMAN;
        }
        return TEXTURE_FACTORY.getAssetTextureRegion(textureEnum);
    }

    private void loadImageFromServer(final Card card) {
        final IAsyncCallback callback = new IAsyncCallback() {
            private String avatar;
            private String image;

            @Override
            public void workToDo() {
                try {
                    avatar = ImageUtils.getLocalString(card.getAvatar(), activity);
                    image = ImageUtils.getLocalString(card.getImage(), activity);
                    TEXTURE_FACTORY.addCardResource(activity, avatar);
                    TEXTURE_FACTORY.addCardResource(activity, image);
                    card.setAvatar(avatar);
                    card.setImage(image);
                } catch (final IOException e) {
                    Debug.e(e);
                }

            }

            @Override
            public void onComplete() {

                if (image != null) {
                    final ITextureRegion texture = TEXTURE_FACTORY.getTextureRegion(image);
                    final Sprite imageSprite = new Sprite(mWidth * 0.5f, mHeight * 0.5f, mWidth, mHeight, texture, vbom);
                    cardSprite.attachChild(imageSprite);
                }

            }

        };

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncTaskLoader().execute(callback);
            }
        });

    }
}

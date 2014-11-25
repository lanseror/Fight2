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
import com.fight2.entity.Card.Race;
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

        final Race race = card.getRace();
        final ITextureRegion cardFrameTexture = getTexture(race);
        final float frameY = (cardFrameTexture.getHeight() * 0.5f - 11f) * scale;
        final Sprite cardFrameSprite = new Sprite(width * 0.5f, frameY, cardFrameTexture, vbom);
        cardFrameSprite.setScale(scale);
        this.attachChild(cardFrameSprite);

        float levelX = 0;
        float levelY = 0;
        switch (race) {
            case Human:
                levelX = 32f;
                levelY = 24f;
                break;
            case Angel:
                levelX = 28f;
                levelY = 24f;
                break;
            case Elf:
                levelX = 31f;
                levelY = 23f;
                break;
            case Devil:
                levelX = 32f;
                levelY = 23f;
                break;
        }
        final Font levelFont = ResourceManager.getInstance().getFont(FontEnum.Bold, (int) (30 * scale));
        final Text levelText = new Text(levelX * scale, levelY * scale, levelFont, String.valueOf(card.getLevel()), vbom);
        levelText.setColor(0XFFFFE3B0);
        this.attachChild(levelText);

        final ITextureRegion starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR);
        for (int i = 0; i < card.getStar(); i++) {
            final Sprite star = new Sprite((87 + 27f * i) * scale, height - 24 * scale, starTexture.getWidth() * scale, starTexture.getHeight() * scale,
                    starTexture, vbom);
            this.attachChild(star);
        }

        ITextureRegion tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN);
        ITextureRegion tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN);
        float tierGridX = 114;
        float tierGridY = 1;
        switch (race) {
            case Human:
                tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN);
                tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN);
                tierGridX = 112;
                tierGridY = 1;
                break;
            case Angel:
                tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_COMMON);
                tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ANGEL);
                tierGridX = 108;
                tierGridY = 6.5f;
                break;
            case Elf:
                tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_COMMON);
                tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ELF);
                tierGridX = 99;
                tierGridY = 1;
                break;
            case Devil:
                tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_COMMON);
                tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_DEVIL);
                tierGridX = 105;
                tierGridY = -1;
                break;
        }
        final float tierGridWidth = tierGridTexture.getWidth();
        final float tierGridHeight = tierGridTexture.getHeight();
        final float tierStickWidth = tierStickTexture.getWidth();
        final float tierStickHeight = tierStickTexture.getHeight();
        final Sprite tierGrid = new Sprite(tierGridX * scale, tierGridY * scale, tierGridWidth * scale, tierGridHeight * scale, tierGridTexture, vbom);
        final Sprite tierStick = new Sprite(tierGridWidth * 0.5f, tierGridHeight * 0.5f - 1, tierStickWidth * scale, tierStickHeight * scale, tierStickTexture,
                vbom);
        tierGrid.attachChild(tierStick);
        this.attachChild(tierGrid);
        final int tierCount = (int) Math.ceil(card.getStar() * 0.5);
        for (int i = 0; i < tierCount; i++) {
            final Sprite tierGridAdd = new Sprite(tierGridX * scale + (tierGridWidth + 1) * scale * (i + 1), tierGridY * scale, tierGridWidth * scale,
                    tierGridHeight * scale, tierGridTexture, vbom);
            if ((card.getTier() - 1) > i) {
                final Sprite tierStickAdd = new Sprite(tierGridWidth * 0.5f, tierGridHeight * 0.5f - 1, tierStickWidth * scale, tierStickHeight * scale,
                        tierStickTexture, vbom);
                tierGridAdd.attachChild(tierStickAdd);
            }
            this.attachChild(tierGridAdd);
        }

    }

    private static ITextureRegion getTexture(final Race race) {
        TextureEnum textureEnum;
        switch (race) {
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

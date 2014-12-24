package com.fight2.entity.engine;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.Card.Race;
import com.fight2.scene.CardInfoScene;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.CardUtils;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardFrame extends Rectangle {
    private final static float CARD_WIDTH = 289.5f;
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final IEntity cardCoverSprite;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final Text hpText;
    private final Text atkText;
    private final Text levelText;
    private final Card card;

    public CardFrame(final float x, final float y, final float width, final float height, final Card card, final GameActivity activity) {
        super(x, y, width, height, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.setAlpha(0);
        final BigDecimal decWidth = BigDecimal.valueOf(width);
        final BigDecimal decBaseWidth = BigDecimal.valueOf(CARD_WIDTH);
        final float scale = decWidth.divide(decBaseWidth, 6, RoundingMode.HALF_UP).floatValue();
        final ITextureRegion coverTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        cardCoverSprite = new Sprite(width * 0.5f, height * 0.5f, width + 3 * scale, height + 3 * scale, coverTexture, vbom);
        cardCoverSprite.setZIndex(0);
        this.attachChild(cardCoverSprite);
        this.card = card;

        loadImageFromServer(card);

        final Race race = card.getRace();
        final ITextureRegion cardFrameTexture = getTexture(race);
        final float frameY = (cardFrameTexture.getHeight() * 0.5f - 11f) * scale;
        final Sprite cardFrameSprite = new Sprite(width * 0.5f, frameY, cardFrameTexture, vbom);
        cardFrameSprite.setScale(scale);
        cardFrameSprite.setZIndex(1);
        this.attachChild(cardFrameSprite);

        float levelX = 0;
        float levelY = 0;
        float hpAtkX = 0;
        float hpY = 0;
        float atkY = 0;
        switch (race) {
            case Human:
                levelX = 32f;
                levelY = 24f;
                hpAtkX = 193;
                hpY = 83;
                atkY = 36;
                break;
            case Angel:
                levelX = 28f;
                levelY = 24f;
                hpAtkX = 193;
                hpY = 88;
                atkY = 41;
                break;
            case Elf:
                levelX = 31f;
                levelY = 23f;
                hpAtkX = 193;
                hpY = 88;
                atkY = 41;
                break;
            case Devil:
                levelX = 32f;
                levelY = 23f;
                hpAtkX = 193;
                hpY = 84;
                atkY = 37;
                break;
        }

        final Font hpatkFont = ResourceManager.getInstance().getFont(FontEnum.Default, (int) (27 * scale));
        hpText = new Text(hpAtkX * scale, hpY * scale, hpatkFont, "1234567890", vbom);
        hpText.setText(String.valueOf(card.getHp()));
        hpText.setColor(0XFFFFE3B0);
        atkText = new Text(hpAtkX * scale, atkY * scale, hpatkFont, "1234567890", vbom);
        atkText.setText(String.valueOf(card.getAtk()));
        atkText.setColor(0XFFFFE3B0);
        this.attachChild(hpText);
        hpText.setZIndex(2);
        this.attachChild(atkText);
        atkText.setZIndex(3);

        final Font levelFont = ResourceManager.getInstance().getFont(FontEnum.Bold, (int) (30 * scale));
        levelText = new Text(levelX * scale, levelY * scale, levelFont, "1234567890", vbom);
        levelText.setText(String.valueOf(card.getLevel()));
        levelText.setColor(0XFFFFE3B0);
        this.attachChild(levelText);
        levelText.setZIndex(4);

        ITextureRegion starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_1);
        switch (card.getStar()) {
            case 1:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_1);
                break;
            case 2:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_2);
                break;
            case 3:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_3);
                break;
            case 4:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_4);
                break;
            case 5:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_5);
                break;
            case 6:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_6);
                break;
        }
        final Sprite star = new Sprite((85 + starTexture.getWidth() * 0.5f) * scale, height - 24 * scale, starTexture.getWidth() * scale,
                starTexture.getHeight() * scale, starTexture, vbom);
        this.attachChild(star);
        star.setZIndex(5);

        ITextureRegion tierGridTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN);
        ITextureRegion tierGridTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN_2);
        ITextureRegion tierStickTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN);
        ITextureRegion tierStickTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN_2);
        ITextureRegion tierStickTexture3 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN_3);
        float tierGridX = 114;
        float tierGridY = 1;
        float tierStickX = 114;
        float tierStickY = 1;
        switch (race) {
            case Human:
                tierGridTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN);
                tierGridTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_HUMAN_2);
                tierStickTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN_2);
                tierStickTexture3 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_HUMAN_3);
                tierGridX = 164;
                tierGridY = 1;
                tierStickX = 128;
                tierStickY = 0;
                break;
            case Angel:
                tierGridTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_ANGEL);
                tierGridTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_ANGEL_2);
                tierStickTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ANGEL);
                tierStickTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ANGEL_2);
                tierStickTexture3 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ANGEL_3);
                tierGridX = 164;
                tierGridY = 6.5f;
                tierStickX = 130;
                tierStickY = 5.5f;
                break;
            case Elf:
                tierGridTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_ELF);
                tierGridTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_ELF_2);
                tierStickTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ELF);
                tierStickTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ELF_2);
                tierStickTexture3 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_ELF_3);
                tierGridX = 153;
                tierGridY = 1;
                tierStickX = 117;
                tierStickY = 0;
                break;
            case Devil:
                tierGridTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_DEVIL);
                tierGridTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_GRID_DEVIL_2);
                tierStickTexture1 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_DEVIL);
                tierStickTexture2 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_DEVIL_2);
                tierStickTexture3 = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME_TIER_STICK_DEVIL_3);
                tierGridX = 153;
                tierGridY = -0.5f;
                tierStickX = 116;
                tierStickY = -1.5f;
                break;
        }

        final int tierGridAddAmount = CardUtils.getMaxEvoTier(card) - 2;
        if (tierGridAddAmount == 1) {
            final float tierGridWidth = tierGridTexture1.getWidth() * scale;
            final float tierGridHeight = tierGridTexture1.getHeight() * scale;
            final Sprite tierGridAdd = new Sprite(tierGridX * scale + tierGridWidth * 0.5f, tierGridY * scale, tierGridWidth, tierGridHeight, tierGridTexture1,
                    vbom);
            this.attachChild(tierGridAdd);
            tierGridAdd.setZIndex(6);
        } else if (tierGridAddAmount == 2) {
            final float tierGridWidth = tierGridTexture2.getWidth() * scale;
            final float tierGridHeight = tierGridTexture2.getHeight() * scale;
            final Sprite tierGridAdd = new Sprite(tierGridX * scale + tierGridWidth * 0.5f, tierGridY * scale, tierGridWidth, tierGridHeight, tierGridTexture2,
                    vbom);
            this.attachChild(tierGridAdd);
            tierGridAdd.setZIndex(6);
        }

        final int tierStickAddAmount = card.getTier() - 1;
        ITextureRegion tierStickTexture = tierStickTexture1;
        if (tierStickAddAmount == 1) {
            tierStickTexture = tierStickTexture1;
        } else if (tierStickAddAmount == 2) {
            tierStickTexture = tierStickTexture2;
        } else if (tierStickAddAmount == 3) {
            tierStickTexture = tierStickTexture3;
        }
        if (tierStickAddAmount > 0) {
            final float tierStickWidth = tierStickTexture.getWidth() * scale;
            final float tierStickHeight = tierStickTexture.getHeight() * scale;
            final Sprite tierStickAdd = new Sprite(tierStickX * scale + tierStickWidth * 0.5f, tierStickY * scale, tierStickWidth, tierStickHeight,
                    tierStickTexture, vbom);
            this.attachChild(tierStickAdd);
            tierStickAdd.setZIndex(7);
        }

    }

    @Override
    public boolean onAreaTouched(final TouchEvent sceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (sceneTouchEvent.isActionCancel() || sceneTouchEvent.isActionUp()) {
            try {
                final Scene cardInfoScene = new CardInfoScene(activity, card);
                final Scene scene = activity.getEngine().getScene();
                Scene childScene = scene;
                while (childScene.getChildScene() != null) {
                    childScene = childScene.getChildScene();
                }
                childScene.setChildScene(cardInfoScene, false, false, true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        return false;
    }

    public Card getCard() {
        return card;
    }

    public void updateCardAttributes(final Card card) {
        hpText.setText(String.valueOf(card.getHp()));
        hpText.setColor(0XFF5AD61E);
        atkText.setText(String.valueOf(card.getAtk()));
        atkText.setColor(0XFF5AD61E);
        levelText.setText(String.valueOf(card.getLevel()));
        levelText.setColor(0XFF5AD61E);
    }

    public void revertCardAttributes() {
        hpText.setText(String.valueOf(card.getHp()));
        hpText.setColor(0XFFFFE3B0);
        atkText.setText(String.valueOf(card.getAtk()));
        atkText.setColor(0XFFFFE3B0);
        levelText.setText(String.valueOf(card.getLevel()));
        levelText.setColor(0XFFFFE3B0);
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
                    if (!card.isAvatarLoaded() && card.getAvatar() != null) {
                        avatar = ImageUtils.getLocalString(card.getAvatar(), activity);
                        card.setAvatar(avatar);
                        card.setAvatarLoaded(true);
                    } else {
                        avatar = card.getAvatar();
                    }

                    if (!card.isImageLoaded()) {
                        image = ImageUtils.getLocalString(card.getImage(), activity);
                        card.setImage(image);
                        card.setImageLoaded(true);
                    } else {
                        image = card.getImage();
                    }
                } catch (final IOException e) {
                    Debug.e(e);
                }

            }

            @Override
            public void onComplete() {

                if (image != null) {
                    final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(image);
                    final Sprite imageSprite = new Sprite(mWidth * 0.5f, mHeight * 0.5f, mWidth, mHeight, texture, vbom);
                    imageSprite.setZIndex(0);
                    final IEntity parent = cardCoverSprite.getParent();
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            cardCoverSprite.detachSelf();
                            parent.attachChild(imageSprite);
                            parent.sortChildren();
                        }
                    });

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

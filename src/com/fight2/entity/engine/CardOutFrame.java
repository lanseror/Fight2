package com.fight2.entity.engine;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.scene.BaseScene;
import com.fight2.scene.CardInfoScene;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IRCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardOutFrame extends Sprite {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private IEntity cardCoverSprite;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final Card card;
    private final float width;
    private final float height;
    private static BigDecimal FACTOR_BASE = BigDecimal.valueOf(400);

    public CardOutFrame(final float x, final float y, final float fWidth, final float fHeight, final Card card, final GameActivity activity) {
        super(x, y, fWidth, fHeight, TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_FRAME), activity.getVertexBufferObjectManager());
        final BigDecimal factor = BigDecimal.valueOf(fWidth).divide(FACTOR_BASE, 4, RoundingMode.HALF_UP);
        final float offSet = factor.multiply(BigDecimal.valueOf(8)).floatValue();
        this.width = fWidth - offSet;
        this.height = this.width * 1.5f;
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();

        this.card = card;

        if (ImageUtils.isCached(card.getImage()) && !card.isImageLoaded()) {
            try {
                card.setImage(ImageUtils.getLocalString(card.getImage(), activity));
                card.setImageLoaded(true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (card.isImageLoaded()) {
            final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(card.getImage());
            final Sprite imageSprite = new Sprite(mWidth * 0.5f, mHeight * 0.5f, width, height, texture, vbom);
            imageSprite.setZIndex(-1);
            this.attachChild(imageSprite);
        } else {
            final ITextureRegion coverTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
            cardCoverSprite = new Sprite(mWidth * 0.5f, mHeight * 0.5f, width, height, coverTexture, vbom);
            cardCoverSprite.setZIndex(-1);
            this.attachChild(cardCoverSprite);
            loadImageFromServer(card);
        }
    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent sceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (sceneTouchEvent.isActionCancel() || sceneTouchEvent.isActionUp()) {
            final Scene scene = activity.getEngine().getScene();
            Scene childScene = scene;
            while (childScene.getChildScene() != null) {
                childScene = childScene.getChildScene();
            }
            final BaseScene currentChildScene = (BaseScene) childScene;
            ResourceManager.getInstance().setChildScene(currentChildScene, new IRCallback<BaseScene>() {
                @Override
                public BaseScene onCallback() {
                    try {
                        return new CardInfoScene(activity, card);
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void loadImageFromServer(final Card card) {
        final IAsyncCallback callback = new IAsyncCallback() {
            private String image;

            @Override
            public void workToDo() {
                try {
                    if (!card.isImageLoaded() && card.getImage() != null) {
                        image = ImageUtils.getLocalString(card.getImage(), activity);
                        card.setImage(image);
                        card.setImageLoaded(true);
                    } else {
                        image = card.getImage();
                    }

                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onComplete() {

                if (image != null) {
                    final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(image);
                    final Sprite imageSprite = new Sprite(mWidth * 0.5f, mHeight * 0.5f, width, height, texture, vbom);
                    imageSprite.setZIndex(-1);
                    final IEntity parent = cardCoverSprite.getParent();
                    activity.runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            cardCoverSprite.detachSelf();
                            parent.attachChild(imageSprite);
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

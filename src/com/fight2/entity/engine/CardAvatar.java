package com.fight2.entity.engine;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.scene.CardInfoScene;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.TextureFactory;

public class CardAvatar extends Rectangle {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final IEntity cardCoverSprite;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final Card card;

    public CardAvatar(final float x, final float y, final float width, final float height, final Card card, final GameActivity activity) {
        super(x, y, width, height, activity.getVertexBufferObjectManager());
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();
        this.setAlpha(0);
        final ITextureRegion coverTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        cardCoverSprite = new Sprite(width * 0.5f, height * 0.5f, width, height, coverTexture, vbom);
        cardCoverSprite.setZIndex(0);
        this.attachChild(cardCoverSprite);
        this.card = card;

        loadImageFromServer(card);

    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent sceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
        if (sceneTouchEvent.isActionCancel() || sceneTouchEvent.isActionUp()) {
            try {
                final Scene cardInfoScene = new CardInfoScene(activity, card);
                activity.getEngine().getScene().setChildScene(cardInfoScene, false, false, true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            return true;
        }
        return false;
    }

    private void loadImageFromServer(final Card card) {
        final IAsyncCallback callback = new IAsyncCallback() {
            private String avatar;

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

                } catch (final IOException e) {
                    Debug.e(e);
                }

            }

            @Override
            public void onComplete() {

                if (avatar != null) {
                    final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(avatar);
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

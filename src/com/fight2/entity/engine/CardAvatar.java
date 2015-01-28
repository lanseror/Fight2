package com.fight2.entity.engine;

import java.io.IOException;

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

public class CardAvatar extends Sprite {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private IEntity cardCoverSprite;
    private final GameActivity activity;
    private final VertexBufferObjectManager vbom;
    private final Card card;
    private final float width;
    private final float height;

    public CardAvatar(final float x, final float y, final float width, final float height, final Card card, final GameActivity activity) {
        super(x, y, width + 2, height + 2, TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_AVATAR_FRAME), activity.getVertexBufferObjectManager());
        this.width = width;
        this.height = height;
        this.activity = activity;
        this.vbom = activity.getVertexBufferObjectManager();

        this.card = card;

        if (ImageUtils.isCached(card.getAvatar()) && !card.isAvatarLoaded()) {
            try {
                card.setAvatar(ImageUtils.getLocalString(card.getAvatar(), activity));
                card.setAvatarLoaded(true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (card.isAvatarLoaded()) {
            final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(card.getAvatar());
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
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onComplete() {

                if (avatar != null) {
                    final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(avatar);
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

package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.MusicEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.F2MusicManager;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.ImageUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class SummonFinishScene extends BaseScene {
    private final Sprite cardSprite;
    final TextureFactory textureFactory = TextureFactory.getInstance();

    public SummonFinishScene(final Card card, final GameActivity activity) throws IOException {
        super(activity);
        loadImageFromServer(card);
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        cardSprite = new Sprite(cameraCenterX, cameraCenterY, 100, 150, texture, vbom);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);
        this.setOnSceneTouchListener(new IOnSceneTouchListener() {

            @Override
            public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
                if (pSceneTouchEvent.isActionDown()) {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Summon);
                }
                return false;
            }

        });

        this.attachChild(cardSprite);
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        // playAnimation();
    }

    @Override
    protected void playAnimation() {
        F2MusicManager.getInstance().playMusic(MusicEnum.SUMMON);
        cardSprite.setRotation(90);
        final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(0.3f, 1f, 3), new RotationByModifier(0.3f, 270));
        cardSprite.registerEntityModifier(modifier);
    }

    public void loadImageFromServer(final Card card) {
        final IAsyncCallback callback = new IAsyncCallback() {
            private String avatar;
            private String image;

            @Override
            public void workToDo() {
                try {
                    avatar = ImageUtils.getLocalString(card.getAvatar(), activity);
                    image = ImageUtils.getLocalString(card.getImage(), activity);
                    textureFactory.addCardResource(activity, avatar);
                    textureFactory.addCardResource(activity, image);
                    card.setAvatar(avatar);
                    card.setImage(image);
                } catch (final IOException e) {
                    Debug.e(e);
                }

            }

            @Override
            public void onComplete() {

                if (image != null) {
                    final ITextureRegion texture = textureFactory.getTextureRegion(image);
                    final Sprite imageSprite = new Sprite(50, 75, 100, 150, texture, vbom);
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

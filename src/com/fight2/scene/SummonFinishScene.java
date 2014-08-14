package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.debug.Debug;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
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
    private final static int CARD_WIDTH = 100;
    private final static int CARD_HEIGHT = 150;
    private final Sprite cardSprite;
    private final IEntity cardFrame;
    private final TextureFactory textureFactory = TextureFactory.getInstance();
    private final Font mFont;
    private final Text hpText;
    private final Text atkText;

    public SummonFinishScene(final Card card, final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().getFont(FontEnum.Main, 10);
        this.cardFrame = new Rectangle(cameraCenterX, cameraCenterY, CARD_WIDTH, CARD_HEIGHT, vbom);
        cardFrame.setRotation(90);
        this.attachChild(cardFrame);
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_CARD_COVER);
        this.cardSprite = new Sprite(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
        cardFrame.attachChild(cardSprite);
        loadImageFromServer(card);
        hpText = new Text(30, 20, mFont, "0123456789", vbom);
        atkText = new Text(30, 10, mFont, "0123456789", vbom);
        hpText.setText(String.valueOf(card.getHp()));
        atkText.setText(String.valueOf(card.getAtk()));
        cardFrame.attachChild(hpText);
        cardFrame.attachChild(atkText);
        final ITextureRegion starTexture = textureFactory.getAssetTextureRegion(TextureEnum.COMMON_STAR);
        for (int i = 0; i < card.getStar(); i++) {
            final Sprite star = new Sprite(15 + 6.5f * i, CARD_HEIGHT - 6, 6.5f, 8, starTexture, vbom);
            cardFrame.attachChild(star);
        }
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
        final IEntityModifier modifier = new ParallelEntityModifier(new ScaleModifier(0.3f, 1f, 3), new RotationByModifier(0.3f, 270));
        cardFrame.registerEntityModifier(modifier);
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
                    final Sprite imageSprite = new Sprite(CARD_WIDTH * 0.5f, CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
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

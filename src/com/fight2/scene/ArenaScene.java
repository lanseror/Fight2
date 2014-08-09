package com.fight2.scene;

import java.io.IOException;
import java.util.List;

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
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.Player;
import com.fight2.util.ArenaUtils;
import com.fight2.util.F2MusicManager;
import com.fight2.util.ResourceManager;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class ArenaScene extends BaseScene {

    private final Player[] players = new Player[3];

    public ArenaScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createCameraImageSprite(TextureEnum.ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedWidth - 100, 250);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);
        updateScene();

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);

    }

    private Sprite createBattleSprite(final TextureEnum textureEnum, final float x, final float y, final int index) {
        final TextureFactory textureFactory = TextureFactory.getInstance();
        final ITextureRegion texture = textureFactory.getAssetTextureRegion(textureEnum);
        final float width = textureEnum.getWidth();
        final float height = textureEnum.getHeight();
        final Sprite sprite = new Sprite(x, y, width, height, texture, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionDown()) {
                    F2MusicManager.getInstance().playMusic(MusicEnum.ARENA_ATTACK);
                    try {
                        final Scene battleScene = new BattleScene(activity, players[index].getId());
                        activity.getEngine().setScene(battleScene);
                    } catch (final IOException e) {
                        Debug.e(e);
                    }
                    // ArenaUtils.attack(players[0].getId());
                    return true;
                }
                return false;
            }
        };
        return sprite;
    }

    @Override
    public void updateScene() {
        final List<Player> competitors = ArenaUtils.getCompetitors(activity);
        final TextureFactory textureFactory = TextureFactory.getInstance();
        for (int i = 0; i < 3 && i < competitors.size(); i++) {
            final Player competitor = competitors.get(i);
            players[i] = competitor;
            final String avatar = competitor.getAvatar();
            final ITextureRegion avatarTexture = StringUtils.isEmpty(avatar) ? textureFactory.getAssetTextureRegion(TextureEnum.COMMON_DEFAULT_AVATAR)
                    : textureFactory.getTextureRegion(avatar);
            final Sprite competitorSprite = new Sprite(633 - 250 * i, 245, avatarTexture, vbom);
            this.attachChild(competitorSprite);
        }
        for (int i = 0; i < players.length; i++) {
            final Player player = players[i];
            if (player == null) {
                continue;
            }
            final Sprite battleButton = createBattleSprite(TextureEnum.ARENA_BATTLE, 633 - 250 * i, 50, i);
            this.attachChild(battleButton);
            this.registerTouchArea(battleButton);
        }
    }
}

package com.fight2.scene;

import java.io.IOException;
import java.util.List;

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
import com.fight2.entity.F2ButtonSprite;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.Player;
import com.fight2.util.ArenaUtils;
import com.fight2.util.F2MusicManager;
import com.fight2.util.ResourceManager;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class ArenaScene extends BaseScene {
    private final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
    private final float topbarY = cameraHeight - TextureEnum.PARTY_TOPBAR.getHeight();
    private final float infoFrameY = topbarY - TextureEnum.ARENA_BATTLE_INFO.getHeight() + 10;
    private final float battleFrameY = infoFrameY - TextureEnum.ARENA_BATTLE_FRAME.getHeight();
    private final Font mFont;
    private final Text hpText;
    private final Text atkText;

    private final Player[] players = new Player[3];

    public ArenaScene(final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().getFont(FontEnum.Main);
        hpText = new Text(this.simulatedLeftX + 360, topbarY + 48, mFont, "0123456789", vbom);
        atkText = new Text(this.simulatedLeftX + 600, topbarY + 48, mFont, "0123456789", vbom);
        hpText.setText(String.valueOf(partyInfo.getHp()));
        atkText.setText(String.valueOf(partyInfo.getAtk()));
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.PARTY_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);
        this.attachChild(hpText);
        this.attachChild(atkText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite infoFrame = createALBImageSprite(TextureEnum.ARENA_BATTLE_INFO, this.simulatedLeftX, infoFrameY);
        this.attachChild(infoFrame);

        for (int i = 0; i < 3; i++) {
            final Sprite battleFrame = createALBImageSprite(TextureEnum.ARENA_BATTLE_FRAME, this.simulatedLeftX + 245 * i, battleFrameY);
            this.attachChild(battleFrame);
        }

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedWidth - 140, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite refleshButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_REFRESH, TextureEnum.ARENA_BATTLE_REFRESH_FCS,
                this.simulatedWidth - 140, 175);
        refleshButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            }
        });
        this.attachChild(refleshButton);
        this.registerTouchArea(refleshButton);

        final F2ButtonSprite rankButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_RANKING, TextureEnum.ARENA_BATTLE_RANKING_FCS,
                this.simulatedWidth - 140, 300);
        rankButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            }
        });
        this.attachChild(rankButton);
        this.registerTouchArea(rankButton);

        final F2ButtonSprite rewardButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_REWARD, TextureEnum.ARENA_BATTLE_REWARD_FCS,
                this.simulatedWidth - 140, 435);
        rewardButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            }
        });
        this.attachChild(rewardButton);
        this.registerTouchArea(rewardButton);

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
            final Sprite competitorSprite = new Sprite(606 - 245 * i, 295, 100, 100, avatarTexture, vbom);
            this.attachChild(competitorSprite);
        }
        for (int i = 0; i < players.length; i++) {
            final Player player = players[i];
            if (player == null) {
                continue;
            }
            final Sprite battleButton = createBattleSprite(TextureEnum.ARENA_BATTLE_BUTTON, 610 - 245 * i, 123, i);
            this.attachChild(battleButton);
            this.registerTouchArea(battleButton);
        }
    }
}

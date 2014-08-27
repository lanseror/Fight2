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
import com.fight2.entity.F2ButtonSprite.F2OnClickListener;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.User;
import com.fight2.entity.UserArenaInfo;
import com.fight2.entity.UserArenaRecord;
import com.fight2.util.ArenaUtils;
import com.fight2.util.BWShaderProgram;
import com.fight2.util.F2MusicManager;
import com.fight2.util.ResourceManager;
import com.fight2.util.StringUtils;
import com.fight2.util.TextureFactory;

public class ArenaScene extends BaseScene {
    private final PartyInfo partyInfo = GameUserSession.getInstance().getPartyInfo();
    private final float topbarY = cameraHeight - TextureEnum.ARENA_TOPBAR.getHeight();
    private final float infoFrameY = topbarY - TextureEnum.ARENA_BATTLE_INFO.getHeight() + 10;
    private final float battleFrameY = infoFrameY - TextureEnum.ARENA_BATTLE_FRAME.getHeight();
    private final Font topBarFont;
    private final Font infoFont;
    private final Font remainTimeFont;
    private final Font boldFaceFont;
    private final Font difficultyFont;
    private final Text hpText;
    private final Text atkText;
    private final Text ticketText;
    private final Text mightText;
    private final Text winText;
    private final Text rankText;
    private final Text loseText;
    private final Text remainTimeText;
    private final Text[] mightTexts = new Text[3];
    private final Text[] difficultyTexts = new Text[3];

    private final Sprite[] battleFrames = new Sprite[3];
    private final Sprite[] competitorSprites = new Sprite[3];
    private final Sprite[] battleButtons = new Sprite[3];
    private final Sprite[] winButtons = new Sprite[3];
    private final Sprite[] loseButtons = new Sprite[3];
    private final Text[] nameTexts = new Text[3];
    private final User[] players = new User[3];

    public ArenaScene(final GameActivity activity) throws IOException {
        super(activity);
        this.topBarFont = ResourceManager.getInstance().getFont(FontEnum.Main);
        this.infoFont = ResourceManager.getInstance().getFont(FontEnum.Default);
        this.remainTimeFont = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
        this.boldFaceFont = ResourceManager.getInstance().getFont(FontEnum.Bold);
        this.difficultyFont = ResourceManager.getInstance().getFont(FontEnum.Default, 21);
        hpText = new Text(280, 48, topBarFont, "0123456789", vbom);
        atkText = new Text(480, 48, topBarFont, "0123456789", vbom);
        ticketText = new Text(670, 48, topBarFont, "0123456789", vbom);
        mightText = new Text(140, 105, infoFont, "0123456789", vbom);
        winText = new Text(360, 105, infoFont, "0123456789", vbom);
        rankText = new Text(140, 40, infoFont, "0123456789", vbom);
        loseText = new Text(360, 40, infoFont, "0123456789", vbom);
        remainTimeText = new Text(658, 160, remainTimeFont, "0123456789: 天", vbom);
        remainTimeText.setColor(0XFFF8B451);
        mightTexts[0] = new Text(140, 90, boldFaceFont, "+10", vbom);
        mightTexts[1] = new Text(140, 90, boldFaceFont, "+ 8", vbom);
        mightTexts[2] = new Text(140, 90, boldFaceFont, "+ 5", vbom);
        difficultyTexts[0] = new Text(117, 137, difficultyFont, "困难", vbom);
        difficultyTexts[0].setColor(0XFFED6F00);
        difficultyTexts[1] = new Text(117, 137, difficultyFont, "一般", vbom);
        difficultyTexts[1].setColor(0XFFF8B551);
        difficultyTexts[2] = new Text(117, 137, difficultyFont, "容易", vbom);
        difficultyTexts[2].setColor(0XFFADCE00);
        hpText.setText(String.valueOf(partyInfo.getHp()));
        atkText.setText(String.valueOf(partyInfo.getAtk()));
        ticketText.setText("0");
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.ARENA_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite topbarSprite = createALBImageSprite(TextureEnum.ARENA_TOPBAR, this.simulatedLeftX, topbarY);
        this.attachChild(topbarSprite);
        topbarSprite.attachChild(hpText);
        topbarSprite.attachChild(atkText);
        topbarSprite.attachChild(ticketText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() + 20, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight());
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite infoFrame = createALBImageSprite(TextureEnum.ARENA_BATTLE_INFO, this.simulatedLeftX, infoFrameY);
        this.attachChild(infoFrame);

        infoFrame.attachChild(mightText);
        infoFrame.attachChild(winText);
        infoFrame.attachChild(rankText);
        infoFrame.attachChild(loseText);
        infoFrame.attachChild(remainTimeText);

        final Sprite continiousWinButton = createALBImageSprite(TextureEnum.ARENA_BATTLE_CONTINIOUS_WIN, 488, 15);
        infoFrame.attachChild(continiousWinButton);

        for (int i = 0; i < 3; i++) {
            final Sprite battleFrame = createALBImageSprite(TextureEnum.ARENA_BATTLE_FRAME, this.simulatedLeftX + 245 * i, battleFrameY);
            battleFrames[i] = battleFrame;
            this.attachChild(battleFrame);
            battleFrame.attachChild(mightTexts[i]);
            battleFrame.attachChild(difficultyTexts[i]);
            final Sprite battleButton = createBattleSprite(TextureEnum.ARENA_BATTLE_BUTTON, 122, 37, i);
            battleButtons[i] = battleButton;
            battleFrame.attachChild(battleButton);

            final Sprite winButton = this.createACImageSprite(TextureEnum.ARENA_RESULT_WIN, 122, 37);
            winButtons[i] = winButton;
            winButton.setVisible(false);
            battleFrame.attachChild(winButton);
            final Sprite loseButton = this.createACImageSprite(TextureEnum.ARENA_RESULT_LOSE, 122, 37);
            loseButtons[i] = loseButton;
            loseButton.setVisible(false);
            battleFrame.attachChild(loseButton);
        }

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 35);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.ArenaList);
                ArenaUtils.exit();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite refleshButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_REFRESH, TextureEnum.ARENA_BATTLE_REFRESH_FCS,
                this.simulatedRightX - 135, 170);
        refleshButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            }
        });
        this.attachChild(refleshButton);
        this.registerTouchArea(refleshButton);

        final F2ButtonSprite rankButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_RANKING, TextureEnum.ARENA_BATTLE_RANKING_FCS,
                this.simulatedRightX - 135, 305);
        rankButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
            }
        });
        this.attachChild(rankButton);
        this.registerTouchArea(rankButton);

        final F2ButtonSprite rewardButton = createALBF2ButtonSprite(TextureEnum.ARENA_BATTLE_REWARD, TextureEnum.ARENA_BATTLE_REWARD_FCS,
                this.simulatedRightX - 135, 440);
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
                        final User player = players[index];
                        final Scene battleScene = new BattleScene(activity, index, player.getId());
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
        final Font nameFont = ResourceManager.getInstance().getFont(FontEnum.Default, 20);
        final UserArenaInfo userArenaInfo = ArenaUtils.enter(activity);
        mightText.setText(String.valueOf(userArenaInfo.getMight()));
        winText.setText(String.valueOf(userArenaInfo.getWin()));
        rankText.setText(String.valueOf(userArenaInfo.getRankNumber()));
        loseText.setText(String.valueOf(userArenaInfo.getLose()));
        remainTimeText.setText(userArenaInfo.getRemainTime());
        final List<UserArenaRecord> userArenaRecords = userArenaInfo.getArenaRecords();
        final TextureFactory textureFactory = TextureFactory.getInstance();
        for (int i = 0; i < 3 && i < userArenaRecords.size(); i++) {
            // clean up;
            battleFrames[i].detachChild(competitorSprites[i]);
            this.unregisterTouchArea(battleButtons[i]);
            battleFrames[i].detachChild(nameTexts[i]);

            final UserArenaRecord userArenaRecord = userArenaRecords.get(i);
            final User competitor = userArenaRecord.getUser();
            players[i] = competitor;

            final Text nameText = new Text(120, 288, nameFont, competitor.getName(), vbom);
            nameText.setColor(0XFFFFD190);
            battleFrames[i].attachChild(nameText);
            nameTexts[i] = nameText;
            final String avatar = competitor.getAvatar();
            final ITextureRegion avatarTexture = StringUtils.isEmpty(avatar) ? textureFactory.getAssetTextureRegion(TextureEnum.COMMON_DEFAULT_AVATAR)
                    : textureFactory.getTextureRegion(avatar);
            final Sprite competitorSprite = new Sprite(117, 209, 100, 100, avatarTexture, vbom);
            battleFrames[i].attachChild(competitorSprite);
            competitorSprites[i] = competitorSprite;

            switch (userArenaRecord.getStatus()) {
                case NoAction:
                    battleButtons[i].setVisible(true);
                    winButtons[i].setVisible(false);
                    loseButtons[i].setVisible(false);
                    this.registerTouchArea(battleButtons[i]);
                    break;
                case Win:
                    battleButtons[i].setVisible(false);
                    winButtons[i].setVisible(true);
                    loseButtons[i].setVisible(false);
                    competitorSprite.setShaderProgram(BWShaderProgram.getInstance());
                    break;
                case Lose:
                    battleButtons[i].setVisible(false);
                    winButtons[i].setVisible(false);
                    loseButtons[i].setVisible(true);
                    break;
            }
        }
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }
}

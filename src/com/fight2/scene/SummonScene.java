package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.CostConstants;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.ScrollZone;
import com.fight2.entity.UserProperties;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.CardUtils;
import com.fight2.util.F2SoundManager;
import com.fight2.util.IRCallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;

public class SummonScene extends BaseScene {
    private static final float FRAME_WIDTH = TextureEnum.SUMMON_FRAME.getWidth();
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
    private final Text summonCharmText;
    private final Text summonStoneText;
    private final Text diamonText;
    private final F2ButtonSprite heroSummonStoneButton;
    private final F2ButtonSprite heroSummonDiamonButton;
    private final UserProperties userProps;
    private final Font costFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
    private final Sprite heroSummonFrame;

    public SummonScene(final GameActivity activity) throws IOException {
        super(activity);
        userProps = QuestUtils.getUserProperties(activity);
        GameUserSession.getInstance().setUserProps(userProps);
        this.summonCharmText = new Text(120, 45, font, String.valueOf(userProps.getSummonCharm()), 8, vbom);
        this.summonStoneText = new Text(315, 45, font, String.valueOf(userProps.getSummonStone()), 8, vbom);
        this.diamonText = new Text(123, 24, font, String.valueOf(userProps.getDiamon()), 8, vbom);

        // create summonFram;
        this.heroSummonFrame = this.createALBImageSprite(TextureEnum.SUMMON_FRAME, this.simulatedLeftX + 100, 130);
        this.heroSummonStoneButton = createSummonSprite(2);
        final Sprite buttonSummonStone = createACImageSprite(TextureEnum.COMMON_SUMMON_STONE, 40, TextureEnum.SUMMON_BUTTON.getHeight() * 0.5f);
        heroSummonStoneButton.attachChild(buttonSummonStone);
        final Text summonStoneCostText = new Text(80, TextureEnum.SUMMON_BUTTON.getHeight() * 0.5f, costFont,
                String.valueOf(CostConstants.HERO_SUMMON_STONE_COST), vbom);
        heroSummonStoneButton.attachChild(summonStoneCostText);

        this.heroSummonDiamonButton = createSummonSprite(3);
        final Sprite buttonSummonDiamon = createACImageSprite(TextureEnum.COMMON_DIAMOND, 40, TextureEnum.SUMMON_BUTTON.getHeight() * 0.5f);
        heroSummonDiamonButton.attachChild(buttonSummonDiamon);
        final Text summonDiamonCostText = new Text(80, TextureEnum.SUMMON_BUTTON.getHeight() * 0.5f, costFont,
                String.valueOf(CostConstants.HERO_SUMMON_DIAMON_COST), vbom);
        heroSummonDiamonButton.attachChild(summonDiamonCostText);

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final ScrollZone scrollZone = new ScrollZone(this.simulatedLeftX + 100 + FRAME_WIDTH * 0.5f, cameraCenterY, FRAME_WIDTH, simulatedHeight, vbom);
        final IEntity touchArea = scrollZone.createTouchArea(this.simulatedLeftX + 100 + FRAME_WIDTH * 0.5f, cameraCenterY, FRAME_WIDTH, simulatedHeight);
        this.attachChild(scrollZone);
        final IEntity topSpace = new Rectangle(0, 0, FRAME_WIDTH, TextureEnum.SUMMON_TOPBAR.getHeight(), vbom);
        topSpace.setAlpha(0);
        scrollZone.attachRow(topSpace);
        final Sprite basicSummonFrame = createBasicSummonFrame();
        scrollZone.attachRow(basicSummonFrame);
        final IEntity space = new Rectangle(0, 0, FRAME_WIDTH, 5, vbom);
        space.setAlpha(0);
        scrollZone.attachRow(space);
        final Sprite heroSummonFrame = createHeroSummonFrame();
        scrollZone.attachRow(heroSummonFrame);
        this.registerTouchArea(touchArea);

        final Sprite topBar = createALBImageSprite(TextureEnum.SUMMON_TOPBAR, this.simulatedLeftX, this.simulatedHeight - TextureEnum.SUMMON_TOPBAR.getHeight());
        this.attachChild(topBar);
        topBar.attachChild(summonCharmText);
        topBar.attachChild(summonStoneText);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);
        rechargeSprite.attachChild(diamonText);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK);
                ResourceManager.getInstance().sceneBack();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private Sprite createBasicSummonFrame() {
        final Sprite summonFrame = this.createALBImageSprite(TextureEnum.SUMMON_FRAME, this.simulatedLeftX + 100, 130);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text titleText = new Text(95, summonFrame.getHeight() - 35, titleFont, "基本召唤", vbom);
        titleText.setColor(0XFFFAB103);
        summonFrame.attachChild(titleText);

        final Sprite stars = this.createACImageSprite(TextureEnum.COMMON_STAR_3, 500, summonFrame.getHeight() - 35);
        summonFrame.attachChild(stars);
        final Text subTitleText = new Text(590, summonFrame.getHeight() - 35, titleFont, "星以下", vbom);
        subTitleText.setColor(0XFFFAB103);
        summonFrame.attachChild(subTitleText);

        final Font descFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 265);
        final Text descText = new Text(540, 170, descFont, "使用200个召唤符，即可获得一张卡片。", textOptions, vbom);
        this.topAlignEntity(descText, summonFrame.getHeight() - 115);
        summonFrame.attachChild(descText);

        final F2ButtonSprite summonButton = createSummonSprite(1);
        summonFrame.attachChild(summonButton);
        this.registerTouchArea(summonButton);

        final Sprite buttonSummonCharm = this.createACImageSprite(TextureEnum.COMMON_SUMMON_CHARM, 45, summonButton.getHeight() * 0.5f);
        summonButton.attachChild(buttonSummonCharm);
        final Text summonCostText = new Text(95, summonButton.getHeight() * 0.5f, descFont, "200", vbom);
        summonButton.attachChild(summonCostText);
        return summonFrame;
    }

    private Sprite createHeroSummonFrame() {
        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text titleText = new Text(95, heroSummonFrame.getHeight() - 35, titleFont, "英雄召唤", vbom);
        titleText.setColor(0XFFFAB103);
        heroSummonFrame.attachChild(titleText);

        final Sprite stars = this.createACImageSprite(TextureEnum.COMMON_STAR_3, 500, heroSummonFrame.getHeight() - 35);
        heroSummonFrame.attachChild(stars);
        final Text subTitleText = new Text(590, heroSummonFrame.getHeight() - 35, titleFont, "星以上", vbom);
        subTitleText.setColor(0XFFFAB103);
        heroSummonFrame.attachChild(subTitleText);

        final Font descFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 265);
        final Text descText = new Text(540, 170, descFont, "使用召唤石或钻石召唤一张卡片！钻石可在商店购买。", textOptions, vbom);
        this.topAlignEntity(descText, heroSummonFrame.getHeight() - 100);
        heroSummonFrame.attachChild(descText);
        refreshHeroSummonButton();
        return heroSummonFrame;
    }

    private void refreshHeroSummonButton() {
        final boolean summonStoneEnough = userProps.getSummonStone() >= CostConstants.HERO_SUMMON_STONE_COST;
        final F2ButtonSprite oldSummonButton = summonStoneEnough ? heroSummonDiamonButton : heroSummonStoneButton;
        final F2ButtonSprite newSummonButton = summonStoneEnough ? heroSummonStoneButton : heroSummonDiamonButton;
        unregisterTouchArea(oldSummonButton);
        registerTouchArea(newSummonButton);
        activity.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                oldSummonButton.detachSelf();
                heroSummonFrame.attachChild(newSummonButton);
            }
        });
    }

    private F2ButtonSprite createSummonSprite(final int type) {
        final F2ButtonSprite summonButton = this.createACF2ButtonSprite(TextureEnum.SUMMON_BUTTON, TextureEnum.SUMMON_BUTTON_FCS, 540, 55);
        summonButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (type == 1) {
                    if (userProps.getSummonCharm() < CostConstants.BASIC_SUMMON_COST) {
                        alert("召唤符不够！你可以通过野外探险或者打竞技场获得召唤符。");
                        return;
                    }
                } else if (type == 2) {
                    if (userProps.getSummonStone() < CostConstants.HERO_SUMMON_STONE_COST) {
                        alert("召唤石不够！");
                        return;
                    }
                } else if (type == 3) {
                    if (userProps.getDiamon() < CostConstants.HERO_SUMMON_DIAMON_COST) {
                        alert("钻石不够！");
                        return;
                    }
                }
                ResourceManager.getInstance().setChildScene(SummonScene.this, new IRCallback<BaseScene>() {

                    @Override
                    public BaseScene onCallback() {
                        final Card card = CardUtils.summon(activity, type);
                        if (type == 1) {
                            userProps.setSummonCharm(userProps.getSummonCharm() - CostConstants.BASIC_SUMMON_COST);
                            summonCharmText.setText(String.valueOf(userProps.getSummonCharm()));
                        } else if (type == 2) {
                            userProps.setSummonStone(userProps.getSummonStone() - CostConstants.HERO_SUMMON_STONE_COST);
                            summonStoneText.setText(String.valueOf(userProps.getSummonStone()));
                            refreshHeroSummonButton();
                        } else if (type == 3) {
                            userProps.setDiamon(userProps.getDiamon() - CostConstants.HERO_SUMMON_DIAMON_COST);
                            diamonText.setText(String.valueOf(userProps.getDiamon()));
                        }

                        try {
                            final BaseScene summonFinishScene = new SummonFinishScene(card, activity);
                            setChildScene(summonFinishScene, false, false, true);
                            summonFinishScene.updateScene();
                            CardUtils.refreshUserCards();
                            return summonFinishScene;
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                });

            }

        });
        return summonButton;
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}

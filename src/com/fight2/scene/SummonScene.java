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
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;

public class SummonScene extends BaseScene {
    private static final float FRAME_WIDTH = TextureEnum.SUMMON_FRAME.getWidth();
    private final Font font = ResourceManager.getInstance().getFont(FontEnum.Default, 24);
    private final Text summonCharmText;
    private final Text summonStoneText;
    private final Text diamonText;
    private final UserProperties userProps;

    public SummonScene(final GameActivity activity) throws IOException {
        super(activity);
        userProps = QuestUtils.getUserProperties(activity);
        GameUserSession.getInstance().setUserProps(userProps);
        this.summonCharmText = new Text(120, 45, font, String.valueOf(userProps.getSummonCharm()), 8, vbom);
        this.summonStoneText = new Text(315, 45, font, String.valueOf(userProps.getSummonStone()), 8, vbom);
        this.diamonText = new Text(123, 24, font, String.valueOf(userProps.getDiamon()), 8, vbom);
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
        final Sprite summonFrame = this.createALBImageSprite(TextureEnum.SUMMON_FRAME, this.simulatedLeftX + 100, 130);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text titleText = new Text(95, summonFrame.getHeight() - 35, titleFont, "英雄召唤", vbom);
        titleText.setColor(0XFFFAB103);
        summonFrame.attachChild(titleText);

        final Sprite stars = this.createACImageSprite(TextureEnum.COMMON_STAR_3, 500, summonFrame.getHeight() - 35);
        summonFrame.attachChild(stars);
        final Text subTitleText = new Text(590, summonFrame.getHeight() - 35, titleFont, "星以上", vbom);
        subTitleText.setColor(0XFFFAB103);
        summonFrame.attachChild(subTitleText);

        final Font descFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 265);
        final Text descText = new Text(540, 170, descFont, "使用召唤石或钻石召唤一张卡片！钻石可在商店购买。", textOptions, vbom);
        this.topAlignEntity(descText, summonFrame.getHeight() - 100);
        summonFrame.attachChild(descText);

        final F2ButtonSprite summonButton = createSummonSprite(2);
        summonFrame.attachChild(summonButton);
        this.registerTouchArea(summonButton);

        final Sprite buttonSummonStone = this.createACImageSprite(TextureEnum.COMMON_SUMMON_STONE, 40, summonButton.getHeight() * 0.5f);
        summonButton.attachChild(buttonSummonStone);
        final Text summonCostText = new Text(80, summonButton.getHeight() * 0.5f, descFont, "1", vbom);
        summonButton.attachChild(summonCostText);
        return summonFrame;
    }

    private F2ButtonSprite createSummonSprite(final int type) {
        final F2ButtonSprite summonButton = this.createACF2ButtonSprite(TextureEnum.SUMMON_BUTTON, TextureEnum.SUMMON_BUTTON_FCS, 540, 55);
        summonButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (type == 1) {
                    if (userProps.getSummonCharm() < CostConstants.BASIC_SUMMON_COST) {
                        alert("召唤符可从基本召唤中获得卡片。你可以通过野外探险或者打竞技场获得召唤符。");
                        return;
                    }
                } else {
                    if (userProps.getSummonStone() < CostConstants.HERO_SUMMON_STONE_COST) {
                        alert("召唤石不够！");
                        return;
                    }
                }
                final Card card = CardUtils.summon(activity, type);
                if (card != null) {
                    try {
                        final BaseScene summonFinishScene = new SummonFinishScene(card, activity);
                        setChildScene(summonFinishScene, false, false, true);
                        summonFinishScene.updateScene();
                        CardUtils.refreshUserCards();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    alert("可能服务器出错或者你召唤的卡片已经超过100张！");
                }

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

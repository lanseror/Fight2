package com.fight2.scene;

import java.io.IOException;
import java.util.List;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.ComboSkill;
import com.fight2.entity.engine.CardOutFrame;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class CardInfoScene extends BaseScene {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final static int CARD_WIDTH = 314;
    private final static int CARD_HEIGHT = 471;
    private final static int FRAME_BOTTOM = 100;

    private final Card card;

    public CardInfoScene(final GameActivity activity, final Card card) throws IOException {
        super(activity);
        this.card = card;
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                back();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final Sprite infoFrame = createALBImageSprite(TextureEnum.CARDINFO_FRAME, this.simulatedLeftX + 420, FRAME_BOTTOM);
        this.attachChild(infoFrame);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 34);
        final Text nameText = new Text(80, 426, titleFont, card.getName(), vbom);
        nameText.setColor(0XFFFAB103);
        this.leftAlignEntity(nameText, 30);
        infoFrame.attachChild(nameText);

        final Text levelText = new Text(400, 426, titleFont, "等级 " + card.getLevel(), vbom);
        this.leftAlignEntity(levelText, 370);
        infoFrame.attachChild(levelText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);

        final Sprite star = new Sprite(80, 373, getStarTexture(card), vbom);
        this.leftAlignEntity(star, 20);
        infoFrame.attachChild(star);

        final Text maxLevelText = new Text(250, 373, detailFont, "最高等级 ：" + CardUtils.getMaxLevel(card), vbom);
        this.leftAlignEntity(maxLevelText, 290);
        infoFrame.attachChild(maxLevelText);

        final Sprite line = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, infoFrame.getWidth() * 0.5f, 345);
        line.setScale(0.65f);
        infoFrame.attachChild(line);

        final Sprite hpIcon = this.createACImageSprite(TextureEnum.COMMON_HP_ICON, 50, 310);
        this.leftAlignEntity(hpIcon, 25);
        infoFrame.attachChild(hpIcon);
        final Text hpText = new Text(60, 310, detailFont, String.valueOf(card.getHp()), vbom);
        this.leftAlignEntity(hpText, 85);
        infoFrame.attachChild(hpText);

        final Sprite atkIcon = this.createACImageSprite(TextureEnum.COMMON_ATK_ICON, 250, 310);
        this.leftAlignEntity(atkIcon, 245);
        infoFrame.attachChild(atkIcon);
        final Text atkText = new Text(60, 310, detailFont, String.valueOf(card.getAtk()), vbom);
        this.leftAlignEntity(atkText, 305);
        infoFrame.attachChild(atkText);

        final float scale = 0.7f;
        final ITextureRegion tierGridTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.CARDINFO_TIER_GRID);
        final ITextureRegion tierStickTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.CARDINFO_TIER_STICK);
        final float tierGridX = 60;
        final float tierGridY = 270;

        final float tierGridWidth = tierGridTexture.getWidth() * scale;
        final float tierGridHeight = tierGridTexture.getHeight() * scale;
        final float tierStickWidth = tierStickTexture.getWidth() * scale;
        final float tierStickHeight = tierStickTexture.getHeight() * scale;
        final int tierGridAmount = CardUtils.getMaxEvoTier(card);
        final int tierStickAmount = card.getTier();
        for (int i = 0; i < tierGridAmount; i++) {
            final Sprite tierGridAdd = new Sprite(tierGridX + (tierGridWidth + 2) * i, tierGridY, tierGridWidth, tierGridHeight, tierGridTexture, vbom);
            infoFrame.attachChild(tierGridAdd);
            if (i < tierStickAmount) {
                final Sprite tierStickAdd = new Sprite(tierGridWidth * 0.5f, tierGridHeight * 0.5f, tierStickWidth, tierStickHeight, tierStickTexture, vbom);
                tierGridAdd.attachChild(tierStickAdd);
            }
        }

        final Sprite line2 = this.createACImageSprite(TextureEnum.GUILD_SCROLL_ROW_SEPARATOR, infoFrame.getWidth() * 0.5f, 245);
        line2.setScale(0.65f);
        infoFrame.attachChild(line2);

        final Font skillTitleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 31);
        final Text skillNameText = new Text(80, 220, skillTitleFont, card.getSkill(), vbom);
        this.leftAlignEntity(skillNameText, 25);
        infoFrame.attachChild(skillNameText);

        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 420);
        final Text skillEffectText = new Text(80, 170, detailFont, card.getSkillEffect(), textOptions, vbom);
        this.leftAlignEntity(skillEffectText, 25);
        this.topAlignEntity(skillEffectText, 190);
        infoFrame.attachChild(skillEffectText);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        activity.getGameHub().needSmallChatRoom(false);
        final CardOutFrame cardSprite = new CardOutFrame(this.simulatedLeftX + 108 + CARD_WIDTH * 0.5f, FRAME_BOTTOM + CARD_HEIGHT * 0.5f, CARD_WIDTH,
                CARD_HEIGHT, card, activity);
        this.attachChild(cardSprite);

        final float comboFrameHeight = TextureEnum.CARDINFO_COMBO_FRAME.getHeight();
        final Sprite comboFrame = createALBImageSprite(TextureEnum.CARDINFO_COMBO_FRAME, this.simulatedLeftX + 105, FRAME_BOTTOM - comboFrameHeight);
        this.attachChild(comboFrame);
        final List<ComboSkill> combos = CardUtils.getCardComboSkills(card, activity);
        float comboX = 40;
        for (final ComboSkill combo : combos) {
            final ITextureRegion iconTexture = TEXTURE_FACTORY.newTextureRegion(combo.getIcon());
            final Sprite iconSprite = new Sprite(comboX, comboFrameHeight * 0.5f, 90, 90, iconTexture, vbom) {
                @Override
                public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                    if (pSceneTouchEvent.isActionDown()) {
                        try {
                            final Scene comboSkillScene = new ComboSkillScene(activity, combo);
                            CardInfoScene.this.setChildScene(comboSkillScene, false, false, true);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;
                }
            };
            this.registerTouchArea(iconSprite);
            comboFrame.attachChild(iconSprite);
            comboX += 100;
        }
    }

    private ITextureRegion getStarTexture(final Card card) {
        ITextureRegion starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_1);
        switch (card.getStar()) {
            case 1:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_1);
                break;
            case 2:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_2);
                break;
            case 3:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_3);
                break;
            case 4:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_4);
                break;
            case 5:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_5);
                break;
            case 6:
                starTexture = TEXTURE_FACTORY.getAssetTextureRegion(TextureEnum.COMMON_STAR_6);
                break;
        }
        return starTexture;
    }

    @Override
    public void updateScene() {

    }

    @Override
    public void leaveScene() {
    }

}

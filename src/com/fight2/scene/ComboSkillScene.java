package com.fight2.scene;

import java.io.IOException;
import java.util.List;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SoundEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.ComboSkill;
import com.fight2.entity.engine.CardAvatar;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.F2SoundManager;
import com.fight2.util.ResourceManager;
import com.fight2.util.SkillUtils;
import com.fight2.util.TextureFactory;

public class ComboSkillScene extends BaseScene {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final ComboSkill combo;

    public ComboSkillScene(final GameActivity activity, final ComboSkill combo) throws IOException {
        super(activity);
        this.combo = combo;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.8f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                F2SoundManager.getInstance().play(SoundEnum.BUTTON_CLICK);
                back();
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final Sprite comboFrame = createACImageSprite(TextureEnum.COMBO_FRAME, this.cameraCenterX, this.cameraCenterY);
        this.attachChild(comboFrame);
        comboFrame.setAlpha(0.8f);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 34);
        final Text nameText = new Text(this.cameraCenterX, 500, titleFont, combo.getName(), vbom);
        nameText.setColor(0XFFF1AD26);
        this.attachChild(nameText);

        final ITextureRegion texture = TEXTURE_FACTORY.newTextureRegion(combo.getIcon());
        final Sprite iconSprite = new Sprite(this.cameraCenterX, 420, 60, 60, texture, vbom);
        this.attachChild(iconSprite);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 28);
        final Text descText = new Text(this.cameraCenterX, 350, detailFont, SkillUtils.getEffect(combo.getOperations()), vbom);
        descText.setColor(0XFFFBDE92);
        this.attachChild(descText);

        final List<Card> cards = combo.getCards();
        float avatarX = 390;
        for (final Card card : cards) {
            final CardAvatar avatar = new CardAvatar(avatarX, 245, 110, 110, card, activity);
            this.attachChild(avatar);
            avatarX += 130;
        }

        final Text tipsText = new Text(this.cameraCenterX, 150, detailFont, "将这些卡片合并至一个队伍，即可为你的队伍解锁此技能。", vbom);
        tipsText.setColor(0XFFF8FAF9);
        this.attachChild(tipsText);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
        activity.getGameHub().needSmallChatRoom(false);
    }

    @Override
    public void updateScene() {

    }

    @Override
    public void leaveScene() {
    }

}

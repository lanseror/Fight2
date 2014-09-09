package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ResourceManager;

public class BattleResultScene extends BaseScene {
    private final Font mFont;
    private final Font rateFont;
    private final Font totoalMightFont;
    private final Text resultText;
    private final Text aliveText;
    private final Text cwText;
    private final Text baseMightText;
    private final Text aliveMightText;
    private final Text cwMightText;
    private final Text cwRateText;
    private final Text totoalMightText;

    public BattleResultScene(final BattleResult battleResult, final GameActivity activity) throws IOException {
        super(activity);
        this.mFont = ResourceManager.getInstance().getFont(FontEnum.Default);
        totoalMightFont = ResourceManager.getInstance().getFont(FontEnum.Default, 36);
        rateFont = ResourceManager.getInstance().getFont(FontEnum.Default, 26);
        final String result = (battleResult.isWinner() ? "你击败了对手！" : "对手已将你击败！");
        resultText = new Text(245, 380, mFont, result, vbom);
        aliveText = new Text(280, 275, mFont, "队伍中所有团队均存活", vbom);
        cwText = new Text(250, 170, mFont, "连续胜利奖励倍数", vbom);
        baseMightText = new Text(600, 400, mFont, String.valueOf(battleResult.getBaseMight()), vbom);
        aliveMightText = new Text(600, 290, mFont, String.valueOf(battleResult.getAliveMight()), vbom);
        cwMightText = new Text(600, 190, mFont, String.valueOf(battleResult.getCwMight()), vbom);
        cwRateText = new Text(670, 190, rateFont, String.format("(+%s%%)", battleResult.getCwRate()), vbom);
        cwRateText.setColor(0XFF00FF0C);
        totoalMightText = new Text(610, 55, totoalMightFont, String.valueOf(battleResult.getTotalMight()), vbom);
        totoalMightText.setColor(0XFF00FF0C);

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.PARTY_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float frameY = this.simulatedHeight - TextureEnum.BATTLE_RESULT.getHeight();
        final Sprite frameSprite = createALBImageSprite(TextureEnum.BATTLE_RESULT, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        frameSprite.attachChild(resultText);
        frameSprite.attachChild(aliveText);
        frameSprite.attachChild(cwText);
        frameSprite.attachChild(cwRateText);
        frameSprite.attachChild(baseMightText);
        frameSprite.attachChild(aliveMightText);
        frameSprite.attachChild(cwMightText);
        frameSprite.attachChild(totoalMightText);

        final F2ButtonSprite confirmButton = createALBF2ButtonSprite(TextureEnum.COMMON_CONFIRM_BUTTON_NORMAL, TextureEnum.COMMON_CONFIRM_BUTTON_PRESSED,
                this.simulatedRightX - 135, 35);
        confirmButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Arena);
            }
        });
        this.attachChild(confirmButton);
        this.registerTouchArea(confirmButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
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

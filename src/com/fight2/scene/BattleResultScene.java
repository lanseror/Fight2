package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.ResourceManager;

public class BattleResultScene extends BaseScene {
    private final Font optionFont;
    private final Font descFont;
    private final Font rateFont;
    private final Font totoalMightFont;
    private final Text optionResultText;
    private final Text resultText;
    private final Text optionAliveText;
    private final Text aliveText;
    private final Text optionCwText;
    private final Text cwText;
    private final Text baseMightText;
    private final Text aliveMightText;
    private final Text cwMightText;
    private final Text cwRateText;
    private final Text totoalMightText;

    public BattleResultScene(final BattleResult battleResult, final GameActivity activity) throws IOException {
        super(activity);
        this.optionFont = ResourceManager.getInstance().newFont(FontEnum.Default);
        this.descFont = ResourceManager.getInstance().newFont(FontEnum.Default);
        totoalMightFont = ResourceManager.getInstance().newFont(FontEnum.Default, 36);
        rateFont = ResourceManager.getInstance().newFont(FontEnum.Default, 26);
        final String optionResult = (battleResult.isWinner() ? "胜利" : "失败");
        final String result = (battleResult.isWinner() ? "你击败了对手！" : "对手已将你击败！");
        optionResultText = new Text(160, 415, optionFont, optionResult, vbom);
        optionResultText.setColor(0XFFF6B453);
        leftAlignEntity(optionResultText, 75);
        resultText = new Text(200, 380, descFont, result, vbom);
        leftAlignEntity(resultText, 75);
        optionAliveText = new Text(220, 310, optionFont, "存活奖励", vbom);
        optionAliveText.setColor(0XFFF6B453);
        leftAlignEntity(optionAliveText, 75);
        aliveText = new Text(240, 275, descFont, "队伍中所有团队均存活", vbom);
        leftAlignEntity(aliveText, 75);
        optionCwText = new Text(190, 205, optionFont, "连续胜利", vbom);
        optionCwText.setColor(0XFFF6B453);
        leftAlignEntity(optionCwText, 75);
        cwText = new Text(210, 170, descFont, "连续胜利奖励倍数", vbom);
        leftAlignEntity(cwText, 75);
        baseMightText = new Text(560, 400, descFont, String.valueOf(battleResult.getBaseMight()), vbom);
        aliveMightText = new Text(560, 290, descFont, String.valueOf(battleResult.getAliveMight()), vbom);
        cwMightText = new Text(560, 190, descFont, String.valueOf(battleResult.getCwMight()), vbom);
        cwRateText = new Text(630, 190, rateFont, String.format("(+%s%%)", battleResult.getCwRate()), vbom);
        cwRateText.setColor(0XFF00FF0C);
        totoalMightText = new Text(570, 55, totoalMightFont, String.valueOf(battleResult.getTotalMight()), vbom);
        totoalMightText.setColor(0XFF00FF0C);

        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final float frameY = this.simulatedHeight - TextureEnum.BATTLE_RESULT.getHeight();
        final Sprite frameSprite = createALBImageSprite(TextureEnum.BATTLE_RESULT, this.simulatedLeftX, frameY);
        this.attachChild(frameSprite);

        frameSprite.attachChild(optionResultText);
        frameSprite.attachChild(resultText);
        frameSprite.attachChild(optionAliveText);
        frameSprite.attachChild(aliveText);
        frameSprite.attachChild(optionCwText);
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
                ResourceManager.getInstance().unManagedSceneBack();
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

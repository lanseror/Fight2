package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;

import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.CardUtils;
import com.fight2.util.ResourceManager;

public class SummonScene extends BaseScene {

    public SummonScene(final GameActivity activity) throws IOException {
        super(activity);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final Sprite summonFrame = this.createALBImageSprite(TextureEnum.SUMMON_FRAME, this.simulatedLeftX + 100, 130);
        this.attachChild(summonFrame);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final F2ButtonSprite summonButton = createSummonSprite();
        summonFrame.attachChild(summonButton);
        this.registerTouchArea(summonButton);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    private F2ButtonSprite createSummonSprite() {
        final F2ButtonSprite summonButton = this.createACF2ButtonSprite(TextureEnum.SUMMON_BUTTON, TextureEnum.SUMMON_BUTTON_FCS, 620 * 0.9f, 60 * 0.9f);
        summonButton.setOnClickListener(new F2OnClickListener() {

            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                final Card card = CardUtils.summon(activity);
                if (card != null) {
                    try {
                        final BaseScene summonFinishScene = new SummonFinishScene(card, activity);
                        activity.getEngine().setScene(summonFinishScene);
                        summonFinishScene.updateScene();
                        CardUtils.refreshUserCards();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "可能服务器出错或者你召唤的卡片已经超过100张！", Toast.LENGTH_SHORT).show();
                        }
                    });
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

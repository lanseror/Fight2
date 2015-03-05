package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.Dialog;
import com.fight2.entity.Dialog.Speaker;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.User;
import com.fight2.entity.battle.BattleType;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.entity.quest.QuestResult;
import com.fight2.util.CardUtils;
import com.fight2.util.IParamCallback;
import com.fight2.util.IRCallback;
import com.fight2.util.ResourceManager;

public class QuestEnemyScene extends BaseScene {
    private final QuestResult questResult;

    public QuestEnemyScene(final GameActivity activity, final QuestResult questResult) throws IOException {
        super(activity);
        this.questResult = questResult;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.3f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Dialog dialog = questResult.getDialog();
        final User enemy = questResult.getEnemy();
        final PartyInfo dialogPartyInfo;
        if (dialog.getSpeaker() == Speaker.Self) {
            dialogPartyInfo = GameUserSession.getInstance().getPartyInfo();
        } else {
            dialogPartyInfo = CardUtils.getPartyByUserId(activity, enemy.getId());
        }
        final Card dialogLeader = dialogPartyInfo.getParties()[0].getCards()[0];
        final String speakerName;
        if (dialog.getSpeaker() == Speaker.Self) {
            speakerName = dialogLeader.getName();
        } else {
            speakerName = enemy.getName();
        }

        final DialogFrame dialogFrame = new HeroDialogFrame(cameraCenterX, cameraCenterY, 600, 300, activity, dialogLeader, speakerName, dialog.getContent());
        dialogFrame.bind(QuestEnemyScene.this, new IParamCallback() {
            @Override
            public void onCallback(final Object param) {
                dialogFrame.unbind(QuestEnemyScene.this);
                final BaseScene scene = ResourceManager.getInstance().getCurrentScene();
                scene.clearChildScene();
                ResourceManager.getInstance().setChildScene(scene, new IRCallback<BaseScene>() {
                    @Override
                    public BaseScene onCallback() {
                        try {
                            return new PreBattleScene(activity, enemy, BattleType.Quest);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, true);
            }
        });
    }

    @Override
    public void updateScene() {
        // TODO Auto-generated method stub
    }

    @Override
    public void leaveScene() {
        // TODO Auto-generated method stub

    }

}

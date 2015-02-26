package com.fight2.scene;

import java.io.IOException;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.util.IParamCallback;
import com.fight2.util.ResourceManager;
import com.fight2.util.TaskUtils;

public class QuestBattleResultScene extends BaseScene {
    private final PartyInfo myPartyInfo = GameUserSession.getInstance().getPartyInfo();
    private final Party[] myParties = myPartyInfo.getParties();
    private final BattleResult battleResult;

    public QuestBattleResultScene(final BattleResult battleResult, final GameActivity activity) throws IOException {
        super(activity);
        this.setBackgroundEnabled(false);
        this.battleResult = battleResult;
        init();
    }

    @Override
    protected void init() throws IOException {
        final Card myLeader = myParties[0].getCards()[0];
        String dialog = null;
        if (battleResult.isWinner()) {
            dialog = "战斗胜利！";
            final QuestTask task = TaskUtils.getTask();
            task.setStatus(UserTaskStatus.Finished);
        } else {
            dialog = "战斗失败！";
        }
        final DialogFrame dialogFrame = new HeroDialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity, myLeader, dialog);
        dialogFrame.bind(this, new IParamCallback() {
            @Override
            public void onCallback(final Object param) {
                back();
                ResourceManager.getInstance().getCurrentScene().updateScene();
            }
        });

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

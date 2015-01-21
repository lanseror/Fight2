package com.fight2.scene;

import java.io.IOException;

import com.fight2.GameActivity;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.battle.BattleResult;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.util.ICallback;

public class TaskBattleResultScene extends BaseScene {
    private final PartyInfo myPartyInfo = GameUserSession.getInstance().getPartyInfo();
    private final Party[] myParties = myPartyInfo.getParties();
    private final BattleResult battleResult;

    public TaskBattleResultScene(final BattleResult battleResult, final GameActivity activity) throws IOException {
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
            dialog = "任务完成了！";
        } else {
            dialog = "我们的力量还不够，加强卡牌再来试试吧！";
        }
        final DialogFrame dialogFrame = new HeroDialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity, myLeader, dialog);
        dialogFrame.bind(this, new ICallback() {
            @Override
            public void onCallback() {
                back();
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

package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.Party;
import com.fight2.entity.PartyInfo;
import com.fight2.entity.QuestTask;
import com.fight2.entity.engine.HeroDialogFrame;
import com.fight2.util.ICallback;
import com.fight2.util.TaskUtils;
import com.fight2.util.TextureFactory;

public class TaskGuideScene extends BaseScene {
    private static final TextureFactory TEXTURE_FACTORY = TextureFactory.getInstance();
    private final PartyInfo myPartyInfo = GameUserSession.getInstance().getPartyInfo();
    private final Party[] myParties = myPartyInfo.getParties();
    private final ICallback iLeaveCallback;

    public TaskGuideScene(final GameActivity activity, final ICallback iLeaveCallback) throws IOException {
        super(activity);
        this.iLeaveCallback = iLeaveCallback;
        init();
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.3f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final Sprite msgSprite = this.createALBImageSprite(TextureEnum.MAIN_MSG, 45, 0);
        this.attachChild(msgSprite);
        final Card myLeader = myParties[0].getCards()[0];
        final QuestTask task = TaskUtils.getTask();
        final HeroDialogFrame dialog = new HeroDialogFrame(715, cameraCenterY - 45, 540, 400, activity, myLeader, task.getDialog());
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                iLeaveCallback.onCallback();
                back();
            }

        });

    }

    @Override
    public void updateScene() {
    }

    @Override
    public void leaveScene() {
    }

}

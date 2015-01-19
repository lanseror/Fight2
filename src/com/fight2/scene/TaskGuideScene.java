package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.QuestTask;
import com.fight2.entity.QuestTask.UserTaskStatus;
import com.fight2.entity.engine.CommonDialogFrame;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.entity.engine.TextDialogFrame;
import com.fight2.util.ICallback;
import com.fight2.util.TaskUtils;

public class TaskGuideScene extends BaseScene {
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
        final QuestTask task = TaskUtils.getTask();
        if (task.getStatus() == UserTaskStatus.Ready) {
            createDialogFrame(task);
        } else if (task.getStatus() == UserTaskStatus.Started) {
            createTipsFrame(task);
        }

    }

    @Override
    public void updateScene() {
    }

    @Override
    public void leaveScene() {
    }

    private void createDialogFrame(final QuestTask task) {
        final DialogFrame dialog = new TextDialogFrame(715, cameraCenterY - 45, 540, 360, activity, task.getDialog());
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                if (TaskUtils.accept()) {
                    dialog.unbind(TaskGuideScene.this);
                    task.setStatus(UserTaskStatus.Started);
                    createTipsFrame(task);
                }
            }

        });
    }

    private void createTipsFrame(final QuestTask task) {
        final DialogFrame dialog = new CommonDialogFrame(715, cameraCenterY - 45, 540, 360, activity, "任务：" + task.getTitle(), task.getTips());
        dialog.bind(this, new ICallback() {

            @Override
            public void onCallback() {
                iLeaveCallback.onCallback();
                back();
            }

        });
    }
}

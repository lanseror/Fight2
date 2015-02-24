package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.Color;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.entity.GameMine;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.UserProperties;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.util.IParamCallback;
import com.fight2.util.MineUtils;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;

public class MineGatherScene extends BaseScene {
    private final IParamCallback yesCallback;
    private final GameMine mine;

    public MineGatherScene(final GameActivity activity, final GameMine mine, final IParamCallback yesCallback) throws IOException {
        super(activity);
        this.yesCallback = yesCallback;
        this.mine = mine;
        init();
        if (mine.getAmount() > 0) {
            if (MineUtils.gather(activity) == 0) {
                mine.setAmount(0);
                final UserProperties userProps = QuestUtils.getUserProperties(activity);
                GameUserSession.getInstance().setUserProps(userProps);
            }
        }
    }

    @Override
    protected void init() throws IOException {
        final IEntity bgEntity = new Rectangle(cameraCenterX, cameraCenterY, this.simulatedWidth, this.simulatedHeight, vbom);
        bgEntity.setColor(Color.BLACK);
        bgEntity.setAlpha(0.3f);
        this.setBackgroundEnabled(false);
        this.attachChild(bgEntity);

        final DialogFrame frame = new DialogFrame(cameraCenterX, cameraCenterY, 600, 350, activity);
        frame.bind(this, new IParamCallback() {

            @Override
            public void onCallback(final Object param) {
                back();
                yesCallback.onCallback(param);
            }

        });

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text itemText = new Text(frame.getWidth() * 0.5f, 300, titleFont, String.format("%s矿场", mine.getType().getDesc()), vbom);
        itemText.setColor(0XFF330504);
        frame.attachChild(itemText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 460);
        if (mine.getAmount() > 0) {
            final Text descText = new Text(frame.getWidth() * 0.5f, 230, detailFont, "矿场的守卫说道：“大人，我卖力工作，才为您赚到这些资源。请迟点再来吧！”", textOptions, vbom);
            descText.setColor(0XFF330504);
            frame.attachChild(descText);
            final TextOptions tipTextOptions = new TextOptions(AutoWrap.LETTERS, 380);
            final Text tipText = new Text(frame.getWidth() * 0.5f, 120, detailFont, String.format("收集到%s%s，这些资源会兑换为你的公会贡献值！", mine.getAmount(), mine.getType()
                    .getDesc()), tipTextOptions, vbom);
            tipText.setColor(0XFF330504);
            frame.attachChild(tipText);
        } else {
            final Text descText = new Text(frame.getWidth() * 0.5f, 230, detailFont, "矿场的守卫说道：“大人，很抱歉，现在还没有生产出资源。请迟点再来吧！”", textOptions, vbom);
            descText.setColor(0XFF330504);
            frame.attachChild(descText);
        }

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

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
import com.fight2.entity.User;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IParamCallback;
import com.fight2.util.MineUtils;
import com.fight2.util.ResourceManager;

public class MineAttackScene extends BaseScene {
    private final IParamCallback yesCallback;
    private final GameMine mine;

    public MineAttackScene(final GameActivity activity, final GameMine mine, final IParamCallback yesCallback) throws IOException {
        super(activity);
        this.yesCallback = yesCallback;
        this.mine = mine;
        init();
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
                final Boolean isConfirmed = (Boolean) param;
                if (isConfirmed) {
                    try {
                        final LoadingScene loadingScene = new LoadingScene(activity);
                        setChildScene(loadingScene, false, false, true);

                        final IAsyncCallback callback = new IAsyncCallback() {
                            private User owner;

                            @Override
                            public void workToDo() {
                                owner = MineUtils.getOwner();
                            }

                            @Override
                            public void onComplete() {
                                loadingScene.back();
                                back();
                                yesCallback.onCallback(owner);
                            }
                        };
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AsyncTaskLoader().execute(callback);
                            }
                        });
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    back();
                    yesCallback.onCallback(null);
                }
            }

        });

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text itemText = new Text(frame.getWidth() * 0.5f, 270, titleFont, String.format("%s矿场，存量%s", mine.getType().getDesc(), mine.getAmount()), vbom);
        itemText.setColor(0XFF330504);
        frame.attachChild(itemText);
        
        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 390);
        final Text descText = new Text(frame.getWidth() * 0.5f, 200, detailFont, "你要花费2颗钻石攻打这个矿点吗？", textOptions, vbom);
        descText.setColor(0XFF330504);
        frame.attachChild(descText);
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

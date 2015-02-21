package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.adt.color.ColorUtils;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.entity.User;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IParamCallback;
import com.fight2.util.MineUtils;
import com.fight2.util.ResourceManager;

public class AttackMineScene extends BaseScene {
    private final IParamCallback yesCallback;

    public AttackMineScene(final GameActivity activity, final IParamCallback yesCallback) throws IOException {
        super(activity);
        this.yesCallback = yesCallback;
        init();
    }

    @Override
    protected void init() throws IOException {
        this.getBackground().setColor(ColorUtils.convertABGRPackedIntToColor(0X55000000));

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
                                yesCallback.onCallback(owner);
                                back();
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
                    yesCallback.onCallback(null);
                    back();
                }
            }

        });

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text itemText = new Text(255, 280, titleFont, "你要花费2颗钻石攻打这个矿点吗？", vbom);
        itemText.setColor(0XFF330504);
        frame.attachChild(itemText);
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

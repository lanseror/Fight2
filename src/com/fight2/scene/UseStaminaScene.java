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
import com.fight2.constant.TextureEnum;
import com.fight2.entity.GameUserSession;
import com.fight2.entity.UserStoreroom;
import com.fight2.entity.engine.DialogFrame;
import com.fight2.util.AsyncTaskLoader;
import com.fight2.util.IAsyncCallback;
import com.fight2.util.IParamCallback;
import com.fight2.util.QuestUtils;
import com.fight2.util.ResourceManager;

public class UseStaminaScene extends BaseScene {
    private final IParamCallback yesCallback;

    public UseStaminaScene(final GameActivity activity, final IParamCallback yesCallback) throws IOException {
        super(activity);
        this.yesCallback = yesCallback;
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
        frame.setConfirmButtonText("使用");
        frame.bind(this, new IParamCallback() {

            @Override
            public void onCallback(final Object param) {
                final Boolean isConfirmed = (Boolean) param;
                if (isConfirmed) {
                    try {
                        final LoadingScene loadingScene = new LoadingScene(activity);
                        setChildScene(loadingScene, false, false, true);
                        
                        final IAsyncCallback callback = new IAsyncCallback() {
                            private boolean isServerOk;

                            @Override
                            public void workToDo() {
                                isServerOk = QuestUtils.useStaminaBottle(activity);
                            }

                            @Override
                            public void onComplete() {
                                loadingScene.back();
                                if (isServerOk) {
                                    yesCallback.onCallback(true);
                                    final UserStoreroom storeroom = GameUserSession.getInstance().getStoreroom();
                                    storeroom.setStamina(storeroom.getStamina() - 1);
                                } else {
                                    yesCallback.onCallback(false);
                                }
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
                    yesCallback.onCallback(false);
                    back();
                }

            }

        });

        final TextureEnum itemGridEnum = TextureEnum.ARENA_REWARD_ITEM_GRID;
        final IEntity staminaGrid = createALBImageSprite(itemGridEnum, 45, 160);
        final IEntity staminaImg = createACImageSprite(TextureEnum.COMMON_STAMINA, itemGridEnum.getWidth() * 0.5f, itemGridEnum.getHeight() * 0.5f - 5);
        staminaGrid.attachChild(staminaImg);
        frame.attachChild(staminaGrid);

        final Font titleFont = ResourceManager.getInstance().newFont(FontEnum.Default, 30);
        final Text itemText = new Text(255, 280, titleFont, "精力药水", vbom);
        itemText.setColor(0XFF330504);
        frame.attachChild(itemText);

        final Font detailFont = ResourceManager.getInstance().newFont(FontEnum.Default, 24);
        final TextOptions textOptions = new TextOptions(AutoWrap.LETTERS, 390);
        final Text descText = new Text(390, 220, detailFont, "精力药水可以完全恢复你的精力，让你可以继续探索", textOptions, vbom);
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

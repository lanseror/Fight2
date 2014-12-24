package com.fight2.scene;

import java.io.IOException;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.region.ITextureRegion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.fight2.GameActivity;
import com.fight2.constant.FontEnum;
import com.fight2.constant.SceneEnum;
import com.fight2.constant.TextureEnum;
import com.fight2.entity.Card;
import com.fight2.entity.engine.F2ButtonSprite;
import com.fight2.entity.engine.F2ButtonSprite.F2OnClickListener;
import com.fight2.util.AccountUtils;
import com.fight2.util.ResourceManager;
import com.fight2.util.TextureFactory;

public class PlayerInfoScene extends BaseScene {
    private final static int CARD_WIDTH = 310;
    private final static int CARD_HEIGHT = 465;
    private final static int FRAME_BOTTOM = 80;
    private final Text nameTitleText;
    private final Text nameText;
    private final TextureFactory textureFactory = TextureFactory.getInstance();
    private Sprite cardSprite;

    public PlayerInfoScene(final GameActivity activity) throws IOException {
        super(activity);
        final Font font = ResourceManager.getInstance().newFont(FontEnum.Default);
        nameTitleText = new Text(80, 426, font, "名称：", vbom);
        nameText = new Text(265, 426, font, session.getName(), 30, vbom);
        init();
    }

    @Override
    protected void init() throws IOException {
        final Sprite bgSprite = createALBImageSprite(TextureEnum.COMMON_BG, 0, 0);
        final Background background = new SpriteBackground(bgSprite);
        this.setBackground(background);

        final Sprite experienceBox = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX, this.simulatedLeftX + 20, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight());
        this.attachChild(experienceBox);
        final Sprite experienceStick = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_STICK, 52, 0);
        experienceBox.attachChild(experienceStick);
        final Sprite experienceBoxStar = createALBImageSprite(TextureEnum.COMMON_EXPERIENCE_BOX_STAR, this.simulatedLeftX + 20, this.simulatedHeight
                - TextureEnum.COMMON_EXPERIENCE_BOX.getHeight());
        this.attachChild(experienceBoxStar);

        final Sprite staminaBox = createALBImageSprite(TextureEnum.COMMON_STAMINA_BOX, this.simulatedLeftX + 320, this.simulatedHeight
                - TextureEnum.COMMON_STAMINA_BOX.getHeight());
        this.attachChild(staminaBox);
        final Sprite staminaStick = createALBImageSprite(TextureEnum.COMMON_STAMINA_STICK, 56, 11);
        staminaBox.attachChild(staminaStick);

        final Sprite rechargeSprite = createALBF2ButtonSprite(TextureEnum.PARTY_RECHARGE, TextureEnum.PARTY_RECHARGE_PRESSED, this.simulatedRightX
                - TextureEnum.PARTY_RECHARGE.getWidth() - 8, cameraHeight - TextureEnum.PARTY_RECHARGE.getHeight() - 4);
        this.attachChild(rechargeSprite);
        this.registerTouchArea(rechargeSprite);

        final F2ButtonSprite backButton = createALBF2ButtonSprite(TextureEnum.COMMON_BACK_BUTTON_NORMAL, TextureEnum.COMMON_BACK_BUTTON_PRESSED,
                this.simulatedRightX - 135, 50);
        backButton.setOnClickListener(new F2OnClickListener() {
            @Override
            public void onClick(final Sprite pButtonSprite, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (!session.getName().equals(nameText.getText().toString())) {
                    session.setName(nameText.getText().toString());
                    final boolean isSaveOk = AccountUtils.saveUserInfo();
                    if (isSaveOk) {
                        ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "用户信息保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ResourceManager.getInstance().setCurrentScene(SceneEnum.Main);
                }

            }
        });
        this.attachChild(backButton);
        this.registerTouchArea(backButton);

        final Sprite infoFrame = createALBImageSprite(TextureEnum.PLAYERINFO_FRAME, this.simulatedLeftX + 350, FRAME_BOTTOM);
        this.attachChild(infoFrame);
        infoFrame.attachChild(nameTitleText);
        infoFrame.attachChild(nameText);

        final IEntity nameTouchArea = new Rectangle(450, 430, 70, 50, vbom) {
            @Override
            public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    showTextInput();
                    return true;
                }
                return false;
            }
        };
        nameTouchArea.setAlpha(0);
        infoFrame.attachChild(nameTouchArea);
        this.registerTouchArea(nameTouchArea);

        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setTouchAreaBindingOnActionMoveEnabled(true);
    }

    @Override
    public void updateScene() {
        activity.getGameHub().needSmallChatRoom(false);
        final Card avatarCard = session.getPartyInfo().getParties()[0].getCards()[0];
        final ITextureRegion texture = textureFactory.newTextureRegion(avatarCard.getImage());
        if (cardSprite != null) {
            cardSprite.detachSelf();
        }
        cardSprite = new Sprite(this.simulatedLeftX + 30 + CARD_WIDTH * 0.5f, FRAME_BOTTOM + CARD_HEIGHT * 0.5f, CARD_WIDTH, CARD_HEIGHT, texture, vbom);
        this.attachChild(cardSprite);
    }

    @Override
    public void leaveScene() {
    }

    public void showTextInput() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("输入名称");
                // alert.setMessage("信息信息信息信息信息信息信息信息");
                final EditText editText = new EditText(activity);
                editText.setTextSize(15f);
                editText.setText(nameText.getText());
                editText.setSelection(nameText.getText().length());
                editText.setGravity(Gravity.CENTER_HORIZONTAL);
                alert.setView(editText);
                alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {
                        nameText.setText(editText.getText());
                    }
                });

                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int whichButton) {

                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        editText.requestFocus();
                        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                dialog.show();
            }
        });
    }

}
